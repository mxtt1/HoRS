/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entities.GuestEntity;
import entities.UnregisteredGuestEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UserAlreadyRegisteredException;

/**
 *
 * @author Mark
 */
@Remote
public interface GuestEntitySessionBeanRemote {

    public long createNewGuest(GuestEntity newGuest) throws UserAlreadyRegisteredException, InputDataValidationException;
    
    public GuestEntity retrieveGuestByUsername(String username);
    
    public GuestEntity guestLogin(String username, String password) throws InvalidLoginCredentialException;

    public List<UnregisteredGuestEntity> retrieveGuestByPassportNo(String passportNo);

}
