/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entities.RoomTypeEntity;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author mattl
 */
@Remote
public interface RoomTypeEntitySessionBeanRemote {

    long createNewRoomType(RoomTypeEntity newRoomType);

    List<RoomTypeEntity> retrieveAllRoomTypes();

    void setRoomTypeRanking(int ranking, long roomId);

    void deleteRoomType(long roomTypeId);

    public RoomTypeEntity retrieveRoomTypeByName(String roomTypeName);

    public RoomTypeEntity updateRoomType(RoomTypeEntity roomType);

    public List<RoomTypeEntity> getAvailableRoomTypes(Date startDate, Date endDate);

    public BigDecimal getNormalRateForDates(RoomTypeEntity roomType, Date startDate, Date endDate);

    int getAvailableRoomQuantity(Date startDate, Date endDate, RoomTypeEntity roomType);

}
