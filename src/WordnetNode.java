public class WordnetNode {
    private String id;
    private String name;
    private String lang;
    private String definition;

    public WordnetNode(String id, String name, String lang, String definition) {
        this.id = id;
        this.name = name;
        this.lang = lang;
        this.definition = definition;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLang() {
        return lang;
    }

    public String getDefinition() {
        return definition;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof WordnetNode)) return false;
        WordnetNode w = (WordnetNode) o;
        return id.equals(w.getId()) && name.equals(w.getName()) && lang.equals(w.getLang()) && definition.equals(w.getDefinition());
    }

    @Override
    public int hashCode() {
        final int prime = 3469;
        int hash = 1;
        hash = prime * hash + id.hashCode();
        hash = prime * hash + name.hashCode();
        hash = prime * hash + lang.hashCode();
        hash = prime * hash + definition.hashCode();
        return hash;
    }
}
