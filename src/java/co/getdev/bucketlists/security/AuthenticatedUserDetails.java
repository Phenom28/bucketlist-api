package co.getdev.bucketlists.security;

import co.getdev.bucketlists.model.Authority;
import java.security.Principal;
import java.util.Collections;
import java.util.Set;

/**
 * {@link Principal} implementation with a set of {@link Authority}.
 *
 * @author Ogundipe Segun David
 */
public class AuthenticatedUserDetails implements Principal {
    
    private final String username;
    private final Set<Authority> authorities;

    public AuthenticatedUserDetails(String username, Set<Authority> authorities) {
        this.username = username;
        this.authorities = Collections.unmodifiableSet(authorities);
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return username;
    }
}
