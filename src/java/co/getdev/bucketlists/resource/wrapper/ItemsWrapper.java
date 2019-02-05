package co.getdev.bucketlists.resource.wrapper;

/**
 * Gets the {@link Items} object to be created
 *
 * @author Ogundipe Segun David
 */
public class ItemsWrapper {
    
    private String name;
    private Boolean done;
    
    public ItemsWrapper(){
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }
}
