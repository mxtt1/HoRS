/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entities.UnregisteredGuestEntity;
import javax.ejb.Remote;
import util.exception.AlreadyExistsException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Mark
 */
@Remote
public interface UnregisteredGuestEntitySessionBeanRemote {
        public long createNewUnregisteredGuest(UnregisteredGuestEntity newUnregisteredGuest) throws UnknownPersistenceException, AlreadyExistsException;

}
