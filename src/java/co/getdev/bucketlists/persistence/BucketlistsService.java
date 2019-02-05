package co.getdev.bucketlists.persistence;

import co.getdev.bucketlists.model.Bucketlists;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

/**
 * Service that provides operations for {@link Bucketlists}.
 *
 * @author Ogundipe Segun David
 */
@Stateless
public class BucketlistsService {
    
    @PersistenceContext(unitName = "bucketlistsapiPU")
    private EntityManager em;
    
    public BucketlistsService(){
        
    }
    
    /**
     * Find a bucketlist by id.
     *
     * @param id
     * @return
     */
    public Optional<Bucketlists> findById(Integer id){
        return Optional.ofNullable(em.find(Bucketlists.class, id));
    }
    
    /**
     * Find all bucketlist.
     *
     * @return
     */
    public List<Bucketlists> findAll() {
        return em.createQuery("SELECT b FROM Bucketlists b", Bucketlists.class).getResultList();
    }
    
    /**
     * Find all user's {@link Bucketlists}.
     *
     * @param username
     * @return all {@link Bucketlists} object belonging to user
     */
    public List<Bucketlists> findAllByName(String username){
        return em.createQuery("SELECT b FROM Bucketlists b WHERE b.createdBy.username = :username")
                .setParameter("username", username)
                .getResultList();
    }
    
    /**
     * Find a bucketlist by name.
     *
     * @param name
     * @return
     */
    public Optional<Bucketlists> findByName(String name) {
        return Optional.ofNullable(em.createQuery("SELECT b FROM Bucketlists b WHERE b.name = :name", Bucketlists.class)
                .setParameter("name", name)
                .getSingleResult());
    }
    
    /**
     * Create a new bucketlist.
     *
     * @param bucketlists
     * 
     */
    public void create(Bucketlists bucketlists){
        em.persist(bucketlists);
    }
    
    /**
     * Edit a bucketlist.
     *
     * @param bucketlists
     *
     */
    public void edit(Bucketlists bucketlists){
        em.merge(bucketlists);
    }
    
    /**
     * Remove a bucketlist from the database.
     *
     * @param bucketlists
     *
     */
    public void remove(Bucketlists bucketlists){
        em.remove(em.merge(bucketlists));
    }
    
    public List<Bucketlists> findRange(Integer page, Integer limit){
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Bucketlists.class));
        Query q = em.createQuery(cq);
        q.setMaxResults(limit - page + 1);
        q.setFirstResult(page);
        return q.getResultList();
    }
}
