package co.getdev.bucketlists.resource;

import co.getdev.bucketlists.model.Authority;
import co.getdev.bucketlists.model.User;
import co.getdev.bucketlists.persistence.UserService;
import co.getdev.bucketlists.security.AuthenticationTokenDetails;
import co.getdev.bucketlists.resource.wrapper.AuthenticationToken;
import co.getdev.bucketlists.resource.wrapper.CreateUserCredentials;
import co.getdev.bucketlists.resource.wrapper.UserCredentials;
import co.getdev.bucketlists.security.TokenBasedSecurityContext;
import co.getdev.bucketlists.security.service.AuthenticationTokenService;
import co.getdev.bucketlists.security.service.PasswordEncoder;
import co.getdev.bucketlists.security.service.UsernamePasswordValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * JAX-RS resource class that provides operations for authentication.
 *
 * @author Ogundipe Segun David
 */
@Path("auth")
@RequestScoped
public class AuthenticationResource {

    @Inject
    private UsernamePasswordValidator usernamePasswordValidator;
    
    @Inject
    private PasswordEncoder encoder;

    @Inject
    private AuthenticationTokenService authenticationTokenService;
    
    private AuthenticationTokenDetails authenticationTokenDetails;
    
    @EJB
    private UserService userService;
    
    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    @Operation(summary = "Create a new User",
            description = "Create a new User and assign the role of USER",
            tags = {"User"},
            responses = {
                @ApiResponse(content = @Content(mediaType = "text/html"))})
    public Response createUser(@RequestBody(
                                description = "Details of the user to be created", required = true,
                                content = @Content(schema = @Schema(implementation = CreateUserCredentials.class)))
                                CreateUserCredentials credentials){
        
        User user = new User();
        user.setActive(true);
        user.setFirstName(credentials.getFirstName());
        user.setLastName(credentials.getLastName());
        user.setUsername(credentials.getUsername());
        user.setEmail(credentials.getEmail());
        user.setPassword(encoder.hashPassword(credentials.getPassword()));
        user.setAuthority(Authority.USER);
        
        userService.createUser(user);
        
        return Response.ok("User: " + credentials.getUsername() + " has been created").build();
    }
    
    /**
     * Validate user credentials and issue a token for the user.
     *
     * @param credentials
     * @return
     */
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Operation(summary = "Authenticate a user",
            description = "Checks a user's credential against the database. Returns a token that can be used "
                    + "for subsequent request. Token is valid for one hour",
            tags = {"User"},
            responses = {
                @ApiResponse(description = "A Token user can provide in the authorization header for authentication",
                        content = @Content(mediaType = "application/json"))})
    public Response authenticate(@RequestBody(
                                description = "Details of the user to be authenticated", required = true,
                                content = @Content(schema = @Schema(implementation = UserCredentials.class)))
                                UserCredentials credentials) {

        User user = usernamePasswordValidator.validateCredentials(credentials.getUsername(), credentials.getPassword());
        String token = authenticationTokenService.issueToken(user.getUsername(), user.getAuthorities());
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setToken(token);
        return Response.ok(authenticationToken).build();
    }
    
    /**
     * Refresh the authentication token for the current user.
     *
     * @param context
     * @return
     */
    @GET
    @Path("refresh")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Refresh a token",
            description = "Refresh a user's token if it hasn't already expired",
            tags = {"User"},
            responses = {
                @ApiResponse(description = "A Token user can provide in the authorization header for authentication",
                        content = @Content(mediaType = "application/json"))})
    public Response refresh(@Context ContainerRequestContext context) {
        
        AuthenticationTokenDetails tokenDetails =
                ((TokenBasedSecurityContext) context.getSecurityContext()).getAuthenticationTokenDetails();
        String token = authenticationTokenService.refreshToken(tokenDetails);

        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setToken(token);
        return Response.ok(authenticationToken).build();
    }
}
