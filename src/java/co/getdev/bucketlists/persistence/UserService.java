package co.getdev.bucketlists.persistence;

import co.getdev.bucketlists.model.User;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service that provides operations for {@link User}s.
 *
 * @author Ogundipe Segun David
 */
@Stateless
public class UserService {
    
    @PersistenceContext(unitName = "bucketlistsapiPU")
    private EntityManager em;
    
    public UserService(){
        
    }
    
    public void createUser(User user){
        em.persist(user);
    }
    
    /**
     * Find a user by username or email.
     *
     * @param identifier
     * @return
     */
    public User findByUsernameOrEmail(String identifier) {
        List<User> users = em.createQuery("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier", User.class)
                .setParameter("identifier", identifier)
                .setMaxResults(1)
                .getResultList();
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    /**
     * Find all users.
     *
     * @return
     */
    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    /**
     * Find a user by id.
     *
     * @param userId
     * @return
     */
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(em.find(User.class, userId));
    }
    
    public void edit(User user){
        em.merge(user);
    }
}
