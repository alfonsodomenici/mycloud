/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ciacformazione.mycloud.control;

import it.ciacformazione.mycloud.Configuration;
import it.ciacformazione.mycloud.entity.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author tss
 */
@Stateless
public class UserStore {

    @PersistenceContext
    EntityManager em;

    @Inject
    DocumentoStore docStore;

    public Optional<User> login(String usr, String pwd) {
        try {
            User p = em.createQuery("select e from User e "
                    + "where e.usr= :usr and e.pwd= :pwd", User.class)
                    .setParameter("usr", usr)
                    .setParameter("pwd", pwd)
                    .getSingleResult();
            return Optional.of(p);
        } catch (NoResultException | NonUniqueResultException ex) {
            return Optional.empty();
        }
    }

    public List<User> findAll() {
        return em.createQuery("select e from User e order by e.cognome", User.class)
                .getResultList();
    }

    public User find(Long id) {
        return em.find(User.class, id);
    }

    public Optional<User> findByUsername(String usr) {
        try {
            User p = em.createQuery("select e from User e where e.usr= :usr", User.class)
                    .setParameter("usr", usr)
                    .getSingleResult();
            return Optional.of(p);
        } catch (NoResultException | NonUniqueResultException ex) {
            return Optional.empty();
        }

    }

    public User save(User a) {
        User saved = em.merge(a);
        Path path = Paths.get(Configuration.DOCUMENT_FOLDER + saved.getUsr());
        if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
            try {
                Files.createDirectory(path);
            } catch (IOException ex) {
                throw new EJBException("save user failed...");
            }
        }
        return saved;
    }

    public void remove(Long id) {
        User saved = find(id);
        em.createQuery("delete from Documento e where e.user= :usr")
                .setParameter("usr", saved)
                .executeUpdate();
        em.remove(saved);
        try {
            deleteDirectory(Paths.get(Configuration.DOCUMENT_FOLDER + saved.getUsr()));
        } catch (IOException ex) {
            throw new EJBException("remove user failed...");
        }
    }

    private void deleteDirectory(Path path) throws IOException {
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}
