/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsreservationclient;

import ejb.session.stateless.GuestEntitySessionBeanRemote;
import ejb.session.stateless.PartnerEntitySessionBeanRemote;
import ejb.session.stateless.ReservationEntitySessionBeanRemote;
import ejb.session.stateless.RoomEntitySessionBeanRemote;
import ejb.session.stateless.RoomRateEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import ejb.session.stateless.UnregisteredGuestEntitySessionBeanRemote;
import entities.GuestEntity;
import entities.RoomTypeEntity;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private ReservationEntitySessionBeanRemote reservationEntitySessionBeanRemote;
    private UnregisteredGuestEntitySessionBeanRemote unregisteredGuestEntitySessionBeanRemote;

    private GuestEntity currentGuestEntity;
    
    private GuestModule guestModule;
    
    private static Scanner sc = new Scanner(System.in);
    

    public MainApp(GuestEntitySessionBeanRemote guestEntitySessionBeanRemote, RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote, PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote,
            RoomEntitySessionBeanRemote roomEntitySessionBean, RoomRateEntitySessionBeanRemote roomRateEntitySessionBean, 
            ReservationEntitySessionBeanRemote reservationEntitySessionBeanRemote, UnregisteredGuestEntitySessionBeanRemote unregisteredGuestEntitySessionBeanRemote) {
        this.guestEntitySessionBeanRemote = guestEntitySessionBeanRemote;
        this.roomTypeEntitySessionBeanRemote = roomTypeEntitySessionBeanRemote;
        this.partnerEntitySessionBeanRemote = partnerEntitySessionBeanRemote;
        this.roomEntitySessionBeanRemote = roomEntitySessionBeanRemote;
        this.roomRateEntitySessionBeanRemote = roomRateEntitySessionBeanRemote;
        this.reservationEntitySessionBeanRemote = reservationEntitySessionBeanRemote;
        this.unregisteredGuestEntitySessionBeanRemote = unregisteredGuestEntitySessionBeanRemote;
    }

    public void runApp() throws InvalidAccessRightException {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("\nWelcome to HoRS Reservation Client");
            System.out.println("1: Guest Login");
            System.out.println("2: Register As Guest");
            System.out.println("3: Search Hotel Room");
            System.out.println("4: Exit");
            response = 0;

            while (response < 1 || response > 99) {
                System.out.print("> ");
                response = sc.nextInt();
                sc.nextLine();
                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login Successful");
                        guestModule = new GuestModule(guestEntitySessionBeanRemote, roomTypeEntitySessionBeanRemote, partnerEntitySessionBeanRemote, currentGuestEntity, reservationEntitySessionBeanRemote, unregisteredGuestEntitySessionBeanRemote);
                        guestModule.menuSystemGuest();
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println(ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    doRegisterAsGuest();
                } else if (response == 3) {
                    doSearchHotelRoom();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid input, try again!");
                }
            }
            if (response == 4) {
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
        System.out.print("Enter username> ");
        username = sc.nextLine().trim();
        System.out.print("Enter password> ");
        password = sc.nextLine().trim();
        /*
        System.out.println("Choose one to input: ");
        System.out.println("1. Passport number");
        System.out.println("2. Email");
        System.out.println("3. Mobile phone number");
        int input = sc.nextInt();
        if (input == 1) {
            System.out.print("Enter passport number> ");
            passportNum = sc.nextLine().trim();
        } else if (input == 2) {
            System.out.print("Enter email> ");
            email = sc.nextLine().trim();
        } else if (input == 3) {
            System.out.print("Enter phone number> ");
            phoneNum = sc.nextLine().trim();
        } else {
            System.out.println("Invalid input!");
        }
        */
        System.out.print("Enter passport number> ");
        passportNum = sc.nextLine().trim();
        
        GuestEntity newGuest = new GuestEntity(username, passportNum, password);
        long newGuestId = guestEntitySessionBeanRemote.createNewGuest(newGuest);
        System.out.println("New guest account resgistered with username: " + username + " and id: " + newGuestId);
    }

    private void doSearchHotelRoom() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        
        Date startDate = null;
        Date endDate = null;
        
        while (startDate == null) {
            System.out.print("Enter start date (format: dd-MM-yyyy, e.g., 12-10-2002): ");
            String startInput = sc.nextLine();
            try {
                startDate = dateFormat.parse(startInput);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use dd-MM-yyyy.");
            }
        }

        while (endDate == null || endDate.before(startDate)) {
            System.out.print("Enter end date (format: dd-MM-yyyy, e.g., 12-10-2002): ");
            String endInput = sc.nextLine();
            try {
                endDate = dateFormat.parse(endInput);
                if (endDate.before(startDate)) {
                    System.out.println("End date must be after the start date.");
                }
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use dd-MM-yyyy.");
            }
        }
        
        List<RoomTypeEntity> availableRoomTypes = roomTypeEntitySessionBeanRemote.getAvailableRoomTypes(startDate, endDate);
        if (availableRoomTypes.isEmpty()) {
            System.out.println("\nNo rooms available for the selected date range.");
        } else {
            System.out.println("\nAvailable Room Types:");
            for (RoomTypeEntity roomType : availableRoomTypes) {
                System.out.print("\nName: " + roomType.getName());
                System.out.println(" Room Description: " + roomType.getDescription());
                System.out.println("Room Size: " + roomType.getRoomSize() + " square meters");
                System.out.println("Bed: " + roomType.getBedType());
                System.out.println("Capacity: " + roomType.getCapacity());
                System.out.println("Amenities: " + roomType.getAmenities());
                BigDecimal cost = roomTypeEntitySessionBeanRemote.getNormalRateForDates(roomType, startDate, endDate);
                System.out.print("Price: $" + cost);
                int quantity = roomTypeEntitySessionBeanRemote.getAvailableRoomQuantity(startDate, endDate, roomType);
                System.out.println(" Available Quantity: " + quantity);
            }
        }
        
        System.out.print("\nPress any key to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
