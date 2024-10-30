/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entities.RoomEntity;
import entities.RoomTypeEntity;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.NoResultException;

/**
 *
 * @author mattl
 */
@Local
public interface RoomEntitySessionBeanLocal {
    
    public List<RoomEntity> retrieveAllRooms();

    public RoomEntity retrieveRoom(long roomId) throws NoResultException;

    public RoomEntity retrieveRoomByNumber(String roomNum) throws NoResultException;

    public RoomEntity updateRoom(RoomEntity room);

    public RoomEntity changeRoomType(long roomId, String newRoomTypeName);

}
