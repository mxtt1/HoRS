/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeEntitySessionBeanRemote;
import ejb.session.stateless.RoomEntitySessionBeanRemote;
import ejb.session.stateless.RoomRateEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import entities.EmployeeEntity;
import entities.RoomEntity;
import entities.RoomRateEntity;
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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.enums.EmployeeRole;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;
import util.RankingComparator;
import util.enums.RateType;
import util.enums.RoomStatus;
import util.exception.EntityIsDisabledException;

/**
 *
 * @author mattl
 */
public class HotelOperationModule {

    private EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote;
    private RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;
    private RoomEntitySessionBeanRemote roomEntitySessionBeanRemote;
    private RoomRateEntitySessionBeanRemote roomRateEntitySessionBeanRemote;

    private EmployeeEntity currentEmployeeEntity;

    private final Scanner sc = new Scanner(System.in);
    
    private final RankingComparator rankingComparator = new RankingComparator();

    public HotelOperationModule() {

    }

    public HotelOperationModule(EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote, RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote, 
            RoomEntitySessionBeanRemote roomEntitySessionBeanRemote, EmployeeEntity currEmployeeEntity, RoomRateEntitySessionBeanRemote roomRateEntitySessionBeanRemote) {
        this();
        this.employeeEntitySessionBeanRemote = employeeEntitySessionBeanRemote;
        this.roomTypeEntitySessionBeanRemote = roomTypeEntitySessionBeanRemote;
        this.roomEntitySessionBeanRemote = roomEntitySessionBeanRemote;
        this.roomRateEntitySessionBeanRemote = roomRateEntitySessionBeanRemote;
        this.currentEmployeeEntity = currEmployeeEntity;
    }

    public void menuHotelOperation() throws InvalidAccessRightException {
        if (currentEmployeeEntity.getEmployeeRole() == EmployeeRole.GRO) {
            throw new InvalidAccessRightException("You don't have rights to access the hotel operation module.");
        }

        Integer response = 0;
        while (true) {
            System.out.println("\nHoRS System :: Hotel Operation");
            System.out.println("1. Create New Room Type");
            System.out.println("2. View Room Type Details");
            System.out.println("3. Update Room Type");
            System.out.println("4. Delete Room Type");
            System.out.println("5. View All Room Types");
            System.out.println("6. Create New Room");
            System.out.println("7. Update Room");
            System.out.println("8. Delete Room");
            System.out.println("9. View All Rooms");
            System.out.println("10. Create New Room Rate");
            System.out.println("11. View Room Rate Details");
            System.out.println("13. Delete Room Rate");
            System.out.println("14. View All Room Rates");
            System.out.println("99. Exit");
            response = 0;

            while (response < 1 || response > 99) {
                System.out.print("> ");
                response = sc.nextInt();
                if (response == 1) {
                    doCreateNewRoomType();
                } else if (response == 2) {
                    doViewRoomTypeDetails();
                } else if (response == 3) {
                    doUpdateRoomTypeRecord();
                } else if (response == 4) {
                    doDeleteRoomTypeRecord();
                } else if (response == 5) {
                    doViewAllRoomTypeRecords();
                } else if (response == 6) {
                    doCreateNewRoom();
                } else if (response == 7) {
                    doUpdateRoom();
                } else if (response == 8) {
                    doDeleteRoom();
                } else if (response == 9) {
                    doViewAllRooms();
                } else if (response == 10) {
                    doCreateNewRoomRate();
                } else if (response == 11) {
                    doViewRoomRateDetails();
                } else if (response == 13) {
                    doDeleteRoomRateRecord();
                } else if (response == 14) {
                    doViewAllRoomRateRecords();
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

    private void doCreateNewRoomType() {
        sc.nextLine();
        System.out.println("\nCreate New Room Type: ");
        System.out.print("Enter name> ");
        String name = sc.nextLine().trim();
        System.out.print("Enter description> ");
        String description = sc.nextLine().trim();
        System.out.print("Enter bed type> ");
        String bedType = sc.nextLine().trim();
        System.out.print("Enter amenities> ");
        String amenities = sc.nextLine().trim();
        System.out.print("Enter capacity> ");
        int capacity = sc.nextInt();
        System.out.print("Enter room size in square metres> ");
        int roomSize = sc.nextInt();

        long newRoomTypeId = roomTypeEntitySessionBeanRemote.createNewRoomType(new RoomTypeEntity(name, description, bedType, amenities, capacity, roomSize));

        List<RoomTypeEntity> allRoomTypes = roomTypeEntitySessionBeanRemote.retrieveAllRoomTypes();
        allRoomTypes.sort(rankingComparator);

        System.out.println("\nRoom Hierarchy (1 is highest): ");
        for (RoomTypeEntity roomType : allRoomTypes) {
            if (roomType.getRanking() != 0 && !roomType.isDisabled()) System.out.println(roomType.getRanking() + ". " + roomType.getName());
            else if (roomType.isDisabled()) allRoomTypes.remove(roomType);
        }
        while (true) {
            System.out.print("\nEnter ranking of new room (rooms currently at and below the ranking will be shifted downwards)> ");
            int newRoomTypeRanking = sc.nextInt();
            if (newRoomTypeRanking <= allRoomTypes.size() && newRoomTypeRanking > 0) {
                roomTypeEntitySessionBeanRemote.setRoomTypeRanking(newRoomTypeRanking, newRoomTypeId);
                System.out.println("New room type created succesfully!");
                break;
            } else {
                System.out.println("Invalid input! Please try again.");
            }
        }
    }

    private RoomTypeEntity doViewRoomTypeDetails() {
        sc.nextLine();
        System.out.println("View Room Type Details: ");
        System.out.print("Enter Name Of Room Type> ");
        String roomTypeName = sc.nextLine().trim();

        RoomTypeEntity roomType = roomTypeEntitySessionBeanRemote.retrieveRoomTypeByName(roomTypeName);
        System.out.println("Room Name: " + roomType.getName());
        System.out.println("Room Description: " + roomType.getDescription());
        System.out.println("Room Size: " + roomType.getRoomSize() + " square meters");
        System.out.println("Bed: " + roomType.getBedType());
        System.out.println("Capacity: " + roomType.getCapacity());
        System.out.println("Amenities: " + roomType.getAmenities());
        System.out.println("Press any key to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(HotelOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        return roomType;

    }

    private void doUpdateRoomTypeRecord() {
        sc.nextLine();
        System.out.println("\nUpdate Room Type Details: ");
        System.out.print("Enter Name Of Room Type to Update> ");
        String roomTypeName = sc.nextLine().trim();

        RoomTypeEntity roomType = roomTypeEntitySessionBeanRemote.retrieveRoomTypeByName(roomTypeName);
        System.out.println("1. Room Type Name: " + roomType.getName());
        System.out.println("2. Room Description: " + roomType.getDescription());
        System.out.println("3. Room Size: " + roomType.getRoomSize() + " square meters");
        System.out.println("4. Bed: " + roomType.getBedType());
        System.out.println("5. Capacity: " + roomType.getCapacity());
        System.out.print("Select choice of update> ");
        int command = sc.nextInt();
        if (command == 5) {
            System.out.print("Input New Capacity> ");
            int newCapacity = sc.nextInt();
            roomType.setCapacity(newCapacity);
        } else if (command == 4) {
            sc.nextLine();
            System.out.print("Input New Bed Type> ");;
            String newBedType = sc.nextLine().trim();
            roomType.setBedType(newBedType);
        } else if (command == 3) {
            System.out.print("Input New Room Size> ");
            int newRoomSize = sc.nextInt();
            roomType.setRoomSize(newRoomSize);
        } else if (command == 2) {
            sc.nextLine();
            System.out.print("Input new Room Description> ");
            String newRoomDescription = sc.nextLine().trim();
            roomType.setDescription(newRoomDescription);
        } else if (command == 1) {
            sc.nextLine();
            System.out.print("Input new Room Type Name>: ");
            String newRoomName = sc.nextLine().trim();
            roomType.setName(newRoomName);
        }
        RoomTypeEntity newRoomType = roomTypeEntitySessionBeanRemote.updateRoomType(roomType);
        System.out.println("\nRoom Type Updated With Details: ");
        System.out.println("Room Name: " + newRoomType.getName());
        System.out.println("Room Description: " + newRoomType.getDescription());
        System.out.println("Room Size: " + newRoomType.getRoomSize() + " square meters");
        System.out.println("Bed: " + newRoomType.getBedType());
        System.out.println("Capacity: " + newRoomType.getCapacity());
        System.out.println("Amenities: " + newRoomType.getAmenities());

        System.out.println("Press any key to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(HotelOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void doDeleteRoomTypeRecord() {
        RoomTypeEntity roomTypeToBeDeleted = this.doViewRoomTypeDetails();
        sc.nextLine();
        System.out.print("Enter 'y' to confirm deletion> ");
        String response = sc.nextLine().trim().toLowerCase();
        if (response.equals("y")) {
            roomTypeEntitySessionBeanRemote.deleteRoomType(roomTypeToBeDeleted.getId());
            System.out.println("Room Type " + roomTypeToBeDeleted.getName() + " deleted!");
        } else {
            System.out.println("Deletion cancelled!");
        }
    }

    private void doViewAllRoomTypeRecords() {
        System.out.println("\nViewing All Room Type Records:\n");
        List<RoomTypeEntity> roomTypes = roomTypeEntitySessionBeanRemote.retrieveAllRoomTypes();
        roomTypes.sort(rankingComparator);
        
        for (RoomTypeEntity rt : roomTypes) {
            if (rt.isDisabled()) System.out.print("(DISABLED) ");
            System.out.println("ID: " + rt.getId() + " | Name: " + rt.getName() + " | Ranking: " + rt.getRanking());
        }
        
        System.out.println("\nPress any key to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(HotelOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doCreateNewRoom() {
        sc.nextLine();
        System.out.println("\nCreate New Room: ");
        System.out.print("Enter room number> ");
        String roomNumber = sc.nextLine().trim();
        System.out.print("Enter room type name> ");
        String roomTypeName = sc.nextLine().trim();

        try {
            long newRoomId = roomEntitySessionBeanRemote.createNewRoom(new RoomEntity(roomNumber), roomTypeName);
            System.out.println("New room created successfully!");
        } catch (EntityIsDisabledException ex) {
            System.out.println(ex.getMessage());
        } catch (NoResultException ex) {
            System.out.println("Error: No such room type!");
        }
    }

    private void doUpdateRoom() {
        sc.nextLine();
        System.out.println("\nUpdate Room: ");
        System.out.print("Enter Number Of Room to Update> ");
        String roomName = sc.nextLine().trim();

        RoomEntity roomEntity = roomEntitySessionBeanRemote.retrieveRoomByNumber(roomName);
        System.out.println("1. Room Number: " + roomEntity.getRoomNumber());
        System.out.println("2. Room Type: " + roomEntity.getRoomType().getName());
        System.out.println("3. Room Status: " + roomEntity.getRoomStatus().name());
        System.out.print("Select choice of update> ");
        int command = sc.nextInt();
        RoomEntity newRoom = null;
        if (command == 3) {
            System.out.println("Input New Room Status: ");
            System.out.println("1. AVAILABLE");
            System.out.println("2. OCCUPIED");
            System.out.print("> ");
            int input = sc.nextInt();
            if (input == 1) {
                roomEntity.setRoomStatus(RoomStatus.AVAILABLE);
            } else if (input == 2) {
                roomEntity.setRoomStatus(RoomStatus.OCCUPIED);
            }
            newRoom = roomEntitySessionBeanRemote.updateRoom(roomEntity);
        } else if (command == 2) {
            sc.nextLine();
            System.out.print("Input new Room Type> ");
            String newRoomType = sc.nextLine().trim();
            newRoom = roomEntitySessionBeanRemote.changeRoomType(roomEntity.getId(), newRoomType);
        } else if (command == 1) {
            sc.nextLine();
            System.out.print("Input new Room Number> ");
            String newRoomName = sc.nextLine().trim();
            roomEntity.setRoomNumber(newRoomName);
            newRoom = roomEntitySessionBeanRemote.updateRoom(roomEntity);
        }
        
        System.out.println("\nRoom Updated With Details: ");
        System.out.println("Room Number: " + newRoom.getRoomNumber());
        System.out.println("Room Type: " + newRoom.getRoomType().getName());
        System.out.println("Room Status: " + newRoom.getRoomStatus().name());
        
        System.out.println("Press any key to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(HotelOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doDeleteRoom() {
        sc.nextLine();
        System.out.println("Delete Room: ");
        System.out.print("Enter Number Of Room to Delete (eg. 1234) > ");
        String roomNum = sc.nextLine().trim();
        RoomEntity roomToBeDeleted = roomEntitySessionBeanRemote.retrieveRoomByNumber(roomNum); 
        long roomToBeDeletedId = roomToBeDeleted.getId();
        System.out.print("Enter 'y' to confirm deletion> ");
        String response = sc.next().toLowerCase();
        if (response.equals("y")) {
            roomEntitySessionBeanRemote.deleteRoom(roomToBeDeletedId);
            System.out.println("Room " + roomToBeDeleted.getRoomNumber()+ " deleted!");
        } else {
            System.out.println("Deletion cancelled!");
        }
    }

    private void doViewAllRooms() {
        System.out.println("\nViewing All Rooms:\n");
        List<RoomEntity> rooms = roomEntitySessionBeanRemote.retrieveAllRooms();
        
        for (RoomEntity room : rooms) {
            if (room.isDisabled()) {
                System.out.print("(DISABLED)");
            }
            System.out.println("ID: " + room.getId() + " | Number: " + room.getRoomNumber() + " | Status: " + room.getRoomStatus().name() + " | Type: " + room.getRoomType().getName());
        }
        
        System.out.println("Press any key to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(HotelOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doCreateNewRoomRate() {
        sc.nextLine();
        System.out.println("\nCreate New Room Rate: ");
        System.out.print("Enter name of room type to create new rate for> ");
        String roomType = sc.nextLine().trim();
        
        System.out.print("Enter name of room rate> ");
        String roomRateName = sc.nextLine().trim();
        System.out.print("Enter amount per night> ");
        BigDecimal ratePerNight = sc.nextBigDecimal();
        System.out.println();
                
        System.out.println("1. Published Rate");
        System.out.println("2. Normal Rate");
        System.out.println("3. Peak Rate");
        System.out.println("4. Promotion Rate");
        int rateType = 0;
        while (true) {
            System.out.print("Select type of rate to create> ");
            rateType = sc.nextInt();
            
            if (rateType == 1 || rateType == 2) {
                if (rateType == 1) {
                    RoomRateEntity newRoomRate = new RoomRateEntity(roomRateName, RateType.PUBLISHED, ratePerNight);
                    roomRateEntitySessionBeanRemote.createNewPublishedNormalRate(newRoomRate, roomType);
                } else {
                    RoomRateEntity newRoomRate = new RoomRateEntity(roomRateName, RateType.NORMAL, ratePerNight);
                    roomRateEntitySessionBeanRemote.createNewPublishedNormalRate(newRoomRate, roomType);
                }
                break;
                
            } else if (rateType == 3 || rateType == 4) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                dateFormat.setLenient(false);
                sc.nextLine();
                System.out.print("Enter the starting date (dd-MM-yyyy)> ");
                String inputDate = sc.nextLine().trim();
                Date startDate = null;
                try {
                    startDate = dateFormat.parse(inputDate);
                } catch (ParseException ex) {
                    Logger.getLogger(HotelOperationModule.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.print("Enter the ending date (dd-MM-yyyy)> ");
                String secondInputDate = sc.nextLine().trim();
                Date endDate = null;
                try {
                    endDate = dateFormat.parse(secondInputDate);
                } catch (ParseException ex) {
                    Logger.getLogger(HotelOperationModule.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (rateType == 3) {
                    RoomRateEntity newRoomRate = new RoomRateEntity(roomRateName, RateType.PEAK, ratePerNight);
                    roomRateEntitySessionBeanRemote.createNewPeakPromotionRate(newRoomRate, startDate, endDate, roomType);
                } else {
                    RoomRateEntity newRoomRate = new RoomRateEntity(roomRateName, RateType.PROMOTION, ratePerNight);
                    roomRateEntitySessionBeanRemote.createNewPeakPromotionRate(newRoomRate, startDate, endDate, roomType);
                }
                break;
            } else {
                System.out.println("Invalid input, try again!");
            } 
        }
        System.out.println("New room rate created successfully!");
    }

    private RoomRateEntity doViewRoomRateDetails() {
        sc.nextLine();
        System.out.println("\nView Room Rate Details: ");
        System.out.print("Enter Name Of Room Rate> ");
        String roomRateName = sc.nextLine().trim();

        RoomRateEntity roomRate = roomRateEntitySessionBeanRemote.retrieveRoomRateByName(roomRateName);
        System.out.println("Room Rate Name: " + roomRate.getName());
        System.out.println("Room Rate Type: " + roomRate.getRateType());
        System.out.println("Room Type: " + roomRate.getRoomType().getName());
        System.out.println("Rate Per Night: $" + roomRate.getRatePerNight());
        if (roomRate.getRateType() == RateType.PROMOTION || roomRate.getRateType() == RateType.PEAK) {
            System.out.println("Start Date: " + roomRate.getStartDate());
            System.out.println("End Date: " + roomRate.getEndDate());
        }
        System.out.println("Press any key to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(HotelOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return roomRate;
    }

    private void doDeleteRoomRateRecord() {
        RoomRateEntity roomRateToBeDeleted = this.doViewRoomRateDetails();
        sc.nextLine();
        System.out.print("Enter 'y' to confirm deletion> ");
        String response = sc.nextLine().trim().toLowerCase();
        if (response.equals("y")) {
            roomRateEntitySessionBeanRemote.deleteRoomRate(roomRateToBeDeleted.getId());
            System.out.println("Room Rate " + roomRateToBeDeleted.getName() + " deleted!");
        } else {
            System.out.println("Deletion cancelled!");
        }
    }

    private void doViewAllRoomRateRecords() {
        System.out.println("\nViewing All Room Rate Records:\n");
        List<RoomRateEntity> roomRates = roomRateEntitySessionBeanRemote.retrieveAllRoomRates();
        
        for (RoomRateEntity rr : roomRates) {
            if (rr.isDisabled()) System.out.print("(DISABLED) ");
            String output = "ID: " + rr.getId() + " | Name: " + rr.getName() + " | Room Type: " + rr.getRoomType().getName()
                    + " | Rate Type: " + rr.getRateType();
            if (rr.getRateType().toString().equals("PEAK") || rr.getRateType().toString().equals("PROMOTION")) {
                output += (" | Start Date: " + rr.getStartDate().toString() + " | End Date: " + rr.getEndDate().toString());
            }
            System.out.println(output);
            
        }
        
        System.out.println("\nPress any key to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(HotelOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
