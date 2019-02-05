package co.getdev.bucketlists.security.service;

import co.getdev.bucketlists.security.exception.AuthenticationException;
import co.getdev.bucketlists.model.User;
import co.getdev.bucketlists.persistence.UserService;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Component for validating user credentials.
 *
 * @author Ogundipe Segun David
 */
@ApplicationScoped
public class UsernamePasswordValidator {
    
    @EJB
    private UserService userService;

    @Inject
    private PasswordEncoder passwordEncoder;

    /**
     * Validate username and password.
     *
     * @param username
     * @param password
     * @return
     */
    public User validateCredentials(String username, String password) {

        User user = userService.findByUsernameOrEmail(username);

        if (user == null) {
            // User cannot be found with the given username/email
            throw new AuthenticationException("Bad credentials.");
        }

        if (!user.isActive()) {
            // User is not active
            throw new AuthenticationException("The user is inactive.");
        }

        if (!passwordEncoder.checkPassword(password, user.getPassword())) {
            // Invalid password
            throw new AuthenticationException("Bad credentials.");
        }

        return user;
    }
}
