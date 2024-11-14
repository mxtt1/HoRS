/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entities.RoomTypeEntity;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.AlreadyExistsException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author mattl
 */
@Local
public interface RoomTypeEntitySessionBeanLocal {

    public long createNewRoomType(RoomTypeEntity newRoomType) throws InputDataValidationException, UnknownPersistenceException, AlreadyExistsException;

    List<RoomTypeEntity> retrieveAllRoomTypes();
    
    void setRoomTypeRanking(int ranking, long roomId);

    void deleteRoomType(long roomTypeId);

    public RoomTypeEntity retrieveRoomTypeByName(String roomTypeName);

    public RoomTypeEntity updateRoomType(RoomTypeEntity roomType)throws InputDataValidationException;

    public List<RoomTypeEntity> getAvailableRoomTypes(Date startDate, Date endDate);

    public BigDecimal getNormalRateForDates(RoomTypeEntity roomType, Date startDate, Date endDate);

    public List<RoomTypeEntity> retrieveActiveRoomTypes();

    int getAvailableRoomQuantity(Date startDate, Date endDate, RoomTypeEntity roomType);

    public BigDecimal getPublishedRateForDates(RoomTypeEntity roomType, Date startDate, Date endDate);
    
}
