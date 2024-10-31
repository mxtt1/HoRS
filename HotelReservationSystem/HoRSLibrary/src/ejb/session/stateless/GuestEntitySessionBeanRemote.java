/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entities.GuestEntity;
import javax.ejb.Remote;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author Mark
 */
@Remote
public interface GuestEntitySessionBeanRemote {

    public long createNewGuest(GuestEntity newGuest);
    public GuestEntity retrieveGuestByUsername(String username);
    public GuestEntity guestLogin(String username, String password) throws InvalidLoginCredentialException;


}
