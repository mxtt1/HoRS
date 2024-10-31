/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsreservationclient;

import ejb.session.stateless.GuestEntitySessionBeanRemote;
import ejb.session.stateless.PartnerEntitySessionBeanRemote;
import ejb.session.stateless.RoomEntitySessionBeanRemote;
import ejb.session.stateless.RoomRateEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import entities.GuestEntity;
import java.util.Scanner;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author Mark
 */
public class MainApp {

    private GuestEntitySessionBeanRemote guestEntitySessionBeanRemote;
    private RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;
    private PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote;
    private RoomEntitySessionBeanRemote roomEntitySessionBeanRemote;
    private RoomRateEntitySessionBeanRemote roomRateEntitySessionBeanRemote;

    private GuestEntity currentGuestEntity;
    
    private GuestModule guestModule;
    private final Scanner sc = new Scanner(System.in);

    public MainApp(GuestEntitySessionBeanRemote guestEntitySessionBeanRemote, RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote, PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote, RoomEntitySessionBeanRemote roomEntitySessionBean, RoomRateEntitySessionBeanRemote roomRateEntitySessionBean) {
        this.guestEntitySessionBeanRemote = guestEntitySessionBeanRemote;
        this.roomTypeEntitySessionBeanRemote = roomTypeEntitySessionBeanRemote;
        this.partnerEntitySessionBeanRemote = partnerEntitySessionBeanRemote;
        this.roomEntitySessionBeanRemote = roomEntitySessionBeanRemote;
        this.roomRateEntitySessionBeanRemote = roomRateEntitySessionBeanRemote;
    }

    public void runApp() throws InvalidAccessRightException {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("\nWelcome to HoRS Reservation Client");
            System.out.println("1: Guest Login");
            System.out.println("2: Register As Guest");
            System.out.println("3: Exit");
            response = 0;

            while (response < 1 || response > 99) {
                System.out.print("> ");
                response = sc.nextInt();
                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login Successful");
                        guestModule = new GuestModule(guestEntitySessionBeanRemote, roomTypeEntitySessionBeanRemote, partnerEntitySessionBeanRemote, currentGuestEntity);
                        guestModule.menuSystemGuest();
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println(ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    doRegisterAsGuest();
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Invalid input, try again!");
                }
            }
            if (response == 3) {
                break;
            }
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException {
        String username = "";
        String password = "";
        
        System.out.println("Login: ");
        System.out.print("Enter username> ");
        username = sc.nextLine().trim();
        System.out.print("Enter password> ");
        password = sc.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0)
        {  
            currentGuestEntity = guestEntitySessionBeanRemote.guestLogin(username, password);
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    private void doRegisterAsGuest() throws InvalidAccessRightException {
        String username = "";
        String password = "";
        String email = "";
        String passportNum = "";
        String phoneNum = "";
        
        System.out.println("Register As Guest: ");
        System.out.println("Enter username>");
        username = sc.nextLine().trim();
        System.out.println("Enter password>");
        password = sc.nextLine().trim();
        System.out.println("Enter email>");
        email = sc.nextLine().trim();
        System.out.println("Enter passport number>");
        passportNum = sc.nextLine().trim();
        System.out.println("Enter phone number>");
        phoneNum = sc.nextLine().trim();
        
        GuestEntity newGuest = new GuestEntity(username, email, passportNum, phoneNum, password);
        long newGuestId = guestEntitySessionBeanRemote.createNewGuest(newGuest);
        System.out.println("New guest account resgistered with username: " + username + " and id: " + newGuestId);
        runApp();
    }

}
