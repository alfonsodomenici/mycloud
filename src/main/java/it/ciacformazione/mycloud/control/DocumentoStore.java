/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ciacformazione.mycloud.control;

import it.ciacformazione.mycloud.Configuration;
import it.ciacformazione.mycloud.entity.Documento;
import it.ciacformazione.mycloud.entity.User;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author tss
 */
@Stateless
public class DocumentoStore {

    @PersistenceContext
    EntityManager em;
    
    User logged;
    
    @PostConstruct
    public void init(){
        logged = em.find(User.class, 1l);
        
    }
    
    public List<Documento> all(){
        return em.createQuery("select e from Documento e where e.user= :usr")
                .setParameter("usr", logged)
                .getResultList();
    }
    
    public Documento find(Long id){
        return em.find(Documento.class, id);
    }
    
    public Documento save(Documento d, InputStream is) {
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
    
    private Path documentPath(String name){
        System.out.println("user: " + logged + " name: " + name);
        return Paths.get(Configuration.DOCUMENT_FOLDER + 
                logged.getUsr() + "/" + name);
    }

    void removeByUser(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
