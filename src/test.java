import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.list.PointerTargetTree;
import net.sf.extjwnl.data.list.PointerTargetTreeNodeList;
import net.sf.extjwnl.dictionary.Dictionary;

public class test {
    public static void main(String[] args) throws JWNLException {
        Dictionary dictionary = Dictionary.getDefaultResourceInstance();
        Synset s = dictionary.getIndexWord(POS.ADJECTIVE, "good").getSenses().get(0);
        System.out.println("offset: "+s.getOffset());
        System.out.println("index: "+s.getIndex());
        System.out.println("key: "+s.getKey());
        System.out.println("type: "+s.getType()+"\ttypename: "+s.getType().getName());
        System.out.println("posid: "+s.getPOS().getId()+"\tposkey: "+s.getPOS().getKey());

        System.out.println(dictionary.getSynsetAt(POS.getPOSForKey("a"), 1123148));
    }
}
