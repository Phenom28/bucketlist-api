package co.getdev.bucketlists.model.queryresult;

import java.util.Date;

/**
 * API model for returning items.
 *
 * @author Ogundipe Segun David
 */
public class QueryItemsResult {
    
    private Integer id;
    private String name;
    private Date dateCreated;
    private Date dateModified;
    private Boolean done;
    
    public QueryItemsResult(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }
}
