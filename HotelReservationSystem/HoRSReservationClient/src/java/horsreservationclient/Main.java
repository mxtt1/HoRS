/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package horsreservationclient;

import ejb.session.stateless.EmployeeEntitySessionBeanRemote;
import ejb.session.stateless.GuestEntitySessionBeanRemote;
import ejb.session.stateless.PartnerEntitySessionBeanRemote;
import ejb.session.stateless.ReservationEntitySessionBeanRemote;
import ejb.session.stateless.RoomEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import javax.ejb.EJB;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;
import ejb.session.stateless.RoomRateEntitySessionBeanRemote;
import ejb.session.stateless.UnregisteredGuestEntitySessionBeanRemote;
import horsreservationclient.MainApp;

/**
 *
 * @author mattl
 */
public class Main {


    @EJB(name = "UnregisteredGuestEntitySessionBeanRemote")
    private static UnregisteredGuestEntitySessionBeanRemote unregisteredGuestEntitySessionBeanRemote;

    @EJB(name = "PartnerEntitySessionBeanRemote")

    private static PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote;

    @EJB(name = "ReservationEntitySessionBeanRemote")
    private static ReservationEntitySessionBeanRemote reservationEntitySessionBeanRemote;

    @EJB(name = "GuestEntitySessionBeanRemote")
    private static GuestEntitySessionBeanRemote guestEntitySessionBeanRemote;

    @EJB(name = "RoomRateEntitySessionBeanRemote")
    private static RoomRateEntitySessionBeanRemote roomRateEntitySessionBean;

    @EJB(name = "RoomEntitySessionBeanRemote")
    private static RoomEntitySessionBeanRemote roomEntitySessionBean;

    @EJB(name = "RoomTypeEntitySessionBeanRemote")
    private static RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;
    
    
    
    
    
    
    public static void main(String[] args) throws InvalidLoginCredentialException, InvalidAccessRightException {
        MainApp mainApp = new MainApp(guestEntitySessionBeanRemote, roomTypeEntitySessionBeanRemote, partnerEntitySessionBeanRemote,
            roomEntitySessionBean, roomRateEntitySessionBean, reservationEntitySessionBeanRemote, unregisteredGuestEntitySessionBeanRemote);

        mainApp.runApp();
    }
    
}
