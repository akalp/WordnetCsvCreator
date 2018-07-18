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
        Synset s = dictionary.getIndexWord(POS.NOUN, "accelerator").getSenses().get(0);
        System.out.println("Meronyms");
        PointerUtils.getMeronyms(s).print();
        System.out.println("\nPart Meronyms");
        PointerUtils.getPartMeronyms(s).print();
        System.out.println("\nMember Meronyms");
        PointerUtils.getMemberMeronyms(s).print();
        System.out.println("\nInherited Meronyms");
        PointerUtils.getInheritedMeronyms(s).print();
        System.out.println("\nInherited Part Meronyms");
        PointerUtils.getInheritedPartMeronyms(s).print();
        System.out.println("\nInherited Member Meronyms");
        PointerUtils.getInheritedMemberMeronyms(s).print();
        System.out.println("\nHolonyms");
        PointerUtils.getHolonyms(s);
    }
}
