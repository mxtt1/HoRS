/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import entities.EmployeeEntity;
import entities.RoomTypeEntity;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.enums.EmployeeRole;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;
import util.RankingComparator;

/**
 *
 * @author mattl
 */
public class HotelOperationModule {

    private EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote;
    private RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;

    private EmployeeEntity currentEmployeeEntity;

    private final Scanner sc = new Scanner(System.in);

    public HotelOperationModule() {

    }

    public HotelOperationModule(EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote,
            RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote, EmployeeEntity currEmployeeEntity) {
        this();
        this.employeeEntitySessionBeanRemote = employeeEntitySessionBeanRemote;
        this.roomTypeEntitySessionBeanRemote = roomTypeEntitySessionBeanRemote;
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
            System.out.println("99. Exit");
            response = 0;

            while (response < 1 || response > 99) {
                System.out.print("> ");
                response = sc.nextInt();
                if (response == 1) {
                    doCreateNewRoomType();
                } else if (response == 2) {
                    doViewRoomTypeDetails();
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
        String name;
        String description;
        String bedType;
        String amenities;
        int capacity;
        int roomSize;
                
        System.out.println("\nCreate New Room Type: ");
        System.out.print("Enter name> ");
        name = sc.nextLine().trim();
        System.out.print("Enter description> ");
        description = sc.nextLine().trim();
        System.out.print("Enter bed type> ");
        bedType = sc.nextLine().trim();
        System.out.print("Enter amenities> ");
        amenities = sc.nextLine().trim();
        System.out.print("Enter capacity> ");
        capacity = sc.nextInt();
        System.out.print("Enter room size in square metres> ");
        roomSize = sc.nextInt();
        
        long newRoomTypeId = roomTypeEntitySessionBeanRemote.createNewRoomType(new RoomTypeEntity(name, description, bedType, amenities, capacity, roomSize));
        
        List<RoomTypeEntity> allRoomTypes = roomTypeEntitySessionBeanRemote.retrieveAllRoomTypes();
        allRoomTypes.sort(new RankingComparator());
        
        System.out.println("\nRoom Hierarchy (1 is highest): ");
        for (RoomTypeEntity roomType : allRoomTypes) {
            System.out.println(roomType.getRanking() + ". "  + roomType.getName());
        }
        System.out.print("Enter ranking of new room (rooms currently at and below the ranking will be shifted downwards)> ");
        int newRoomTypeRanking = sc.nextInt();
        roomTypeEntitySessionBeanRemote.setRoomTypeRanking(newRoomTypeRanking, newRoomTypeId);
        System.out.println("New room type created succesfully!");
    }
    
    private void doViewRoomTypeDetails() throws InvalidAccessRightException {
        sc.nextLine();
        System.out.println("Enter Name Of Room Type: ");
        String roomTypeName = sc.nextLine();
        System.out.println("DEBUG: User input received: " + roomTypeName); // Debugging line
        RoomTypeEntity roomType = roomTypeEntitySessionBeanRemote.retrieveRoomTypeByName(roomTypeName);
        System.out.println("Room Name: " + roomType.getName());
        System.out.println("Room Description: " + roomType.getDescription());
        System.out.println("Room Size: " + roomType.getRoomSize() + " square meters");
        System.out.println("Bed: " + roomType.getBedType());
        System.out.println("Capacity: " + roomType.getCapacity());
        System.out.println("Amenities: " + roomType.getAmenities());
        
        System.out.println("Press any key to continue.");
        sc.next();
    }
}
