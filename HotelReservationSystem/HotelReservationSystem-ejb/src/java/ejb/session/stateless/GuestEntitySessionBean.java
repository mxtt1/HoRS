/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.GuestEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author Mark
 */
@Stateless
public class GuestEntitySessionBean implements GuestEntitySessionBeanRemote, GuestEntitySessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public long createNewGuest(GuestEntity newGuest) {
        em.persist(newGuest);
        em.flush();
        return newGuest.getId();
    }
    
    @Override
    public GuestEntity guestLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            GuestEntity currentGuest = this.retrieveGuestByUsername(username);
            if (currentGuest.getPassword().equals(password)) {
                return currentGuest;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (NoResultException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }
    
    @Override
    public GuestEntity retrieveGuestByUsername(String username) {
        GuestEntity guest = em.createQuery("SELECT e FROM GuestEntity e WHERE e.name = :name", GuestEntity.class)
             .setParameter("name", username)
             .getSingleResult();
        return guest;
    }
}
