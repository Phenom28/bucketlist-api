package co.getdev.bucketlists.resource.wrapper;

/**
 * Gets the {@link Bucketlists} object to be created
 *
 * @author Ogundipe Segun David
 */
public class BucketlistWrapper {
    
    private String name;
    
    public BucketlistWrapper(){
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
