/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.ReservationEntitySessionBeanLocal;
import entities.ReservationEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author mattl
 */
@Singleton
@Startup
public class RoomAllocationSessionBean implements RoomAllocationSessionBeanRemote, RoomAllocationSessionBeanLocal {

    @EJB
    private ReservationEntitySessionBeanLocal reservationEntitySessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    @Schedule(hour = "2", minute = "0", second = "0", persistent = false)
    public void roomAllocationTimer() {
        Date currentDate = new Date(); // empty constructor instantiates Date object with today's date
        this.allocateRoomsForDate(currentDate);
    }

    @Override
    public void allocateRoomsForDate(Date currentDate) {
        TypedQuery<ReservationEntity> query = em.createQuery("SELECT re FROM ReservationEntity re WHERE re.startDate = :currentDate", ReservationEntity.class);
        query.setParameter("currentDate", currentDate);
        List<ReservationEntity> reservationsToAllocate = query.getResultList();
        
        for (ReservationEntity re : reservationsToAllocate) {
            reservationEntitySessionBean.allocateRoomsToReservation(re.getId());
        }
    }
        
}
