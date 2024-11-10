/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.EmployeeEntity;
import entities.GuestEntity;
import entities.ReservationEntity;
import entities.ReservationRoomEntity;
import entities.RoomRateEntity;
import entities.RoomTypeEntity;
import entities.UnregisteredGuestEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    //GUEST MODULE
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
        
        newReservation.setFee(BigDecimal.ZERO);
        em.persist(newReservation);
        em.flush();

        
        for (int i = 1; i <= newReservation.getQuantity(); i++) {
            ReservationRoomEntity newReservationRoom = new ReservationRoomEntity();  
            newReservation.getReservationRooms().add(newReservationRoom);
            newReservationRoom.setReservation(newReservation);
            em.persist(newReservationRoom);
            em.flush();
        }
        
        Date currentDate = newReservation.getStartDate();
        BigDecimal totalCost = BigDecimal.ZERO;

        while (currentDate.before(newReservation.getEndDate())) {
            RoomRateEntity applicableRate = null;
            List<RoomRateEntity> promoRates = roomRateEntitySessionBean.retrieveApplicablePromoRates(roomTypeToSet, currentDate);
            List<RoomRateEntity> peakRates = roomRateEntitySessionBean.retrieveApplicablePeakRates(roomTypeToSet, currentDate);
            
            if (!(promoRates == null) && !promoRates.isEmpty() ) {
                applicableRate = promoRates.get(0);
            } else if (!(promoRates == null) && !peakRates.isEmpty()) {
                applicableRate = peakRates.get(0);
            } else {
                applicableRate = roomTypeToSet.getNormalRate();
            }
            
            totalCost = totalCost.add(applicableRate.getRatePerNight());
            
            if (!newReservation.getRoomRates().contains(applicableRate)) {
                newReservation.getRoomRates().add(applicableRate);
                applicableRate.getReservations().add(newReservation);
            }
            em.flush();
            // Move to the next day
            currentDate = new Date(currentDate.getTime() + (1000 * 60 * 60 * 24)); 
        }
        newReservation.setFee(totalCost);
        em.flush();
        return newReservation.getId();

    }
    
    @Override
    public long createNewWalkInReservation(ReservationEntity newReservation, long employeeId, long guestId, long roomTypeId) {
        EmployeeEntity employee = em.find(EmployeeEntity.class, employeeId);
        UnregisteredGuestEntity guest = em.find(UnregisteredGuestEntity.class, guestId);
        newReservation.setEmployee(employee);
        newReservation.setOccupant(guest);
        guest.getReservations().add(newReservation);
        employee.getReservations().add(newReservation);
        
        RoomTypeEntity roomTypeToSet = em.find(RoomTypeEntity.class, roomTypeId);
        roomTypeToSet.getReservations().add(newReservation);
        newReservation.setRoomType(roomTypeToSet);
        
        newReservation.setFee(roomTypeEntitySessionBean.getPublishedRateForDates(roomTypeToSet, newReservation.getStartDate(), newReservation.getEndDate()));
        em.persist(newReservation);
        em.flush();
        
        for (int i = 0; i < newReservation.getQuantity(); i++) {
            ReservationRoomEntity newReservationRoom = new ReservationRoomEntity();
            newReservation.getReservationRooms().add(newReservationRoom);
            newReservationRoom.setReservation(newReservation);
            em.persist(newReservationRoom);
            em.flush();
        }
        
        RoomRateEntity applicableRate = roomTypeToSet.getPublishedRate();
        applicableRate.getReservations().add(newReservation);
        newReservation.getRoomRates().add(applicableRate);
        
        em.flush();
        return newReservation.getId();
    }
    
}
