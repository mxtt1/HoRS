/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entities.ReservationEntity;
import javax.ejb.Local;

/**
 *
 * @author mattl
 */
@Local
public interface ReservationEntitySessionBeanLocal {

    long createNewOnlineReservation(ReservationEntity newReservation, long bookerId, long roomTypeId );

    public long createNewWalkInReservation(ReservationEntity newReservation, long employeeId, long guestId, long roomTypeId);

    void allocateRoomsToReservation(long reservationId);
    
}
