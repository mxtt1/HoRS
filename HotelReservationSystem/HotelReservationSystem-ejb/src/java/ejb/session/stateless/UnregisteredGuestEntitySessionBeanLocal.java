/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entities.UnregisteredGuestEntity;
import javax.ejb.Local;
import util.exception.AlreadyExistsException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Mark
 */
@Local
public interface UnregisteredGuestEntitySessionBeanLocal {

    public long createNewUnregisteredGuest(UnregisteredGuestEntity newUnregisteredGuest) throws UnknownPersistenceException, AlreadyExistsException;
    
}
