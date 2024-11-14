/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeEntitySessionBeanRemote;
import ejb.session.stateless.GuestEntitySessionBeanRemote;
import ejb.session.stateless.ReservationEntitySessionBeanRemote;
import ejb.session.stateless.RoomEntitySessionBeanRemote;
import ejb.session.stateless.RoomRateEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import ejb.session.stateless.UnregisteredGuestEntitySessionBeanRemote;
import entities.EmployeeEntity;
import entities.GuestEntity;
import entities.ReservationEntity;
import entities.ReservationRoomEntity;
import entities.RoomTypeEntity;
import entities.UnregisteredGuestEntity;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.persistence.NoResultException;
import util.enums.EmployeeRole;
import util.enums.RoomStatus;
import util.exception.AlreadyExistsException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Mark
 */
class FrontOfficeModule {

    private EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote;
    private RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;
    private RoomEntitySessionBeanRemote roomEntitySessionBeanRemote;
    private RoomRateEntitySessionBeanRemote roomRateEntitySessionBeanRemote;
    private GuestEntitySessionBeanRemote guestEntitySessionBeanRemote;
    private UnregisteredGuestEntitySessionBeanRemote unregisteredGuestEntitySessionBeanRemote;
    private ReservationEntitySessionBeanRemote reservationEntitySessionBeanRemote;

    private static Scanner sc = new Scanner(System.in);

    private EmployeeEntity currentEmployeeEntity;

    public FrontOfficeModule() {

    }

    FrontOfficeModule(EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote, RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote,
            RoomEntitySessionBeanRemote roomEntitySessionBeanRemote, EmployeeEntity currentEmployeeEntity,
            RoomRateEntitySessionBeanRemote roomRateEntitySessionBeanRemote, GuestEntitySessionBeanRemote guestEntitySessionBeanRemote,
            UnregisteredGuestEntitySessionBeanRemote unregisteredGuestEntitySessionBeanRemote, ReservationEntitySessionBeanRemote reservationEntitySessionBeanRemote) {
        this();
        this.employeeEntitySessionBeanRemote = employeeEntitySessionBeanRemote;
        this.roomTypeEntitySessionBeanRemote = roomTypeEntitySessionBeanRemote;
        this.roomEntitySessionBeanRemote = roomEntitySessionBeanRemote;
        this.roomRateEntitySessionBeanRemote = roomRateEntitySessionBeanRemote;
        this.currentEmployeeEntity = currentEmployeeEntity;
        this.guestEntitySessionBeanRemote = guestEntitySessionBeanRemote;
        this.unregisteredGuestEntitySessionBeanRemote = unregisteredGuestEntitySessionBeanRemote;
        this.reservationEntitySessionBeanRemote = reservationEntitySessionBeanRemote;
    }

    void menuGuestRelation() throws InvalidAccessRightException {
        if (currentEmployeeEntity.getEmployeeRole() != EmployeeRole.GRO) {
            throw new InvalidAccessRightException("You don't have rights to access the guest relation module.");
        }

        Integer response = 0;
        while (true) {
            System.out.println("\nHoRS System :: Guest Relations");
            System.out.println("1: Walk-in Search Room");
            System.out.println("2: Walk-in Reserve Room");
            System.out.println("3: Check-in Guest");
            System.out.println("4: Check-out Guest");
            System.out.println("5: Exit");

            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");
                response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    doWalkInSearchRoom();
                } else if (response == 2) {
                    doReserveHotelRoom();
                } else if (response == 3) {
                    doCheckInGuest();
                } else if (response == 4) {
                    doCheckOutGuest();
                } else if (response == 5) {
                    break;
                } else {
                    System.out.println("Invalid input, try again!");
                }
            }
            if (response == 5) {
                break;
            }
        }
    }

    private void doWalkInSearchRoom() {

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
                System.out.println(" Room Description: " + roomType.getDescription());
                System.out.println("Room Size: " + roomType.getRoomSize() + " square meters");
                System.out.println("Bed: " + roomType.getBedType());
                System.out.println("Capacity: " + roomType.getCapacity());
                System.out.println("Amenities: " + roomType.getAmenities());
                BigDecimal cost = roomTypeEntitySessionBeanRemote.getPublishedRateForDates(roomType, startDate, endDate);
                System.out.print("Price: $" + cost);
                int quantity = roomTypeEntitySessionBeanRemote.getAvailableRoomQuantity(startDate, endDate, roomType);
                System.out.println(" Available Quantity: " + quantity);
            }
        }

        System.out.print("\nPress enter to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean doWalkInSearchRoom(Date sd, Date ed) {

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
                System.out.println(" Room Description: " + roomType.getDescription());
                System.out.println("Room Size: " + roomType.getRoomSize() + " square meters");
                System.out.println("Bed: " + roomType.getBedType());
                System.out.println("Capacity: " + roomType.getCapacity());
                System.out.println("Amenities: " + roomType.getAmenities());
                BigDecimal cost = roomTypeEntitySessionBeanRemote.getPublishedRateForDates(roomType, startDate, endDate);
                System.out.print("Price: $" + cost);
                int quantity = roomTypeEntitySessionBeanRemote.getAvailableRoomQuantity(startDate, endDate, roomType);
                System.out.println(" Available Quantity: " + quantity);
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

        boolean hasAvailableRoomTypes = doWalkInSearchRoom(startDate, endDate);
        if (hasAvailableRoomTypes) {
            System.out.print("Input room type name to book> ");
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
                System.out.print("Input guest's passport number> ");
                String passportNo = sc.nextLine().trim();
                long guestId = -1;
                List<UnregisteredGuestEntity> guests = guestEntitySessionBeanRemote.retrieveGuestByPassportNo(passportNo);
                if (guests.size() == 0) {
                    guestId = unregisteredGuestEntitySessionBeanRemote.createNewUnregisteredGuest(new UnregisteredGuestEntity(passportNo));
                } else {
                    guestId = guests.get(0).getId();
                }
                ReservationEntity newReservation = new ReservationEntity(startDate, endDate, bookingQuantity);
                long newReservationId = 0l;
                try {
                    newReservationId = reservationEntitySessionBeanRemote.createNewWalkInReservation(newReservation, currentEmployeeEntity.getId(), guestId, roomType.getId());
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
            } catch (UnknownPersistenceException upe) {
                System.out.println(upe.getMessage());
            } catch (AlreadyExistsException aee) {
                System.out.println(aee.getMessage());
            }
        } else {
            return;
        }

    }
    
    private void doCheckInGuest() {
        UnregisteredGuestEntity guest = null;
        System.out.print("Input guest passport number> ");
        String passportNum = sc.nextLine().trim();
        try {
            guest = guestEntitySessionBeanRemote.retrieveGuestByPassportNo(passportNum).get(0);
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("No guests associated with inputted passport number.");
            return;
        }

        
        List<ReservationEntity> reservations = reservationEntitySessionBeanRemote.retrieveAllReservationsForGuest(guest.getId());
        
        Date currentDate = new Date();
        reservations = reservations.stream()
                .filter(reservation -> 
                        !currentDate.before(reservation.getStartDate()) &&
                        !currentDate.after(reservation.getEndDate()))
                .collect(Collectors.toList());
        if (reservations.size() == 0) {
            System.out.println("No reservations found");
            return;
        }
        for (ReservationEntity reservation : reservations) {
            List<ReservationRoomEntity> reservationRooms = reservation.getReservationRooms();
            for (ReservationRoomEntity reservationRoom : reservationRooms) {
                if (reservationRoom.getAllocatedRoom() == null) {
                    //
                } else {
                    try {
                        reservationRoom.getAllocatedRoom().setRoomStatus(RoomStatus.OCCUPIED);
                        roomEntitySessionBeanRemote.updateRoom(reservationRoom.getAllocatedRoom());
                        System.out.println("Room " + reservationRoom.getAllocatedRoom().getRoomNumber() + " checked in");
                    } catch (InputDataValidationException ex) {
                        System.out.println(ex.getMessage() + "\n");
                    }
                }
            }
        }

    }

    private void doCheckOutGuest() {
        Date currentDate = new Date();
        UnregisteredGuestEntity guest = null;
        System.out.print("Input guest passport number> ");
        String passportNum = sc.nextLine().trim();
        try {
            guest = guestEntitySessionBeanRemote.retrieveGuestByPassportNo(passportNum).get(0);
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("No guests associated with inputted passport number.");
            return;
        }

        
        List<ReservationEntity> reservations = reservationEntitySessionBeanRemote.retrieveAllReservationsForGuest(guest.getId());
        reservations = reservations.stream().filter(reservation -> !currentDate.after(reservation.getEndDate())).collect(Collectors.toList());
        for (ReservationEntity reservation : reservations) {
            List<ReservationRoomEntity> reservationRooms = reservation.getReservationRooms();
            for (ReservationRoomEntity reservationRoom : reservationRooms) {
                if (reservationRoom.getAllocatedRoom() != null && reservationRoom.getAllocatedRoom().getRoomStatus().equals(RoomStatus.OCCUPIED)) {
                    try {
                        reservationRoom.getAllocatedRoom().setRoomStatus(RoomStatus.AVAILABLE);
                        roomEntitySessionBeanRemote.updateRoom(reservationRoom.getAllocatedRoom());
                        System.out.println("Room " + reservationRoom.getAllocatedRoom().getRoomNumber() + " checked out");
                    } catch (InputDataValidationException ex) {
                        System.out.println(ex.getMessage() + "\n");
                    }
                }
            }
        }
    }
    
    
}
