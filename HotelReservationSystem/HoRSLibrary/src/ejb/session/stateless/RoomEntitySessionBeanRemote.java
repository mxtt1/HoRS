/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entities.RoomEntity;
import entities.RoomTypeEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import javax.persistence.NoResultException;
import util.exception.EntityIsDisabledException;
import util.exception.InputDataValidationException;

/**
 *
 * @author mattl
 */
@Remote
public interface RoomEntitySessionBeanRemote {

    public long createNewRoom(RoomEntity newRoom, String roomTypeName) throws NoResultException, EntityIsDisabledException, InputDataValidationException;

    public List<RoomEntity> retrieveAllRooms();

    public RoomEntity retrieveRoom(long roomId, boolean loadReservations);

    public void deleteRoom(long roomId);
    
    public RoomEntity retrieveRoomByNumber(String roomNum) throws NoResultException;
    
    public RoomEntity updateRoom(RoomEntity room) throws InputDataValidationException;

    public RoomEntity changeRoomType(long roomId, String newRoomTypeName);

    public List<RoomEntity> findUnassignedRoomsForRoomType(long roomTypeId, Date givenDate);
}
