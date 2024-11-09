/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.UnregisteredGuestEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Mark
 */
@Stateless
public class UnregisteredGuestEntitySessionBean implements UnregisteredGuestEntitySessionBeanRemote, UnregisteredGuestEntitySessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public long createNewUnregisteredGuest(UnregisteredGuestEntity newUnregisteredGuest) {
        em.persist(newUnregisteredGuest);
        em.flush();
        return newUnregisteredGuest.getId();
    }
}
