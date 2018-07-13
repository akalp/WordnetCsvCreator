public class Relationship {
    private String from;
    private String to;
    private String type;

    public Relationship(String from, String to, String type){
        this.from = from;
        this.to = to;
        this.type = type;
    }

    public String getFrom(){
        return from;
    }

    public String getTo(){
        return to;
    }

    public String getType(){
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Relationship)) return false;
        Relationship r = (Relationship) o;
        return this.type.equals(r.getType()) && this.from.equals(r.getFrom()) && this.to.equals(r.getTo());

    }

    @Override
    public int hashCode() {
        final int prime = 3469;
        int hash = 1;
        hash = prime * hash + from.hashCode();
        hash = prime * hash + to.hashCode();
        hash = prime * hash + type.hashCode();
        return hash;
    }
}
