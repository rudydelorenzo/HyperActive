
package HDControl;

public class Replay {
    
    private int clipId;
    private String timecode;
    private boolean hasClipId = false;
    private boolean hasTimecode = false;
    
    public Replay(int Id, String tc) {
        clipId = Id;
        timecode = tc;
        hasClipId = true;
        hasTimecode = true;
    }
    public Replay() {
        
    }
    
    public void setClipId(int id) {
        clipId = id;
        hasClipId = true;
    }
    
    public void setTimecode(String tc) {
        if (tc.contains(";")) {
            timecode = tc.replace(';', ':');
        } else {
            timecode = tc;
        }
        hasClipId = true;
    }
    
    public int getClipId() {
        return clipId;
    }
    
    public String getTimecode() {
        return timecode;
    }
    
    public boolean hasBoth() {
        return (hasClipId == true && hasTimecode == true);
    }
}
