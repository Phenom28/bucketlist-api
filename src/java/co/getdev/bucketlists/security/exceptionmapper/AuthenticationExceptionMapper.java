package co.getdev.bucketlists.security.exceptionmapper;

import co.getdev.bucketlists.apierror.ApiErrorDetails;
import co.getdev.bucketlists.security.exception.AuthenticationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for {@link AuthenticationException}s.
 *
 * @author Ogundipe Segun David
 */
@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {
    
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(AuthenticationException exception) {

        Response.Status status = Response.Status.FORBIDDEN;

        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setStatus(status.getStatusCode());
        errorDetails.setTitle(status.getReasonPhrase());
        errorDetails.setMessage(exception.getMessage());
        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

        return Response.status(status).entity(errorDetails).type(MediaType.APPLICATION_JSON).build();
    }
}
