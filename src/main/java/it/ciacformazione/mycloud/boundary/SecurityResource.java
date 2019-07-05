/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ciacformazione.mycloud.boundary;

import it.ciacformazione.mycloud.JWTManager;
import it.ciacformazione.mycloud.control.UserStore;
import it.ciacformazione.mycloud.entity.User;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author tss
 */
@DenyAll
@Path("/auth")
public class SecurityResource {

    @Inject
    UserStore store;

    @PermitAll
    @GET
    @Path("test")
    public Response login() {

        try {
            String token = JWTManager.generateJWTString("token.json");
            System.out.println("------------ generated token -------------------");
            System.out.println("------------ curl command for test -------------");
            System.out.println("curl -i -H'Authorization: Bearer " + token + "' http://localhost:8080/mycloud/resources/users");
        } catch (Exception ex) {
            Logger.getLogger(SecurityResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok().build();
    }

    @PermitAll
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("usr") String usr, @FormParam("pwd") String pwd) {
        System.out.println(usr + " " + pwd);
        Optional<User> p = store.login(usr, pwd);
        if (p.isPresent()) {
            JsonObject token = Json.createObjectBuilder().add("token",
                JWTManager.generateJWTString("token.json", p.get().getUsr())).build();
            return Response.ok().entity(token).build();
        }
        
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
