package co.getdev.bucketlists.persistence;

import co.getdev.bucketlists.model.Bucketlists;
import co.getdev.bucketlists.model.Items;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service that provides operations for {@link Items}.
 *
 * @author Ogundipe Segun David
 */
@Stateless
public class ItemsService {
    
    @PersistenceContext(unitName = "bucketlistsapiPU")
    private EntityManager em;

    public ItemsService() {
        
    }
    
    /**
     * Find an item by id.
     *
     * @param itemId The item id
     * @return
     */
    public Optional<Items> findById(Integer itemId){
        return Optional.ofNullable(em.find(Items.class, itemId));
    }
    
    /**
     * Find all items in a bucketlist.
     *
     * @param id The bucketlist id
     * @return
     */
    public List<Items> findAll(Bucketlists id) {
        return em.createQuery("SELECT i FROM Items i WHERE i.bucketlists = :id")
                .setParameter("id", id).getResultList();
    }
    
    /**
     * Create a new items.
     *
     * @param item The item to create
     * 
     */
    public void create(Items item){
        em.persist(item);
    }
    
    /**
     * Edit an item.
     *
     * @param item The item to edit
     *
     */
    public void edit(Items item){
        em.merge(item);
    }
    
    /**
     * Remove an item from the database.
     *
     * @param item The item to remove
     *
     */
    public void remove(Items item){
        em.remove(em.merge(item));
    }
}
