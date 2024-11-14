/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.singleton.RoomAllocationSessionBeanRemote;
import ejb.session.stateless.EmployeeEntitySessionBeanRemote;
import ejb.session.stateless.PartnerEntitySessionBeanRemote;
import ejb.session.stateless.ReservationEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import entities.EmployeeEntity;
import entities.PartnerEntity;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import util.enums.EmployeeRole;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author Mark
 */
public class SystemAdminModule {

    private EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote;
    private RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;
    private PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote;
    private RoomAllocationSessionBeanRemote allocationSessionBeanRemote;

    private EmployeeEntity currentEmployeeEntity;
    
    private static Scanner sc = new Scanner(System.in);

    public SystemAdminModule() {

    }

    public SystemAdminModule(EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote,
            RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote,
            PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote, EmployeeEntity currEmployeeEntity, RoomAllocationSessionBeanRemote allocationSessionBeanRemote) {
        this();
        this.employeeEntitySessionBeanRemote = employeeEntitySessionBeanRemote;
        this.roomTypeEntitySessionBeanRemote = roomTypeEntitySessionBeanRemote;
        this.partnerEntitySessionBeanRemote = partnerEntitySessionBeanRemote;
        this.currentEmployeeEntity = currEmployeeEntity;
        this.allocationSessionBeanRemote = allocationSessionBeanRemote;
    }

    public void menuSystemAdmin() throws InvalidAccessRightException {

        if (currentEmployeeEntity.getEmployeeRole() != EmployeeRole.SYSTEM_ADMIN) {
            throw new InvalidAccessRightException("You don't have SYSTEM_ADMIN rights to access the system administration module.");
        }
        Integer response = 0;
        while (true) {
            System.out.println("\nHoRS System :: System Administration");
            System.out.println("1. Create New Employee");
            System.out.println("2. View All Employees");
            System.out.println("3. Create New Partner");
            System.out.println("4. View All Partners");
            System.out.println("99. Exit");
            response = 0;

            while (response < 1 || response > 99) {
                System.out.print("> ");
                response = sc.nextInt();
                sc.nextLine();
                if (response == 1) {
                    try {
                        doCreateNewEmployee();
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println(ex.getMessage());
                    }
                } else if (response == 2) {
                    doViewAllEmployees();
                } else if (response == 3) {
                    try {
                        doCreateNewPartner();
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println(ex.getMessage());
                    }
                } else if (response == 4) {
                    doViewAllPartners();
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

    private void doCreateNewEmployee() throws InvalidLoginCredentialException {
        String username;
        String password;
        String fullname;
        EmployeeRole employeeRole = EmployeeRole.SYSTEM_ADMIN;

        System.out.println("\nCreate New Employee Account: ");
        System.out.print("Enter username> ");
        username = sc.nextLine().trim();
        System.out.print("Enter password> ");
        password = sc.nextLine().trim();
        System.out.print("Enter full name> ");
        fullname = sc.nextLine().trim();

        System.out.println("Choose access right");
        System.out.println("1: System Administrator");
        System.out.println("2: Operation Manager");
        System.out.println("3: Sales Manager");
        System.out.println("4: Guest Relation Officer");
        int response = 0;

        while (response < 1 || response > 5) {
            System.out.print("> ");
            response = sc.nextInt();
            sc.nextLine();
            if (response == 1) {
                employeeRole = EmployeeRole.SYSTEM_ADMIN;
            } else if (response == 2) {
                employeeRole = EmployeeRole.OPS_MANAGER;
            } else if (response == 3) {
                employeeRole = EmployeeRole.SALES_MANAGER;
            } else if (response == 4) {
                employeeRole = EmployeeRole.GRO;
            } else {
                System.out.println("Invalid input, try again!");
            }
        }
        if (username.length() > 0 && password.length() > 0 && fullname.length() > 0) {
            try {
                EmployeeEntity newEmployee = new EmployeeEntity(employeeRole, username, password, fullname);
                long id = employeeEntitySessionBeanRemote.createNewEmployee(newEmployee);
                System.out.println("New employee with id " + id + " and role " + employeeRole.toString() + " created successfully");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    private void doViewAllEmployees() {

        System.out.println("\nViewing All Employee Records:\n");
        List<EmployeeEntity> employees = employeeEntitySessionBeanRemote.retrieveAllEmployees();
        for (EmployeeEntity e : employees) {
            System.out.println("ID: " + e.getId() + "| Full Name: " + e.getFullName()
                    + "| Username: " + e.getUsername() + "| Password: " + e.getPassword());
        }
        System.out.print("\nPress enter to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(SystemAdminModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doCreateNewPartner() throws InvalidLoginCredentialException {
        String username;
        String password;
        System.out.println("\nCreate New Partner Account: ");
        System.out.print("Enter username> ");
        username = sc.nextLine().trim();
        System.out.print("Enter password> ");
        password = sc.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            PartnerEntity newPartner = new PartnerEntity(username, password);
            long id = partnerEntitySessionBeanRemote.createNewPartner(newPartner);
            System.out.println("New partner with id " + id + " created successfully");
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    private void doViewAllPartners() {

        System.out.println("\nViewing All Partner Records:\n");
        List<PartnerEntity> partners = partnerEntitySessionBeanRemote.retrieveAllPartners();
        for (PartnerEntity p : partners) {
            System.out.println("ID: " + p.getPartnerEntityId() + "| Username: " + p.getUsername() + "| Password: " + p.getPassword());
        }
        System.out.print("\nPress enter to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(SystemAdminModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
    private void doManuallyAllocateRooms() {
        System.out.println("\nManually allocating rooms:\n");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        
        Date givenDate = null;
        while (givenDate == null) {
            System.out.print("Enter date for allocation (format: dd-MM-yyyy, e.g., 12-10-2002): ");
            String startInput = sc.nextLine();
            try {
                givenDate = dateFormat.parse(startInput);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use dd-MM-yyyy.");
            }
        }
        //try {
            allocationSessionBeanRemote.allocateRoomsForDate(givenDate);
            System.out.println("Rooms allocated successfully!");
       // } catch (){

       // }
    }
*/

}
