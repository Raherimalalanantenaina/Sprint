package mg.itu.prom16.map;

public class VerbAction {
    String verb;
    String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }



    public VerbAction(String verb,String action){
        this.verb=verb;
        this.action=action;
    }
}
