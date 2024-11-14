/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.PartnerEntity;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.exception.AlreadyExistsException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Mark
 */
@Stateless
public class PartnerEntitySessionBean implements PartnerEntitySessionBeanRemote, PartnerEntitySessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public long createNewPartner(PartnerEntity newPartner) throws AlreadyExistsException, UnknownPersistenceException {
        try {
        em.persist(newPartner);
        em.flush();
        return newPartner.getPartnerEntityId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new AlreadyExistsException("Partner already exists");
                }
            }
            throw new UnknownPersistenceException(ex.getMessage());

        }
    }
    
    @Override
    public List<PartnerEntity> retrieveAllPartners() {
        return em.createQuery("SELECT r FROM PartnerEntity r", PartnerEntity.class).getResultList();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
