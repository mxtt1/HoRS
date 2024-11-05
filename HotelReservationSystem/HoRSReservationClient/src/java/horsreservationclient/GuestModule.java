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

/**
 *
 * @author Mark
 */
class GuestModule {

    private GuestEntitySessionBeanRemote guestEntitySessionBeanRemote;
    private RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;
    private PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote;
    private RoomEntitySessionBeanRemote roomEntitySessionBeanRemote;
    private RoomRateEntitySessionBeanRemote roomRateEntitySessionBeanRemote;
    private ReservationEntitySessionBeanRemote reservationEntitySessionBeanRemote;
    
    private GuestEntity currentGuestEntity;
    
    private final Scanner sc = new Scanner(System.in);
    
    GuestModule(GuestEntitySessionBeanRemote guestEntitySessionBeanRemote, RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote, 
            PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote, GuestEntity currentGuestEntity, ReservationEntitySessionBeanRemote reservationEntitySessionBeanRemote) {
        this.guestEntitySessionBeanRemote = guestEntitySessionBeanRemote;
        this.roomTypeEntitySessionBeanRemote = roomTypeEntitySessionBeanRemote;
        this.partnerEntitySessionBeanRemote = partnerEntitySessionBeanRemote;
        this.roomEntitySessionBeanRemote = roomEntitySessionBeanRemote;
        this.roomRateEntitySessionBeanRemote = roomRateEntitySessionBeanRemote;
        this.reservationEntitySessionBeanRemote = reservationEntitySessionBeanRemote;
        this.currentGuestEntity = currentGuestEntity;
    }
    
    public void menuSystemGuest() throws InvalidAccessRightException {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        while (true) {
            System.out.println("\nHoRS System :: Reservation Client");
            System.out.println("1. Search Hotel Room");
            System.out.println("2. Reserve Hotel Room");
            System.out.println("99. Exit");
            response = 0;

            while (response < 1 || response > 99) {
                System.out.print("> ");
                response = sc.nextInt();
                if (response == 1) {
                    doSearchHotelRoom();
                } else if (response == 2) {
                    doReserveHotelRoom();
                } else if (response == 99) {
                    break;
                } else {
                    System.out.println("Invalid input, try again!");
                }
            }
            if (response == 99) {
                break;
            }
        }
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
            System.out.println("No rooms available for the selected date range.");
        } else {
            System.out.println("Available Room Types:");
            for (RoomTypeEntity roomType : availableRoomTypes) {
                System.out.print(" Name: " + roomType.getName());
                System.out.println("Room Name: " + roomType.getName());
                System.out.println("Room Description: " + roomType.getDescription());
                System.out.println("Room Size: " + roomType.getRoomSize() + " square meters");
                System.out.println("Bed: " + roomType.getBedType());
                System.out.println("Capacity: " + roomType.getCapacity());
                System.out.println("Amenities: " + roomType.getAmenities());
                BigDecimal cost = roomTypeEntitySessionBeanRemote.getNormalRateForDates(roomType, startDate, endDate);
                System.out.print(" Price: $" + cost);
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
    private void doReserveHotelRoom() {
        
    }
}
