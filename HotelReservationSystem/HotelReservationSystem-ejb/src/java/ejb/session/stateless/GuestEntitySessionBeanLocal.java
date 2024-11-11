/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entities.GuestEntity;
import entities.UnregisteredGuestEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author Mark
 */
@Local
public interface GuestEntitySessionBeanLocal {

    public long createNewGuest(GuestEntity newGuest);

    public GuestEntity retrieveGuestByUsername(String username);

    public GuestEntity guestLogin(String username, String password) throws InvalidLoginCredentialException;

    public List<UnregisteredGuestEntity> retrieveGuestByPassportNo(String passportNo);

}
