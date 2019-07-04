/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ciacformazione.mycloud.boundary;

import it.ciacformazione.mycloud.control.UserStore;
import it.ciacformazione.mycloud.entity.User;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author tss
 */
@Path("/auth")
public class SecurityResource {
    
    @Inject
    UserStore store;
    
    @POST
    public Response login( @FormParam("usr") String usr, @FormParam("pwd") String pwd){
        Optional<User> p = store.login(usr, pwd);
        p.ifPresent(a -> System.out.println(a.getNome()));
        return p.isPresent() ?  Response.ok().build():
                Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
