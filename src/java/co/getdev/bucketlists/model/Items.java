package co.getdev.bucketlists.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Ogundipe Segun David
 */
@Entity
@Table(name = "items")
public class Items implements Serializable {
    
    private static final long serialVersionUID = 2239701801613198300L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name", length = 200, unique = true)
    private String name;
    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @Column(name = "date_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateModified;
    @Column(name = "done")
    private Boolean done;
    @JoinColumn(name = "bucketlists", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @JsonbTransient
    private Bucketlists bucketlists;

    public Items() {
        this.dateCreated = Date.from(Instant.now());
    }

    public Items(Integer id) {
        this.id = id;
    }

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

    public Bucketlists getBucketlists() {
        return bucketlists;
    }

    public void setBucketlists(Bucketlists bucketlists) {
        this.bucketlists = bucketlists;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Items items = (Items) object;
        return name != null ? name.equals(items.name) : items.name == null;
    }
}
