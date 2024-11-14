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
import entities.ReservationEntity;
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
import javax.persistence.NoResultException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;

/**
 *
 * @author Mark
 */
class GuestModule {

    private GuestEntitySessionBeanRemote guestEntitySessionBeanRemote;
    private RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;
    private PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote;
    private ReservationEntitySessionBeanRemote reservationEntitySessionBeanRemote;
    private UnregisteredGuestEntitySessionBeanRemote unregisteredGuestEntitySessionBeanRemote;

    private GuestEntity currentGuestEntity;

    private static Scanner sc = new Scanner(System.in);

    GuestModule(GuestEntitySessionBeanRemote guestEntitySessionBeanRemote, RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote,
            PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote, GuestEntity currentGuestEntity,
            ReservationEntitySessionBeanRemote reservationEntitySessionBeanRemote, UnregisteredGuestEntitySessionBeanRemote unregisteredGuestEntitySessionBeanRemote) {
        this.guestEntitySessionBeanRemote = guestEntitySessionBeanRemote;
        this.roomTypeEntitySessionBeanRemote = roomTypeEntitySessionBeanRemote;
        this.partnerEntitySessionBeanRemote = partnerEntitySessionBeanRemote;
        this.reservationEntitySessionBeanRemote = reservationEntitySessionBeanRemote;
        this.currentGuestEntity = currentGuestEntity;
        this.unregisteredGuestEntitySessionBeanRemote = unregisteredGuestEntitySessionBeanRemote;
    }

    public void menuSystemGuest() throws InvalidAccessRightException {
        Integer response = 0;
        while (true) {
            System.out.println("\nHoRS System :: Reservation Client");
            System.out.println("1. Search Hotel Room");
            System.out.println("2. Reserve Hotel Room");
            System.out.println("3. View My Reservation Details");
            System.out.println("4. View All My Reservations");
            System.out.println("99. Logout");
            response = 0;

            while (response < 1 || response > 99) {
                System.out.print("> ");
                response = sc.nextInt();
                sc.nextLine();
                if (response == 1) {
                    doSearchHotelRoom();
                } else if (response == 2) {
                    doReserveHotelRoom();
                } else if (response == 3) {
                    doViewMyReservationDetails();
                } else if (response == 4) {
                    doViewAllGuestReservations();
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
            System.out.println("\nNo rooms available for the selected date range.");
        } else {
            System.out.println("\nAvailable Room Types:");
            for (RoomTypeEntity roomType : availableRoomTypes) {
                System.out.println("\nName: " + roomType.getName());
                System.out.println(" | Room Description: " + roomType.getDescription());
                System.out.println("Room Size: " + roomType.getRoomSize() + " square meters");
                System.out.println("Bed: " + roomType.getBedType());
                System.out.println("Capacity: " + roomType.getCapacity());
                System.out.println("Amenities: " + roomType.getAmenities());
                BigDecimal cost = roomTypeEntitySessionBeanRemote.getNormalRateForDates(roomType, startDate, endDate);
                System.out.print("Price: $" + cost);
                int quantity = roomTypeEntitySessionBeanRemote.getAvailableRoomQuantity(startDate, endDate, roomType);
                System.out.println(" | Available Quantity: " + quantity);
            }
        }

        System.out.print("\nPress enter to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean doSearchHotelRoom(Date sd, Date ed) {

        Date startDate = sd;
        Date endDate = ed;
        List<RoomTypeEntity> availableRoomTypes = roomTypeEntitySessionBeanRemote.getAvailableRoomTypes(startDate, endDate);
        if (availableRoomTypes.isEmpty()) {
            System.out.println("\nNo rooms available for the selected date range.");
            return false;
        } else {
            System.out.println("\nAvailable Room Types:");
            for (RoomTypeEntity roomType : availableRoomTypes) {
                System.out.println("\nName: " + roomType.getName());
                System.out.println(" | Room Description: " + roomType.getDescription());
                System.out.println("Room Size: " + roomType.getRoomSize() + " square meters");
                System.out.println("Bed: " + roomType.getBedType());
                System.out.println("Capacity: " + roomType.getCapacity());
                System.out.println("Amenities: " + roomType.getAmenities());
                BigDecimal cost = roomTypeEntitySessionBeanRemote.getNormalRateForDates(roomType, startDate, endDate);
                System.out.print("Price: $" + cost);
                int quantity = roomTypeEntitySessionBeanRemote.getAvailableRoomQuantity(startDate, endDate, roomType);
                System.out.println(" | Available Quantity: " + quantity);
            }
        }

        System.out.print("\nPress enter to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private void doReserveHotelRoom() {

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

        boolean hasAvailableRoomTypes = doSearchHotelRoom(startDate, endDate);

        if (hasAvailableRoomTypes) {
            System.out.print("Input Name of Room Type you wish to book> ");
            String roomTypeName = sc.nextLine().trim();
            try {
                RoomTypeEntity roomType = roomTypeEntitySessionBeanRemote.retrieveRoomTypeByName(roomTypeName);
                int quantity = roomTypeEntitySessionBeanRemote.getAvailableRoomQuantity(startDate, endDate, roomType);
                System.out.println("Available Quantity: " + quantity);
                System.out.print("Input quantity of rooms to book> ");
                int bookingQuantity = sc.nextInt();
                while (bookingQuantity > quantity) {
                    System.out.println("Booking quantity cannot exceed available quantity.");
                    System.out.print("Please input a valid quantity> ");
                    bookingQuantity = sc.nextInt();
                }
                sc.nextLine();
                long guestId = currentGuestEntity.getId();
                ReservationEntity newReservation = new ReservationEntity(startDate, endDate, bookingQuantity);
                long newReservationId = 0l;
                try {
                    newReservationId = reservationEntitySessionBeanRemote.createNewOnlineReservation(newReservation, guestId, roomType.getId());
                } catch (InputDataValidationException ex) {
                    System.out.println(ex.getMessage() + "\n");
                }

                Date now = new Date();
                Date reservationStart = newReservation.getStartDate();
                boolean isPast2AM = now.getHours() >= 2;

                // check same day and past 2am
                if (now.getYear() == reservationStart.getYear() && now.getMonth() == reservationStart.getMonth()
                        && now.getDate() == reservationStart.getDate() && isPast2AM) {
                    reservationEntitySessionBeanRemote.allocateRoomsToReservation(newReservationId);
                }

                System.out.println("Reservation Successful!");

            } catch (NoResultException e) {
                System.out.println(e.getMessage());
            }
        } else {
            return;
        }
    }

    private void doViewAllGuestReservations() {
        System.out.println("\nViewing All Reservations:\n");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        List<ReservationEntity> reservations = reservationEntitySessionBeanRemote.retrieveAllReservationsForGuest(this.currentGuestEntity.getId());

        for (ReservationEntity re : reservations) {
            String output = "ID: " + re.getId() + " | Check-in Date: " + dateFormat.format(re.getStartDate()) + " | Check-out Date: " + dateFormat.format(re.getEndDate())
                    + " | Room Type: " + re.getRoomType().getName();
            System.out.println(output);
        }

        System.out.println("\nPress enter to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(GuestModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doViewMyReservationDetails() {
        doViewAllGuestReservations();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        System.out.print("Enter ID of reservation to view details> ");
        long reservationId = sc.nextInt();
        sc.nextLine();
        ReservationEntity reservation = reservationEntitySessionBeanRemote.retrieveReservation(reservationId);

        if (reservation.getOccupant().getPassportNum().equals(this.currentGuestEntity.getPassportNum())) {
            System.out.println("Room Type: " + reservation.getRoomType().getName());
            System.out.println("Check-in Date: " + dateFormat.format(reservation.getStartDate()));
            System.out.println("Check-out Date: " + dateFormat.format(reservation.getEndDate()));
            System.out.println("Total Fee: $" + reservation.getFee());
            System.out.println("Number of Rooms: " + reservation.getQuantity());
            System.out.println("Press enter to continue.");
            try {
                System.in.read();
            } catch (IOException ex) {
                Logger.getLogger(GuestModule.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.out.println("Error: Reservation ID does not exist, please try again!");
        }
    }
}
