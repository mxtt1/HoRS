/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.GuestEntity;
import entities.ReservationEntity;
import entities.UnregisteredGuestEntity;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.InvalidLoginCredentialException;
import util.exception.UserAlreadyRegisteredException;

/**
 *
 * @author Mark
 */
@Stateless
public class GuestEntitySessionBean implements GuestEntitySessionBeanRemote, GuestEntitySessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public long createNewGuest(GuestEntity newGuest) throws UserAlreadyRegisteredException {
        List<UnregisteredGuestEntity> duplicate = em.createQuery("SELECT g FROM UnregisteredGuestEntity g WHERE g.passportNum = :passportNum")
                .setParameter("passportNum", newGuest.getPassportNum())
                .getResultList();
                
        if (!duplicate.isEmpty() && !(duplicate.get(0) instanceof GuestEntity)) { // if there exists a UNREGISTERED guest with same passport number
            UnregisteredGuestEntity guestToDelete = duplicate.get(0);
            List<ReservationEntity> reservations = guestToDelete.getReservations();
            String passportNum = newGuest.getPassportNum();
            newGuest.setPassportNum("TEMPORARY");

            for (ReservationEntity re : reservations) {
                re.setOccupant(newGuest);
            }
            newGuest.getReservations().addAll(reservations);
            guestToDelete.getReservations().clear();
            em.remove(guestToDelete);
            em.persist(newGuest);
            em.flush();
            newGuest.setPassportNum(passportNum);
        } else if (!duplicate.isEmpty() && duplicate.get(0) instanceof GuestEntity) {
            throw new UserAlreadyRegisteredException("Error: User has already been registered. Please log in instead.");
        } else { //
            em.persist(newGuest);
            em.flush();
        }
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
    
    @Override
    public List<UnregisteredGuestEntity> retrieveGuestByPassportNo(String passportNo) {
        List<UnregisteredGuestEntity> guests = em.createQuery("SELECT e FROM UnregisteredGuestEntity e WHERE e.passportNum = :passportNo", UnregisteredGuestEntity.class)
             .setParameter("passportNo", passportNo).getResultList();
          
        return guests;
    }
    
}
