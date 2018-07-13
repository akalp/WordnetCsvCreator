public class IGRAPHNode {
    private String id;
    private String name;
    private String lang;
    private static int count;

    public IGRAPHNode(String name, String lang){
        this.id = "i" + count;
        this.name = name;
        this.lang = lang;
        count++;
    }

    public String getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLang() {
        return lang;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof IGRAPHNode)) return false;
        IGRAPHNode i = (IGRAPHNode) o;
        return id.equals(i.getId()) && name.equals(i.getName()) && lang.equals(i.getLang());
    }

    @Override
    public int hashCode() {
        final int prime = 3469;
        int hash = 1;
        hash = prime * hash + id.hashCode();
        hash = prime * hash + name.hashCode();
        hash = prime * hash + lang.hashCode();
        return hash;
    }
}
