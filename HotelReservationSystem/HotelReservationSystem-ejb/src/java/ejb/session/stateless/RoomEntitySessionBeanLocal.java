/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entities.RoomEntity;
import entities.RoomTypeEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.NoResultException;
import util.exception.AlreadyExistsException;
import util.exception.EntityIsDisabledException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author mattl
 */
@Local
public interface RoomEntitySessionBeanLocal {
    
    public long createNewRoom(RoomEntity newRoom, String roomTypeName) throws NoResultException, EntityIsDisabledException, InputDataValidationException, UnknownPersistenceException, AlreadyExistsException;
    
    public List<RoomEntity> retrieveAllRooms();

    public RoomEntity retrieveRoom(long roomId) throws NoResultException;

    public RoomEntity retrieveRoomByNumber(String roomNum) throws NoResultException;

    public RoomEntity updateRoom(RoomEntity room) throws InputDataValidationException;

    public RoomEntity changeRoomType(long roomId, String newRoomTypeName);

    public List<RoomEntity> retrieveActiveRoomsForType(RoomTypeEntity roomType);

    public List<RoomEntity> findUnassignedRoomsForRoomType(long roomTypeId, Date givenDate);

}
