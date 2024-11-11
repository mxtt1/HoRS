/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.AllocationExceptionEntity;
import entities.EmployeeEntity;
import entities.GuestEntity;
import entities.ReservationEntity;
import entities.ReservationRoomEntity;
import entities.RoomEntity;
import entities.RoomRateEntity;
import entities.RoomTypeEntity;
import entities.UnregisteredGuestEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enums.RoomStatus;

/**
 *
 * @author mattl
 */
@Stateless
public class ReservationEntitySessionBean implements ReservationEntitySessionBeanRemote, ReservationEntitySessionBeanLocal {

    @EJB
    private RoomEntitySessionBeanLocal roomEntitySessionBean;

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

    @Override
    @Asynchronous
    public void allocateRoomsToReservation(long reservationId) {
        ReservationEntity reservationToAllocate = em.find(ReservationEntity.class, reservationId);
        RoomTypeEntity roomTypeToAllocate = reservationToAllocate.getRoomType();
        for (ReservationRoomEntity rr : reservationToAllocate.getReservationRooms()) {
            List<RoomEntity> availableRooms = roomEntitySessionBean.findUnassignedRoomsForRoomType(roomTypeToAllocate.getId());

            if (!availableRooms.isEmpty() && rr.getAllocatedRoom() == null) { // there is an available unassigned room
                RoomEntity roomToAllocate = availableRooms.get(0);
                roomToAllocate.getReservationRooms().add(rr);
                rr.setAllocatedRoom(roomToAllocate);
                roomToAllocate.setRoomStatus(RoomStatus.ASSIGNED);
            } else if (roomTypeToAllocate.getRanking() == 1 && rr.getAllocatedRoom() == null) { // no available roosm of original room type and no better room type  
                String exceptionMessage = "Unable to assign room of type " + roomTypeToAllocate.getName()
                        + " for reservation made by guest with passport number "
                        + reservationToAllocate.getOccupant().getPassportNum() + " starting on " + reservationToAllocate.getStartDate();
                AllocationExceptionEntity newAllocationException = new AllocationExceptionEntity(false, exceptionMessage);
                newAllocationException.setReservationRoom(rr);
                rr.setAllocationException(newAllocationException);
                em.persist(newAllocationException);
            } else if(rr.getAllocatedRoom() == null) { // check the better room type
                RoomTypeEntity upgradedRoomType = em.createNamedQuery("findUpgradedRoomType", RoomTypeEntity.class)
                        .setParameter("givenRanking", roomTypeToAllocate.getRanking() - 1).getSingleResult();
                availableRooms = roomEntitySessionBean.findUnassignedRoomsForRoomType(upgradedRoomType.getId());

                if (!availableRooms.isEmpty()) {
                    RoomEntity roomToAllocate = availableRooms.get(0);
                    roomToAllocate.getReservationRooms().add(rr);
                    rr.setAllocatedRoom(roomToAllocate);
                    roomToAllocate.setRoomStatus(RoomStatus.ASSIGNED);
                    String exceptionMessage = "Automatically assigned room of upgraded type " + upgradedRoomType.getName()
                            + " for reservation made by guest with passport number "
                            + reservationToAllocate.getOccupant().getPassportNum() + " starting on " + reservationToAllocate.getStartDate();
                    AllocationExceptionEntity newAllocationException = new AllocationExceptionEntity(true, exceptionMessage);
                    newAllocationException.setReservationRoom(rr);
                    rr.setAllocationException(newAllocationException);
                    em.persist(newAllocationException);
                } else {
                    String exceptionMessage = "Unable to assign room of type " + roomTypeToAllocate.getName()
                            + " for reservation made by guest with passport number "
                            + reservationToAllocate.getOccupant().getPassportNum() + " starting on " + reservationToAllocate.getStartDate();
                    AllocationExceptionEntity newAllocationException = new AllocationExceptionEntity(false, exceptionMessage);
                    newAllocationException.setReservationRoom(rr);
                    rr.setAllocationException(newAllocationException);
                    em.persist(newAllocationException);
                }
            }
        em.flush();    
        }
    }
    
}
