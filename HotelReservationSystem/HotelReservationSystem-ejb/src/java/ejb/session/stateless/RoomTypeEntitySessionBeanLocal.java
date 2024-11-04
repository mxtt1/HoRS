/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entities.RoomTypeEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author mattl
 */
@Local
public interface RoomTypeEntitySessionBeanLocal {

    long createNewRoomType(RoomTypeEntity newRoomType);

    List<RoomTypeEntity> retrieveAllRoomTypes();

    void deleteRoomType(long roomTypeId);

    public RoomTypeEntity retrieveRoomTypeByName(String roomTypeName);

    public RoomTypeEntity updateRoomType(RoomTypeEntity roomType);

    public List<RoomTypeEntity> getAvailableRoomTypes(Date startDate, Date endDate);

    
    public int getNormalRateForDates(RoomTypeEntity roomType, Date startDate, Date endDate);
    
}
