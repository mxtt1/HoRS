/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeEntitySessionBeanRemote;
import ejb.session.stateless.RoomEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import entities.EmployeeEntity;
import entities.RoomEntity;
import entities.RoomTypeEntity;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.enums.EmployeeRole;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;
import util.RankingComparator;
import util.enums.RoomStatus;

/**
 *
 * @author mattl
 */
public class HotelOperationModule {

    private EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote;
    private RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;
    private RoomEntitySessionBeanRemote roomEntitySessionBeanRemote;

    private EmployeeEntity currentEmployeeEntity;

    private final Scanner sc = new Scanner(System.in);
    
    private final RankingComparator rankingComparator = new RankingComparator();

    public HotelOperationModule() {

    }

    public HotelOperationModule(EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote, RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote, 
            RoomEntitySessionBeanRemote roomEntitySessionBeanRemote, EmployeeEntity currEmployeeEntity) {
        this();
        this.employeeEntitySessionBeanRemote = employeeEntitySessionBeanRemote;
        this.roomTypeEntitySessionBeanRemote = roomTypeEntitySessionBeanRemote;
        this.roomEntitySessionBeanRemote = roomEntitySessionBeanRemote;
        this.currentEmployeeEntity = currEmployeeEntity;
    }

    public void menuHotelOperation() throws InvalidAccessRightException {
        if (currentEmployeeEntity.getEmployeeRole() == EmployeeRole.GRO) {
            throw new InvalidAccessRightException("You don't have rights to access the hotel operation module.");
        }

        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        while (true) {
            System.out.println("\nHoRS System :: Hotel Operation");
            System.out.println("1. Create New Room Type");
            System.out.println("2. View Room Type Details");
            System.out.println("3. Update Room Type Details");
            System.out.println("4. Delete Room Type");
            System.out.println("5. View All Room Types");
            System.out.println("6. Create New Room");
            System.out.println("7. Update Room");
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
        
        System.out.print("Enter 'y' to confirm deletion> ");
        String response = sc.nextLine().trim();
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
            System.out.println("ID: " + rt.getId() + " Name: " + rt.getName() + " Ranking: " + rt.getRanking());
        }
    }

    private void doCreateNewRoom() {
        sc.nextLine();
        System.out.println("\nCreate New Room: ");
        System.out.print("Enter room number> ");
        String roomNumber = sc.nextLine().trim();
        System.out.print("Enter room type name> ");
        String roomTypeName = sc.nextLine().trim();
        
        long newRoomId = roomEntitySessionBeanRemote.createNewRoom(new RoomEntity(roomNumber), roomTypeName);
        System.out.println("New room created successfully!");
    }

    private void doUpdateRoom() {
        System.out.println("Update Room: ");
        System.out.println("Enter Name Of Room to Update> ");
        String roomName = sc.nextLine().trim();

        RoomEntity roomEntity = roomEntitySessionBeanRemote.retrieveRoomByName(roomName);
        System.out.println("1. Room Name: " + roomEntity.getRoomNumber());
        System.out.println("2. Room Type: " + roomEntity.getRoomType().getName());
        System.out.println("3. Room Status: " + roomEntity.getRoomStatus().name());
        System.out.print("Select choice of update> ");
        int command = sc.nextInt();
        if (command == 3) {
            System.out.print("Input New Room Status> ");
            System.out.println("1. AVAILABLE");
            System.out.println("2. OCCUPIED");
            int input = sc.nextInt();
            if (input == 1) {
                roomEntity.setRoomStatus(RoomStatus.AVAILABLE);
            } else if (input == 2) {
                roomEntity.setRoomStatus(RoomStatus.OCCUPIED);
            }
        } else if (command == 2) {
            sc.nextLine();
            System.out.print("Input new Room Type> ");
            String newRoomType = sc.nextLine().trim();
            RoomTypeEntity newRoomTypeEntity = roomTypeEntitySessionBeanRemote.retrieveRoomTypeByName(newRoomType);
            roomEntity.setRoomType(newRoomTypeEntity);
        } else if (command == 1) {
            sc.nextLine();
            System.out.print("Input new Room Name> ");
            String newRoomName = sc.nextLine().trim();
            roomEntity.setRoomNumber(newRoomName);
        }
        RoomEntity newRoom = roomEntitySessionBeanRemote.updateRoom(roomEntity);
        System.out.println("\nRoom Updated With Details: ");
        System.out.println("Room Name: " + newRoom.getRoomNumber());
        System.out.println("Room Type: " + newRoom.getRoomType().getName());
        System.out.println("Room Status: " + newRoom.getRoomStatus().name());
        System.out.println("Press any key to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(HotelOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
