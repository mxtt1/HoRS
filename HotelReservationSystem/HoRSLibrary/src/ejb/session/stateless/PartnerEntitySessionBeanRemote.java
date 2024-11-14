/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entities.PartnerEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.AlreadyExistsException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Mark
 */
@Remote
public interface PartnerEntitySessionBeanRemote {
    
    public long createNewPartner(PartnerEntity newPartner) throws AlreadyExistsException, UnknownPersistenceException;

    public List<PartnerEntity> retrieveAllPartners();
}
