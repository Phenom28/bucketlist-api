package co.getdev.bucketlists.security.exceptionmapper;

import co.getdev.bucketlists.apierror.ApiErrorDetails;
import co.getdev.bucketlists.security.exception.AccessDeniedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for {@link AccessDeniedException}s.
 *
 * @author Ogundipe Segun David
 */
@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException>{
    
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(AccessDeniedException exception) {

        Response.Status status = Response.Status.FORBIDDEN;

        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setStatus(status.getStatusCode());
        errorDetails.setTitle(status.getReasonPhrase());
        errorDetails.setMessage("You don't have enough permissions to perform this action.");
        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

        return Response.status(status).entity(errorDetails).type(MediaType.APPLICATION_JSON).build();
    }
}
