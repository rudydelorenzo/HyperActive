
package HDControl;

public class Replay {
    
    private int slotId;
    private int clipId;
    private String timecode;
    private boolean hasSlotId = false;
    private boolean hasClipId = false;
    private boolean hasTimecode = false;
    
    public Replay(int sid, int Id, String tc) {
        slotId = sid;
        clipId = Id;
        timecode = tc;
        hasClipId = true;
        hasTimecode = true;
        hasSlotId = true;
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
    
    public void setSlotId(int id) {
        slotId = id;
        hasSlotId = true;
    }
    
    public int getSlotId() {
        return slotId;
    }
    
    public int getClipId() {
        return clipId;
    }
    
    public String getTimecode() {
        return timecode;
    }
    
    public boolean hasAll() {
        return (hasSlotId == true && hasClipId == true && hasTimecode == true);
    }
}