package it.ciacformazione.mycloud;

import java.util.Set;
import javax.annotation.security.DeclareRoles;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.microprofile.auth.LoginConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 * Configures a JAX-RS endpoint. Delete this class, if you are not exposing
 * JAX-RS resources in your application.
 *
 * @author airhacks.com
 */
@ApplicationScoped
@LoginConfig(authMethod = "MP-JWT", realmName = "TCK-MP-JWT")
@ApplicationPath("resources")
@DeclareRoles({"users"})
public class JAXRSConfiguration extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(MultiPartFeature.class);
        resources.add(com.airhacks.cors.CorsResponseFilter.class);
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.airhacks.ping.boundary.PingResource.class);
        resources.add(it.ciacformazione.mycloud.boundary.DocumentiResource.class);
        resources.add(it.ciacformazione.mycloud.boundary.SecurityResource.class);
        resources.add(it.ciacformazione.mycloud.boundary.UsersResource.class);
    }

    
}
