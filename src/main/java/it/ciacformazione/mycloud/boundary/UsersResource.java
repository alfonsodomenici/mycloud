/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ciacformazione.mycloud.boundary;


import it.ciacformazione.mycloud.control.UserStore;
import it.ciacformazione.mycloud.entity.User;
import java.net.URI;
import java.util.List;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 *
 * @author tss
 */
@DenyAll
@Path("/users")
public class UsersResource {

    @Inject
    UserStore store;

    @Context
    ResourceContext rc;
    
    @Inject
    JsonWebToken callerPrincipal;
    
    @GET
    @RolesAllowed({"dev"})
    public List<User> findAll() {
        return store.findAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response create(User a, @Context UriInfo uriInfo) {
        User saved = store.save(a);
        URI uri = uriInfo.getAbsolutePathBuilder()
                .path("/" + saved.getId())
                .build();
        return Response.ok(uri).build();
    }
    
    @GET
    @Path("{id}")
    @RolesAllowed({"users"})
    public User find(@PathParam("id") Long id) {
        return store.find(id);
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"users"})
    public void update(@PathParam("id") Long id, User a) {
        a.setId(id);
        store.save(a);
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"users"})
    public void delete(@PathParam("id") Long id) {
        store.remove(id);
    }
}
