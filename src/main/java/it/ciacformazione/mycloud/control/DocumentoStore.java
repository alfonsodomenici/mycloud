/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ciacformazione.mycloud.control;

import it.ciacformazione.mycloud.Configuration;
import it.ciacformazione.mycloud.entity.Documento;
import it.ciacformazione.mycloud.entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 *
 * @author tss
 */
@RolesAllowed({"users"})
@Stateless
public class DocumentoStore {

    @PersistenceContext
    EntityManager em;

    @Inject
    Principal principal;
    
    @Inject
    JsonWebToken token;

    @Inject
    UserStore userStore;
    
    @PostConstruct
    public void init() {
        
    }

    public List<Documento> all() {
        System.out.println("token user: " + token.getName());
        System.out.println("token email: " + token.getClaim(Claims.email.name()));
        return em.createQuery("select e from Documento e where e.user.usr= :usr")
                .setParameter("usr", principal.getName())
                .getResultList();
    }

    public Documento find(Long id) {
        return em.find(Documento.class, id);
    }

    
    public Documento save(Documento d, InputStream is) {
        Optional<User> user = userStore.findByUsr(principal.getName());
        User logged = user.orElseThrow(() -> new EJBException("utente non trovato: " + principal.getName()));
        d.setUser(logged);
        Documento saved = em.merge(d);
        try {
            Files.copy(is, documentPath(saved.getFileName()),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new EJBException("save document failed...");
        }
        return saved;
    }

    public void remove(Long id) {
        Documento saved = find(id);
        try {
            Files.delete(documentPath(saved.getFileName()));
        } catch (IOException ex) {
            throw new EJBException("delete document failed...");
        }
        em.remove(saved);
    }

    private Path documentPath(String name) {
        return documentPath(name, principal.getName());
    }

    private Path documentPath(String name, String user) {
        return Paths.get(Configuration.DOCUMENT_FOLDER
                + user + "/" + name);
    }
    
    public File getFile(String fileName){
        return documentPath(fileName).toFile();
    }
    
    public File getFile(String fileName, String user){
        return documentPath(fileName,user).toFile();
    }
}
