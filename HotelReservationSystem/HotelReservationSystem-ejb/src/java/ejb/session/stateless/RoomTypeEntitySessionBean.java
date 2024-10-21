/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.RoomTypeEntity;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mattl
 */
@Stateless
public class RoomTypeEntitySessionBean implements RoomTypeEntitySessionBeanRemote, RoomTypeEntitySessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;


    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public long createNewRoomType(RoomTypeEntity newRoomType) {
        em.persist(newRoomType);
        return newRoomType.getId();
    }

    @Override
    public List<RoomTypeEntity> retrieveAllRoomTypes() {
        return em.createQuery("SELECT rt FROM RoomTypeEntity rt", RoomTypeEntity.class).getResultList();
    }

    @Override
    public void setRoomTypeRanking(int ranking, long roomId) {
        List<RoomTypeEntity> roomTypes = this.retrieveAllRoomTypes();
        RoomTypeEntity roomTypeToSetRanking = em.find(RoomTypeEntity.class, roomId);
        
        roomTypeToSetRanking.setRanking(ranking);
        
        for (RoomTypeEntity rt : roomTypes) {
            int currentRank = rt.getRanking();
            if (rt != roomTypeToSetRanking && currentRank >= ranking) rt.setRanking(currentRank + 1);
        }
    }
    
    
}
