package co.getdev.bucketlists;

import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

/**
 *
 * @author Ogundipe Segun David
 */
@ApplicationPath("api/v1")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        resources.add(OpenApiResource.class);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(co.getdev.bucketlists.resource.AuthenticationResource.class);
        resources.add(co.getdev.bucketlists.resource.BucketlistsResource.class);
        resources.add(co.getdev.bucketlists.resource.ItemsResource.class);
        resources.add(co.getdev.bucketlists.security.exceptionmapper.AccessDeniedExceptionMapper.class);
        resources.add(co.getdev.bucketlists.security.exceptionmapper.AuthenticationExceptionMapper.class);
        resources.add(co.getdev.bucketlists.security.exceptionmapper.AuthenticationTokenRefreshmentExceptionMapper.class);
        resources.add(co.getdev.bucketlists.security.filter.AuthenticationFilter.class);
        resources.add(co.getdev.bucketlists.security.filter.AuthorizationFilter.class);
    }

}
