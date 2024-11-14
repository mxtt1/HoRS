/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entities.RoomRateEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import javax.persistence.NoResultException;
import util.exception.AlreadyExistsException;
import util.exception.EntityIsDisabledException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author mattl
 */
@Remote
public interface RoomRateEntitySessionBeanRemote {

    public long createNewPublishedNormalRate(RoomRateEntity newRoomRate, String roomType) throws EntityIsDisabledException, InputDataValidationException, AlreadyExistsException, UnknownPersistenceException;

    public long createNewPeakPromotionRate(RoomRateEntity newRoomRate, Date startDate, Date endDate, String roomType) throws EntityIsDisabledException, InputDataValidationException, AlreadyExistsException, UnknownPersistenceException;

    public RoomRateEntity retrieveRoomRateByName(String roomRateName) throws NoResultException;

    public void deleteRoomRate(long roomRateId);

    public List<RoomRateEntity> retrieveAllRoomRates();

    RoomRateEntity retrieveRoomRate(long roomRateId);

    RoomRateEntity updateRoomRate(RoomRateEntity newRoomRate)throws InputDataValidationException;
    
}
