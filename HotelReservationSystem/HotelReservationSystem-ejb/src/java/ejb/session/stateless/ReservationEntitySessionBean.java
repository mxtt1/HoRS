/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.GuestEntity;
import entities.ReservationEntity;
import entities.ReservationRoomEntity;
import entities.RoomRateEntity;
import entities.RoomTypeEntity;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mattl
 */
@Stateless
public class ReservationEntitySessionBean implements ReservationEntitySessionBeanRemote, ReservationEntitySessionBeanLocal {

    @EJB
    private RoomRateEntitySessionBeanLocal roomRateEntitySessionBean;

    @EJB
    private RoomTypeEntitySessionBeanLocal roomTypeEntitySessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;


    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public long createNewOnlineReservation(ReservationEntity newReservation, long bookerId, long roomTypeId) {
        GuestEntity booker = em.find(GuestEntity.class, bookerId);
        newReservation.setBooker(booker);
        newReservation.setOccupant(booker);
        booker.getReservations().add(newReservation);
        booker.getBookedReservations().add(newReservation);
        
        RoomTypeEntity roomTypeToSet = em.find(RoomTypeEntity.class, roomTypeId);
        roomTypeToSet.getReservations().add(newReservation);
        newReservation.setRoomType(roomTypeToSet);
        
        for (int i = 1; i <= newReservation.getQuantity(); i++) {
            ReservationRoomEntity newReservationRoom = new ReservationRoomEntity();
            newReservation.getReservationRooms().add(newReservationRoom);
            newReservationRoom.setReservation(newReservation);
            em.persist(newReservationRoom);
        }
        
        BigDecimal totalCost = BigDecimal.ZERO;
        Date currentDate = newReservation.getStartDate();

        while (!currentDate.after(newReservation.getEndDate())) {
            RoomRateEntity applicableRate = null;
            List<RoomRateEntity> promoRates = roomRateEntitySessionBean.retrieveApplicablePromoRates(roomTypeToSet, currentDate);
            List<RoomRateEntity> peakRates = roomRateEntitySessionBean.retrieveApplicablePeakRates(roomTypeToSet, currentDate);
            
            if (!promoRates.isEmpty()) {
                applicableRate = promoRates.get(0);
            } else if (!peakRates.isEmpty()) {
                applicableRate = peakRates.get(0);
            } else {
                applicableRate = roomTypeToSet.getNormalRate();
            }
            
            if (applicableRate != null) {
                totalCost = totalCost.add(applicableRate.getRatePerNight());
            }
            
            if (!newReservation.getRoomRates().contains(applicableRate)) {
                newReservation.getRoomRates().add(applicableRate);
                applicableRate.getReservations().add(newReservation);
            }

            // Move to the next day
            currentDate = new Date(currentDate.getTime() + (1000 * 60 * 60 * 24)); 
        }
        
        newReservation.setFee(totalCost);
        
        em.persist(newReservation);
        em.flush();
        return newReservation.getId();
    }
}
