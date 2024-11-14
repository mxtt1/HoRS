/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.GuestEntity;
import entities.ReservationEntity;
import entities.RoomRateEntity;
import entities.RoomTypeEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import util.exception.AlreadyExistsException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author mattl
 */
@Stateless
public class RoomTypeEntitySessionBean implements RoomTypeEntitySessionBeanRemote, RoomTypeEntitySessionBeanLocal {

    @EJB
    private RoomRateEntitySessionBeanLocal roomRateEntitySessionBean;

    @EJB
    private RoomEntitySessionBeanLocal roomEntitySessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Inject
    private Validator validator;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public List<RoomTypeEntity> getAvailableRoomTypes(Date startDate, Date endDate) {
        List<RoomTypeEntity> availableRoomTypes = new ArrayList<>();
        List<RoomTypeEntity> roomTypes = this.retrieveActiveRoomTypes();

        for (RoomTypeEntity roomType : roomTypes) {
            if (this.getAvailableRoomQuantity(startDate, endDate, roomType) > 0) {
                availableRoomTypes.add(roomType);
            }
        }
        return availableRoomTypes;
    }

    @Override
    public BigDecimal getNormalRateForDates(RoomTypeEntity roomType, Date startDate, Date endDate) {
        BigDecimal totalCost = BigDecimal.ZERO;
        Date currentDate = startDate;

        while (currentDate.before(endDate)) {
            RoomRateEntity applicableRate = null;
            List<RoomRateEntity> promoRates = roomRateEntitySessionBean.retrieveApplicablePromoRates(roomType, currentDate);
            List<RoomRateEntity> peakRates = roomRateEntitySessionBean.retrieveApplicablePeakRates(roomType, currentDate);

            if (!promoRates.isEmpty()) {
                applicableRate = promoRates.get(0);
            } else if (!peakRates.isEmpty()) {
                applicableRate = peakRates.get(0);
            } else {
                applicableRate = roomType.getNormalRate();
            }

            if (applicableRate != null) {
                totalCost = totalCost.add(applicableRate.getRatePerNight());
            }

            // Move to the next day
            currentDate = new Date(currentDate.getTime() + (1000 * 60 * 60 * 24));
        }

        return totalCost;
    }

    @Override
    public BigDecimal getPublishedRateForDates(RoomTypeEntity roomType, Date startDate, Date endDate) {
        Date currentDate = startDate;
        BigDecimal totalCost = BigDecimal.ZERO;
        RoomRateEntity applicableRate = roomType.getPublishedRate();

        while (currentDate.before(endDate)) {
            totalCost = totalCost.add(applicableRate.getRatePerNight());
            currentDate = new Date(currentDate.getTime() + (1000 * 60 * 60 * 24));
        }

        return totalCost;
    }

    @Override
    public long createNewRoomType(RoomTypeEntity newRoomType) throws InputDataValidationException, UnknownPersistenceException, AlreadyExistsException {
        try {
            Set<ConstraintViolation<RoomTypeEntity>> constraintViolations = validator.validate(newRoomType);
            if (constraintViolations.isEmpty()) {
                em.persist(newRoomType);
                em.flush();
                return newRoomType.getId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new AlreadyExistsException("Room Type already exists");
                }
            }
            throw new UnknownPersistenceException(ex.getMessage());

        }
    }

    @Override
    public List<RoomTypeEntity> retrieveAllRoomTypes() {
        return em.createQuery("SELECT rt FROM RoomTypeEntity rt", RoomTypeEntity.class).getResultList();
    }

    @Override
    public List<RoomTypeEntity> retrieveActiveRoomTypes() {
        return em.createNamedQuery("findActiveRoomTypes").getResultList();
    }

    @Override
    public void setRoomTypeRanking(int ranking, long roomId) {
        List<RoomTypeEntity> roomTypes = this.retrieveAllRoomTypes();
        RoomTypeEntity roomTypeToSetRanking = em.find(RoomTypeEntity.class, roomId);

        roomTypeToSetRanking.setRanking(ranking);

        for (RoomTypeEntity rt : roomTypes) {
            int currentRank = rt.getRanking();
            if (rt != roomTypeToSetRanking && currentRank >= ranking && !rt.isDisabled()) {
                rt.setRanking(currentRank + 1);
            }
        }
    }

    @Override
    public RoomTypeEntity retrieveRoomTypeByName(String roomTypeName) throws NoResultException {
        try {
            RoomTypeEntity roomType = em.createQuery("SELECT e FROM RoomTypeEntity e WHERE e.name = :roomName", RoomTypeEntity.class)
                    .setParameter("roomName", roomTypeName)
                    .getSingleResult();
            roomType.getRooms().size();
            roomType.getAllRates().size();
            roomType.getReservations().size();
            roomType.getRooms().size();
            return roomType;
        } catch (NoResultException e) {
            throw new NoResultException("No result found");
        }

    }

    @Override
    public void deleteRoomType(long roomTypeId) throws NoResultException {
        RoomTypeEntity roomTypeToDelete = em.find(RoomTypeEntity.class, roomTypeId);
        List<RoomTypeEntity> roomTypes = this.retrieveAllRoomTypes();

        for (RoomTypeEntity rt : roomTypes) {
            int currentRank = rt.getRanking();
            if (currentRank > roomTypeToDelete.getRanking() && !rt.isDisabled()) {
                rt.setRanking(currentRank - 1);
            }
        }

        if (!roomTypeToDelete.getRooms().isEmpty() || !roomTypeToDelete.getAllRates().isEmpty() || !roomTypeToDelete.getReservations().isEmpty()) {
            roomTypeToDelete.setDisabled(true);
        } else {
            em.remove(roomTypeToDelete);
        }

    }

    @Override
    public RoomTypeEntity updateRoomType(RoomTypeEntity roomType) throws InputDataValidationException {
        Set<ConstraintViolation<RoomTypeEntity>> constraintViolations = validator.validate(roomType);
        if (constraintViolations.isEmpty()) {
            em.merge(roomType);
            em.flush();
            return roomType;
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public int getAvailableRoomQuantity(Date startDate, Date endDate, RoomTypeEntity roomType) {
        int totalRooms = roomEntitySessionBean.retrieveActiveRoomsForType(roomType).size();
        int bookedRooms = 0;
        roomType.getAllRates().size();

        List<ReservationEntity> overlappingReservations = em.createNamedQuery("findOverlappingReservations")
                .setParameter("givenEndDate", endDate)
                .setParameter("givenStartDate", startDate)
                .setParameter("roomType", roomType)
                .getResultList();
        for (ReservationEntity reservation : overlappingReservations) {
            bookedRooms += reservation.getQuantity();
        }
        return Math.max(totalRooms - bookedRooms, 0);
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomTypeEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
