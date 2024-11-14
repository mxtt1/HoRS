/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.AllocationExceptionEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Mark
 */
@Stateless
public class AllocationExceptionEntitySessionBean implements AllocationExceptionEntitySessionBeanRemote, AllocationExceptionEntitySessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public List<AllocationExceptionEntity> getExceptionReportsForDate(Date date) {
        return em.createQuery("SELECT ae FROM AllocationExceptionEntity ae WHERE ae.reservationRoom.reservation.startDate = :currentDate", AllocationExceptionEntity.class)
                .setParameter("currentDate", date)
                .getResultList();  
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
