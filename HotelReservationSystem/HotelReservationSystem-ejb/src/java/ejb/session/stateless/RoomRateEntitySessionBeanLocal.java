/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entities.RoomRateEntity;
import entities.RoomTypeEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.EntityIsDisabledException;

/**
 *
 * @author mattl
 */
@Local
public interface RoomRateEntitySessionBeanLocal {

    void deleteRoomRate(long roomRateId);
    
    public long createNewPublishedNormalRate(RoomRateEntity newRoomRate, String roomType)throws EntityIsDisabledException;

    public long createNewPeakPromotionRate(RoomRateEntity newRoomRate, Date startDate, Date endDate, String roomType)throws EntityIsDisabledException;

    public List<RoomRateEntity> retrieveAllRoomRates();

    public List<RoomRateEntity> retrieveApplicablePromoRates(RoomTypeEntity roomType, Date date);

    public List<RoomRateEntity> retrieveApplicablePeakRates(RoomTypeEntity roomType, Date date);

    RoomRateEntity retrieveRoomRate(long roomRateId);

    RoomRateEntity updateRoomRate(RoomRateEntity newRoomRate);
    
}
