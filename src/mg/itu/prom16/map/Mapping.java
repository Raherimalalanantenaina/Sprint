package mg.itu.prom16.map;
import java.util.*;

public class Mapping {
    String className;
    List<VerbAction> verb;


    public Mapping(String className) {
        this.className = className;
        this.verb =new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }

    public void setVerbActions(VerbAction verbAction){
        this.verb.add(verbAction);
    }

    public void setClassName(String className) {
        this.className = className;
    }
    public List<VerbAction> getVerb(){
        return verb;
    }

    public boolean isVerbPresent(String verbToCheck) {
        for (VerbAction action : this.verb) {
            if (action.getVerb().equalsIgnoreCase(verbToCheck)) {
                return true;
            }
        }
        return false;
    }
    
}
