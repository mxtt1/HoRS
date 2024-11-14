/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.GuestEntity;
import entities.RoomRateEntity;
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
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import util.enums.RateType;
import util.exception.EntityIsDisabledException;
import util.exception.InputDataValidationException;

/**
 *
 * @author mattl
 */
@Stateless
public class RoomRateEntitySessionBean implements RoomRateEntitySessionBeanRemote, RoomRateEntitySessionBeanLocal {

    @EJB
    private RoomTypeEntitySessionBeanLocal roomTypeEntitySessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Inject
    private Validator validator;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public long createNewPublishedNormalRate(RoomRateEntity newRoomRate, String roomType) throws EntityIsDisabledException, InputDataValidationException {
        Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(newRoomRate);
        if (constraintViolations.isEmpty()) {
            RoomTypeEntity roomTypeToBeAssigned = roomTypeEntitySessionBean.retrieveRoomTypeByName(roomType);

            newRoomRate.setRoomType(roomTypeToBeAssigned);
            if (roomTypeToBeAssigned.isDisabled()) {
                throw new EntityIsDisabledException("Error: room type is disabled!");
            }
            if (newRoomRate.getRateType() == RateType.NORMAL && roomTypeToBeAssigned.getNormalRate() == null) {
                roomTypeToBeAssigned.setNormalRate(newRoomRate);
                roomTypeToBeAssigned.getAllRates().add(newRoomRate);
                em.persist(newRoomRate);
                em.flush();
                return newRoomRate.getId();
            } else if (newRoomRate.getRateType() == RateType.NORMAL) {
                roomTypeToBeAssigned.getNormalRate().setRatePerNight(newRoomRate.getRatePerNight());
                return roomTypeToBeAssigned.getNormalRate().getId();
            } else if (newRoomRate.getRateType() == RateType.PUBLISHED && roomTypeToBeAssigned.getPublishedRate() == null) {
                roomTypeToBeAssigned.setPublishedRate(newRoomRate);
                roomTypeToBeAssigned.getAllRates().add(newRoomRate);
                em.persist(newRoomRate);
                em.flush();
                return newRoomRate.getId();
            } else {
                roomTypeToBeAssigned.getPublishedRate().setRatePerNight(newRoomRate.getRatePerNight());
                return roomTypeToBeAssigned.getPublishedRate().getId();
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public long createNewPeakPromotionRate(RoomRateEntity newRoomRate, Date startDate, Date endDate, String roomType) throws EntityIsDisabledException, InputDataValidationException{
        Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(newRoomRate);
        if (constraintViolations.isEmpty()) {
            RoomTypeEntity roomTypeToBeAssigned = roomTypeEntitySessionBean.retrieveRoomTypeByName(roomType);
            if (roomTypeToBeAssigned.isDisabled()) {
                throw new EntityIsDisabledException("Error: room type is disabled!");
            }
            newRoomRate.setRoomType(roomTypeToBeAssigned);
            roomTypeToBeAssigned.getAllRates().add(newRoomRate);

            newRoomRate.setStartDate(startDate);
            newRoomRate.setEndDate(endDate);

            em.persist(newRoomRate);
            em.flush();
            return newRoomRate.getId();
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public RoomRateEntity retrieveRoomRateByName(String roomRateName) throws NoResultException{
         RoomRateEntity roomRateEntity = em.createQuery("SELECT rr FROM RoomRateEntity rr WHERE rr.name = :roomRateName", RoomRateEntity.class)
             .setParameter("roomRateName", roomRateName)
             .getSingleResult();

        return roomRateEntity;
    }

    @Override
    public void deleteRoomRate(long roomRateId) {
        RoomRateEntity roomRateToBeDeleted = em.find(RoomRateEntity.class, roomRateId);

        if (roomRateToBeDeleted.getRoomType().getNormalRate() == roomRateToBeDeleted) {
            roomRateToBeDeleted.getRoomType().setNormalRate(null);
        } else if (roomRateToBeDeleted.getRoomType().getPublishedRate() == roomRateToBeDeleted) {
            roomRateToBeDeleted.getRoomType().setPublishedRate(null);
        }
        
        if (roomRateToBeDeleted.getReservations().isEmpty()) {
                    roomRateToBeDeleted.getRoomType().getAllRates().remove(roomRateToBeDeleted);
                    em.remove(roomRateToBeDeleted);
        } else {
            roomRateToBeDeleted.setDisabled(true);
        }
    }

    @Override
    public List<RoomRateEntity> retrieveAllRoomRates() {
        return em.createQuery("SELECT rr FROM RoomRateEntity rr", RoomRateEntity.class).getResultList();
    }

    @Override
    public List<RoomRateEntity> retrieveApplicablePromoRates(RoomTypeEntity roomType, Date date) {
        return em.createNamedQuery("findApplicablePromoRates", RoomRateEntity.class)
                .setParameter("roomType", roomType)
                .setParameter("givenDate", date)
                .setParameter("promotionRate", RateType.PROMOTION)
                .getResultList();
    }

    @Override
    public List<RoomRateEntity> retrieveApplicablePeakRates(RoomTypeEntity roomType, Date date) {
        return em.createNamedQuery("findApplicablePeakRates", RoomRateEntity.class)
                .setParameter("roomType", roomType)
                .setParameter("givenDate", date)
                .setParameter("peakRate", RateType.PEAK)
                .getResultList();
    }

    @Override
    public RoomRateEntity retrieveRoomRate(long roomRateId) {
        return em.find(RoomRateEntity.class, roomRateId);
    }

    @Override
    public RoomRateEntity updateRoomRate(RoomRateEntity newRoomRate) throws InputDataValidationException{
        Set<ConstraintViolation<RoomRateEntity>> constraintViolations = validator.validate(newRoomRate);
        if (constraintViolations.isEmpty()) {
            em.merge(newRoomRate);
            em.flush();
            return newRoomRate;
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomRateEntity>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}