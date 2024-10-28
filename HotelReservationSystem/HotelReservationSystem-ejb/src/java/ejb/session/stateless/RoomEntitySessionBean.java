/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.RoomEntity;
import entities.RoomTypeEntity;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mattl
 */
@Stateless
public class RoomEntitySessionBean implements RoomEntitySessionBeanRemote, RoomEntitySessionBeanLocal {

    @EJB
    private RoomTypeEntitySessionBeanLocal roomTypeEntitySessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
 

    @Override
    public long createNewRoom(RoomEntity newRoom, String roomTypeName) throws NoResultException{
        RoomTypeEntity roomType = roomTypeEntitySessionBean.retrieveRoomTypeByName(roomTypeName);
        roomType.getRooms().add(newRoom);
        newRoom.setRoomType(roomType);
        
        em.persist(newRoom);
        em.flush();
        return newRoom.getId();
    }
    
    @Override
    public RoomEntity retrieveRoomByName(String roomName) throws NoResultException{
        RoomEntity roomEntity = em.createQuery("SELECT e FROM RoomEntity e WHERE e.roomNumber = :roomName", RoomEntity.class)
             .setParameter("roomName", roomName)
             .getSingleResult();
        roomEntity.getReservations().size();
        return roomEntity;
    }
    
    @Override
    public List<RoomEntity> retrieveAllRooms() {
        return em.createQuery("SELECT r FROM RoomEntity r WHERE r.disabled = FALSE", RoomEntity.class).getResultList();
    } 
    
    @Override
    public RoomEntity retrieveRoom(long roomId) throws NoResultException{
        return em.find(RoomEntity.class, roomId);
    }

    @Override
    public RoomEntity retrieveRoom(long roomId, boolean loadReservations) throws NoResultException{
        RoomEntity retrievedRoom = this.retrieveRoom(roomId);
        if (loadReservations) {
            retrievedRoom.getReservations().size();
        }

        return retrievedRoom;
    }

    @Override
    public void deleteRoom(long roomId) throws NoResultException{
        RoomEntity roomToBeDeleted = em.find(RoomEntity.class, roomId);
        
        if (roomToBeDeleted.getCurrentReservation() != null) {
            roomToBeDeleted.setDisabled(true);
        } else {
            roomToBeDeleted.getRoomType().getRooms().remove(roomToBeDeleted);
            em.remove(roomToBeDeleted);
        }
    }
    
    @Override
    public RoomEntity updateRoom(RoomEntity room) {
        em.merge(room);
        em.flush();
        return room;
    }
    
}
