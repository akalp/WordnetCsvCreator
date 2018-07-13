import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.data.list.PointerTargetNode;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.list.PointerTargetTreeNode;
import net.sf.extjwnl.data.list.PointerTargetTreeNodeList;
import net.sf.extjwnl.dictionary.Dictionary;
import org.apache.commons.csv.*;

import java.io.*;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    private static HashSet<IGRAPHNode> igraphNodes;
    private static HashSet<Relationship> relationships;
    private static ConcurrentHashMap<Synset, WordnetNode> addedSynsets;
    private static Dictionary dictionary;

    public static void main(String[] args) {
        igraphNodes = new HashSet<>();
        relationships = new HashSet<>();
        addedSynsets = new ConcurrentHashMap<>();

        try {
            dictionary = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            System.err.println("Dictionary creation fault in main");
        }

        System.out.println("Reading igraph csv");
        readIGRAPHcsv();
        System.out.println("Creating hyp, lemma, antonym");
        while (createHyp() || createLemmas() || createAntonyms()){
            System.out.println("added new things");
        }
        System.out.println("Creating csv files");
        createCSV();

    }

    private static void readIGRAPHcsv(){
        CSVParser csvParser = null;
        try{
            csvParser = new CSVParser(new BufferedReader(new FileReader(new File("igraph.csv"))), CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim()
            );

            for(CSVRecord csvRecord: csvParser.getRecords()){
                IGRAPHNode igraphNode = new IGRAPHNode(csvRecord.get("word"), csvRecord.get("lang"));
                igraphNodes.add(igraphNode);
                createSynsets(igraphNode.getId(), csvRecord.get("synsets"));
            }
        }catch (IOException e){
            System.err.println("IOException in createNodesFromIGRAPH");
        }
    }
    
    private static String getSynsetID(Synset synset){
        return synset.getOffset()+"-"+synset.getPOS().getKey();
    }

    private static void createSynsets(String fromID, String synsets) {
        if(!synsets.equals("None")){
            String[] synsetIDs = synsets.split(" ");
            for(String synsetId: synsetIDs){
                Synset synset = getSynset(synsetId);
                if(synset != null){
                    WordnetNode wordnetNode = null;
                    if(!addedSynsets.keySet().contains(synset)) {
                        wordnetNode = new WordnetNode(getSynsetID(synset), getName(synset), "eng", synset.getGloss());
                        addedSynsets.put(synset, wordnetNode);
                    }else{
                        wordnetNode = addedSynsets.get(synset);
                    }
                    relationships.add(new Relationship(fromID, wordnetNode.getId(), "Synset"));
                }
            }
        }
    }

    private static boolean createLemmas(){
        boolean addednew = false;
        for(Synset s: addedSynsets.keySet()){
            for(Word w: s.getWords()){
                Synset s1 = w.getSynset();
                if(s1 != s){
                    WordnetNode wordnetNode = null;
                    if(!addedSynsets.keySet().contains(s1)){
                        wordnetNode = new WordnetNode(getSynsetID(s1), getName(s1), "eng", s1.getGloss());
                        addedSynsets.put(s1, wordnetNode);
                        addednew = true;
                    }else{
                        wordnetNode = addedSynsets.get(s1);
                    }
                    relationships.add(new Relationship(addedSynsets.get(s).getId(), wordnetNode.getId(), "Lemma"));
                }
            }
        }
        return addednew;
    }

    private static boolean createAntonyms(){
        boolean addednew = false;
        for(Synset s: addedSynsets.keySet()){
            try {
                PointerTargetNodeList antonyms = PointerUtils.getAntonyms(s);
                for(PointerTargetNode ptn: antonyms){
                    Synset s1 = ptn.getSynset();
                    WordnetNode wordnetNode = null;
                    if(!addedSynsets.keySet().contains(s1)){
                        wordnetNode = new WordnetNode(getSynsetID(s1), getName(s1), "eng", s1.getGloss());
                        addedSynsets.put(s1, wordnetNode);
                        addednew = true;
                    }else{
                        wordnetNode = addedSynsets.get(s1);
                    }
                    relationships.add(new Relationship(addedSynsets.get(s).getId(), wordnetNode.getId(), "Antonym"));
                }
            } catch (JWNLException e) {
                e.printStackTrace();
            }
        }
        return addednew;
    }

    private static POS getPosFromString(String id) {
        return POS.getPOSForKey(id.split("-")[1]);
    }

    private static int getOffsetFromString(String id) {
        return Integer.parseInt(id.split("-")[0]);
    }

    private static String getName(Synset synset) {
        return synset.getWords().get(0).getLemma();
    }

    private static String getTypeFromSynset(Synset synset){
        String type = synset.getPOS().getLabel();
        return type.substring(0,1).toUpperCase()+type.substring(1);
    }

    private static Synset getSynset(String id) {
        try {
            return dictionary.getSynsetAt(getPosFromString(id), getOffsetFromString(id));
        } catch (Exception e) {
            return null;
        }
    }


    private static boolean createHyp(){
        Boolean addednew = new Boolean(false);
        for (Synset synset : addedSynsets.keySet()) {
            try {
                hypHelper(PointerUtils.getHyponymTree(synset).getRootNode(), addedSynsets.get(synset).getId(), "Hyponym", addednew);
                hypHelper(PointerUtils.getHypernymTree(synset).getRootNode(), addedSynsets.get(synset).getId(), "Hypernym", addednew);
            } catch (JWNLException e) {
                System.err.println("Cant call hypHelper with " + getName(synset) + " " + getTypeFromSynset(synset));
            }
        }
        return addednew;
    }

    private static void hypHelper(PointerTargetTreeNode root, String rootID, String relation, Boolean addednew){
        PointerTargetTreeNodeList pointerTargetTreeNodes = root.getChildTreeList();
        if(pointerTargetTreeNodes != null){
            for(PointerTargetTreeNode pointerTargetTreeNode: pointerTargetTreeNodes){
                WordnetNode wordnetNode = null;
                if(!addedSynsets.keySet().contains(pointerTargetTreeNode.getSynset())) {
                    wordnetNode = new WordnetNode(getSynsetID(pointerTargetTreeNode.getSynset()),
                            getName(pointerTargetTreeNode.getSynset()),
                            "eng",
                            pointerTargetTreeNode.getSynset().getGloss());
                    addedSynsets.put(pointerTargetTreeNode.getSynset(), wordnetNode);
                    addednew = true;
                }else{
                    wordnetNode = addedSynsets.get(pointerTargetTreeNode.getSynset());
                }

                if(relation.equals("Hyponym")) {
                    relationships.add(new Relationship(rootID, wordnetNode.getId(), relation));
                    relationships.add(new Relationship(wordnetNode.getId(), rootID, "Hypernym"));
                }else {
                    relationships.add(new Relationship(rootID, wordnetNode.getId(), relation));
                }
                hypHelper(pointerTargetTreeNode, addedSynsets.get(pointerTargetTreeNode.getSynset()).getId(), relation, addednew);
            }
        }
    }

    private static void createCSV(){
        CSVPrinter igraph = null;
        CSVPrinter wordnet = null;
        CSVPrinter definition = null;
        CSVPrinter relation = null;

        try {
            igraph = new CSVPrinter(new BufferedWriter(new FileWriter(new File("igraph_nodes.csv"))), CSVFormat.DEFAULT);
            wordnet = new CSVPrinter(new BufferedWriter(new FileWriter(new File("synset_nodes.csv"))), CSVFormat.DEFAULT);
            definition = new CSVPrinter(new BufferedWriter(new FileWriter(new File("definition.csv"))), CSVFormat.DEFAULT);
            relation = new CSVPrinter(new BufferedWriter(new FileWriter(new File("relationship.csv"))), CSVFormat.DEFAULT);

            for(IGRAPHNode igraphNode: igraphNodes){
                igraph.printRecord(igraphNode.getId(),igraphNode.getName(),igraphNode.getLang(), "Wordnet");
            }

            for(Synset synset: addedSynsets.keySet()){
                WordnetNode wordnetNode = addedSynsets.get(synset);

                wordnet.printRecord(wordnetNode.getId(), wordnetNode.getName(), wordnetNode.getLang(), "Wordnet;"+getTypeFromSynset(synset));
                definition.printRecord(wordnetNode.getId(), wordnetNode.getDefinition());
            }

            for(Relationship relationship: relationships){
                relation.printRecord(relationship.getFrom(), relationship.getTo(), relationship.getType());
            }


        }catch (IOException e){
            e.printStackTrace();
        }

        try {
            if (igraph != null) igraph.close();
            if (wordnet != null) wordnet.close();
            if (definition != null) definition.close();
            if (relation != null) relation.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}
