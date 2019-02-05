package co.getdev.bucketlists.model.queryresult;

import co.getdev.bucketlists.model.Items;
import java.util.Date;
import java.util.List;

/**
 * API model for returning bucketlists.
 *
 * @author Ogundipe Segun David
 */
public class QueryBucketlistResult {
    
    private Integer id;
    private String name;
    private List<Items> items;
    private Date dateCreated;
    private Date dateModified;
    private String createdBy;
    
    public QueryBucketlistResult(){}

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

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
