
package HDControl;

public class ReplayIdentifier {
    
    private int id;
    private String name;
    private boolean starred = false;
    
    public ReplayIdentifier(int id) {
        this.id = id;
        name = "Unnamed";
        starred = false;
    }
    
    public ReplayIdentifier(int id, String name) {
        this.id = id;
        this.name = name;
        starred = false;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public int getId() {
        return id;
    }
    
    public void toggleStarred() {
        starred = !starred;
    }
    
    public boolean isStarred() {
        return starred;
    }
}