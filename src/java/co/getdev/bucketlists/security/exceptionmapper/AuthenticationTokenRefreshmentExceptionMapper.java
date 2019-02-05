package co.getdev.bucketlists.security.exceptionmapper;

import co.getdev.bucketlists.apierror.ApiErrorDetails;
import co.getdev.bucketlists.security.exception.AuthenticationTokenRefreshmentException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for {@link AuthenticationTokenRefreshmentException}s.
 *
 * @author Ogundipe Segun David
 */
@Provider
public class AuthenticationTokenRefreshmentExceptionMapper implements ExceptionMapper<AuthenticationTokenRefreshmentException>{
    
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(AuthenticationTokenRefreshmentException exception) {

        Response.Status status = Response.Status.FORBIDDEN;

        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setStatus(status.getStatusCode());
        errorDetails.setTitle(status.getReasonPhrase());
        errorDetails.setMessage("The authentication token cannot be refreshed.");
        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

        return Response.status(status).entity(errorDetails).type(MediaType.APPLICATION_JSON).build();
    }
}
