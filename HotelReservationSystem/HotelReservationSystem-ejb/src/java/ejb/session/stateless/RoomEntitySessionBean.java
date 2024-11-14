/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.GuestEntity;
import entities.ReservationEntity;
import entities.RoomEntity;
import entities.RoomTypeEntity;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import util.enums.RoomStatus;
import util.exception.EntityIsDisabledException;
import util.exception.InputDataValidationException;

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
 
    @Inject
    private Validator validator;

    @Override
    public long createNewRoom(RoomEntity newRoom, String roomTypeName) throws NoResultException, EntityIsDisabledException, InputDataValidationException{
        Set<ConstraintViolation<RoomEntity>> constraintViolations = validator.validate(newRoom);

        if (constraintViolations.isEmpty()) {
            RoomTypeEntity roomType = roomTypeEntitySessionBean.retrieveRoomTypeByName(roomTypeName);
            if (roomType.isDisabled()) {
                throw new EntityIsDisabledException("Error: room type is disabled!");
            }
            roomType.getRooms().add(newRoom);
            newRoom.setRoomType(roomType);

            em.persist(newRoom);
            em.flush();
            return newRoom.getId();
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public RoomEntity retrieveRoomByNumber(String roomNum) throws NoResultException{
        RoomEntity roomEntity = em.createQuery("SELECT e FROM RoomEntity e WHERE e.roomNumber = :roomNum", RoomEntity.class)
             .setParameter("roomNum", roomNum)
             .getSingleResult();
        //roomEntity.getReservations().size();
        return roomEntity;
    }
    
    @Override
    public List<RoomEntity> retrieveAllRooms() {
        return em.createQuery("SELECT r FROM RoomEntity r", RoomEntity.class).getResultList();
    } 
    
    @Override
    public List<RoomEntity> retrieveActiveRoomsForType(RoomTypeEntity roomType) {
        return em.createNamedQuery("findActiveRoomsForRoomType")
                .setParameter("roomType", roomType)
                .getResultList();
    }
    
    
    @Override
    public RoomEntity retrieveRoom(long roomId) throws NoResultException{
        return em.find(RoomEntity.class, roomId);
    }

    @Override
    public RoomEntity retrieveRoom(long roomId, boolean loadReservations) throws NoResultException{
        RoomEntity retrievedRoom = this.retrieveRoom(roomId);
        /*if (loadReservations) {
            retrievedRoom.getReservations().size();
        }*/

        return retrievedRoom;
    }

    @Override
    public void deleteRoom(long roomId) throws NoResultException{
        RoomEntity roomToBeDeleted = em.find(RoomEntity.class, roomId);
        
        if (!roomToBeDeleted.getReservationRooms().isEmpty()) {
            roomToBeDeleted.setDisabled(true);
        } else {
            roomToBeDeleted.getRoomType().getRooms().remove(roomToBeDeleted);
            em.remove(roomToBeDeleted);
        }
    }
    
    @Override
    public RoomEntity updateRoom(RoomEntity room) throws InputDataValidationException{
        Set<ConstraintViolation<RoomEntity>> constraintViolations = validator.validate(room);

        if (constraintViolations.isEmpty()) {
            em.merge(room);
            em.flush();
            return room;
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public RoomEntity changeRoomType(long roomId, String newRoomTypeName) {
        RoomTypeEntity newRoomType = roomTypeEntitySessionBean.retrieveRoomTypeByName(newRoomTypeName);
        RoomEntity roomToChangeType = em.find(RoomEntity.class, roomId);
        
        roomToChangeType.getRoomType().getRooms().remove(roomToChangeType);
        roomToChangeType.setRoomType(newRoomType);
        newRoomType.getRooms().add(roomToChangeType);
        em.flush();
        
        return roomToChangeType;
    }

    @Override
    public List<RoomEntity> findUnassignedRoomsForRoomType(long roomTypeId, Date givenDate) {
       
        TypedQuery<RoomEntity> queryNoReservation = em.createQuery(
                "SELECT r FROM RoomEntity r "
                + "WHERE r.disabled = false "
                + "AND r.roomType.id = :roomTypeId "
                + "AND r.mostRecentReservation IS NULL",
                RoomEntity.class
        );
        queryNoReservation.setParameter("roomTypeId", roomTypeId);
        List<RoomEntity> roomsNoReservation = queryNoReservation.getResultList();


        TypedQuery<RoomEntity> queryWithEndDate = em.createQuery(
                "SELECT r FROM RoomEntity r "
                + "WHERE r.disabled = false "
                + "AND r.roomType.id = :roomTypeId "
                + "AND r.mostRecentReservation IS NOT NULL "
                + "AND r.mostRecentReservation.reservation.endDate <= :givenDate",
                RoomEntity.class
        );
        queryWithEndDate.setParameter("roomTypeId", roomTypeId);
        queryWithEndDate.setParameter("givenDate", givenDate);
        List<RoomEntity> roomsWithEndDate = queryWithEndDate.getResultList();

        roomsNoReservation.addAll(roomsWithEndDate);
        return roomsNoReservation;

    }
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomEntity>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }

}
