/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.UnregisteredGuestEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.exception.AlreadyExistsException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Mark
 */
@Stateless
public class UnregisteredGuestEntitySessionBean implements UnregisteredGuestEntitySessionBeanRemote, UnregisteredGuestEntitySessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public long createNewUnregisteredGuest(UnregisteredGuestEntity newUnregisteredGuest) throws UnknownPersistenceException, AlreadyExistsException {
        try {
        em.persist(newUnregisteredGuest);
        em.flush();
        return newUnregisteredGuest.getId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new AlreadyExistsException("Guest already exists");
                }
            }
            throw new UnknownPersistenceException(ex.getMessage());

        }
    }
}
