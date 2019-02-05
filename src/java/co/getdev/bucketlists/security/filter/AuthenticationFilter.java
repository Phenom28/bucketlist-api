package co.getdev.bucketlists.security.filter;

import co.getdev.bucketlists.model.User;
import co.getdev.bucketlists.security.AuthenticatedUserDetails;
import co.getdev.bucketlists.security.AuthenticationTokenDetails;
import co.getdev.bucketlists.security.TokenBasedSecurityContext;
import co.getdev.bucketlists.security.service.AuthenticationTokenService;
import co.getdev.bucketlists.persistence.UserService;
import java.io.IOException;
import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

/**
 * JWT authentication filter.
 *
 * @author Ogundipe Segun David
 */
@Provider
@Dependent
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    
    @EJB
    private UserService userService;

    @Inject
    private AuthenticationTokenService authenticationTokenService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String authenticationToken = authorizationHeader.substring(7);
            handleTokenBasedAuthentication(authenticationToken, requestContext);
            return;
        }

        // Other authentication schemes (such as Basic) could be supported
    }

    private void handleTokenBasedAuthentication(String authenticationToken, ContainerRequestContext requestContext) {

        AuthenticationTokenDetails authenticationTokenDetails = authenticationTokenService.parseToken(authenticationToken);
        User user = userService.findByUsernameOrEmail(authenticationTokenDetails.getUsername());
        AuthenticatedUserDetails authenticatedUserDetails = new AuthenticatedUserDetails(user.getUsername(), user.getAuthorities());

        boolean isSecure = requestContext.getSecurityContext().isSecure();
        SecurityContext securityContext = new TokenBasedSecurityContext(authenticatedUserDetails, authenticationTokenDetails, isSecure);
        requestContext.setSecurityContext(securityContext);
    }
}
