/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entities.ReservationEntity;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author mattl
 */
@Remote
public interface ReservationEntitySessionBeanRemote {

    long createNewOnlineReservation(ReservationEntity newReservation, long bookerId, long roomTypeId);

    public long createNewWalkInReservation(ReservationEntity newReservation, long employeeId, long guestId, long roomTypeId);

    void allocateRoomsToReservation(long reservationId);

    List<ReservationEntity> retrieveAllReservationsForGuest(long guestId);

    ReservationEntity retrieveReservation(long reservationId);

}
