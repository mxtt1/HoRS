/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeEntitySessionBeanLocal;
import ejb.session.stateless.RoomEntitySessionBeanLocal;
import ejb.session.stateless.RoomRateEntitySessionBeanLocal;
import ejb.session.stateless.RoomTypeEntitySessionBeanLocal;
import entities.EmployeeEntity;
import entities.RoomEntity;
import entities.RoomRateEntity;
import entities.RoomTypeEntity;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.enums.EmployeeRole;
import util.enums.RateType;
import util.exception.EntityIsDisabledException;

/**
 *
 * @author Mark
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private RoomEntitySessionBeanLocal roomEntitySessionBeanLocal;

    @EJB
    private RoomRateEntitySessionBeanLocal roomRateEntitySessionBeanLocal;

    @EJB
    private RoomTypeEntitySessionBeanLocal roomTypeEntitySessionBeanLocal;

    @EJB(name = "EmployeeEntitySessionBeanLocal")
    private EmployeeEntitySessionBeanLocal employeeEntitySessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct() {
        if (em.find(EmployeeEntity.class, 1l)  == null) {
            initializeData();
        }
        
    }

    private void initializeData() {
        employeeEntitySessionBeanLocal.createNewEmployee(new EmployeeEntity(EmployeeRole.SYSTEM_ADMIN, "sysadmin", "password", "administrator"));
        employeeEntitySessionBeanLocal.createNewEmployee(new EmployeeEntity(EmployeeRole.OPS_MANAGER, "opmanager", "password", "operator"));
        employeeEntitySessionBeanLocal.createNewEmployee(new EmployeeEntity(EmployeeRole.SALES_MANAGER, "salesmanager", "password", "sales"));
        employeeEntitySessionBeanLocal.createNewEmployee(new EmployeeEntity(EmployeeRole.GRO, "guestrelo", "password", "guest relations"));
        
        long deluxeId =roomTypeEntitySessionBeanLocal.createNewRoomType(new RoomTypeEntity("Deluxe Room", "NIL", "NIL", "NIL", 2, 20));
        long premierId = roomTypeEntitySessionBeanLocal.createNewRoomType(new RoomTypeEntity("Premier Room", "NIL", "NIL", "NIL", 2, 20));
        long familyId = roomTypeEntitySessionBeanLocal.createNewRoomType(new RoomTypeEntity("Family Room", "NIL", "NIL", "NIL", 2, 20));
        long juniorId = roomTypeEntitySessionBeanLocal.createNewRoomType(new RoomTypeEntity("Junior Suite", "NIL", "NIL", "NIL", 2, 20));
        long grandId = roomTypeEntitySessionBeanLocal.createNewRoomType(new RoomTypeEntity("Grand Suite", "NIL", "NIL", "NIL", 2, 20));
        
        roomTypeEntitySessionBeanLocal.setRoomTypeRanking(1, grandId);
        roomTypeEntitySessionBeanLocal.setRoomTypeRanking(2, juniorId);
        roomTypeEntitySessionBeanLocal.setRoomTypeRanking(3, familyId);
        roomTypeEntitySessionBeanLocal.setRoomTypeRanking(4, premierId);
        roomTypeEntitySessionBeanLocal.setRoomTypeRanking(5, deluxeId);
        
        try {
            roomRateEntitySessionBeanLocal.createNewPublishedNormalRate(new RoomRateEntity("Deluxe Room Published", RateType.PUBLISHED, BigDecimal.valueOf(100)), "Deluxe Room");
            roomRateEntitySessionBeanLocal.createNewPublishedNormalRate(new RoomRateEntity("Deluxe Room Normal", RateType.NORMAL, BigDecimal.valueOf(50)), "Deluxe Room");
            roomRateEntitySessionBeanLocal.createNewPublishedNormalRate(new RoomRateEntity("Premier Room Published", RateType.PUBLISHED, BigDecimal.valueOf(200)), "Premier Room");
            roomRateEntitySessionBeanLocal.createNewPublishedNormalRate(new RoomRateEntity("Premier Room Normal", RateType.NORMAL, BigDecimal.valueOf(100)), "Premier Room");
            roomRateEntitySessionBeanLocal.createNewPublishedNormalRate(new RoomRateEntity("Family Room Published", RateType.PUBLISHED, BigDecimal.valueOf(300)), "Family Room");
            roomRateEntitySessionBeanLocal.createNewPublishedNormalRate(new RoomRateEntity("Family Room Normal", RateType.NORMAL, BigDecimal.valueOf(150)), "Family Room");
            roomRateEntitySessionBeanLocal.createNewPublishedNormalRate(new RoomRateEntity("Junior Suite Published", RateType.PUBLISHED, BigDecimal.valueOf(400)), "Junior Suite");
            roomRateEntitySessionBeanLocal.createNewPublishedNormalRate(new RoomRateEntity("Junior Suite Normal", RateType.NORMAL, BigDecimal.valueOf(200)), "Junior Suite");
            roomRateEntitySessionBeanLocal.createNewPublishedNormalRate(new RoomRateEntity("Grand Suite Published", RateType.PUBLISHED, BigDecimal.valueOf(500)), "Grand Suite");
            roomRateEntitySessionBeanLocal.createNewPublishedNormalRate(new RoomRateEntity("Grand Suite Normal", RateType.NORMAL, BigDecimal.valueOf(250)), "Grand Suite");
        } catch (EntityIsDisabledException ex) {
            System.err.println(ex.getMessage());
        }
        
        try {
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0101"), "Deluxe Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0201"), "Deluxe Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0301"), "Deluxe Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0401"), "Deluxe Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0501"), "Deluxe Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0102"), "Premier Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0202"), "Premier Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0302"), "Premier Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0402"), "Premier Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0502"), "Premier Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0103"), "Family Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0203"), "Family Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0303"), "Family Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0403"), "Family Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0503"), "Family Room");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0104"), "Junior Suite");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0204"), "Junior Suite");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0304"), "Junior Suite");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0404"), "Junior Suite");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0504"), "Junior Suite");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0105"), "Grand Suite");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0205"), "Grand Suite");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0305"), "Grand Suite");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0405"), "Grand Suite");
        roomEntitySessionBeanLocal.createNewRoom(new RoomEntity("0505"), "Grand Suite");
        } catch (EntityIsDisabledException | NoResultException ex) {
            System.err.println(ex.getMessage());
        }
        
    }   
    
}
