/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import entities.EmployeeEntity;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enums.EmployeeRole;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author Mark
 */
public class SystemAdminModule {
    private EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote;
    private RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;
    
    private EmployeeEntity currentEmployeeEntity;
    
    private final Scanner sc = new Scanner(System.in);
    
    public SystemAdminModule() {
        
    }
    
    public SystemAdminModule(EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote, 
            RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote, EmployeeEntity currEmployeeEntity) 
    {
        this();
        this.employeeEntitySessionBeanRemote = employeeEntitySessionBeanRemote;
        this.roomTypeEntitySessionBeanRemote = roomTypeEntitySessionBeanRemote;
        this.currentEmployeeEntity = currEmployeeEntity;
    }
    
    public void menuSystemAdmin() throws InvalidAccessRightException {
        if (currentEmployeeEntity.getEmployeeRole() != EmployeeRole.SYSTEM_ADMIN) {
            throw new InvalidAccessRightException("You don't have SYSTEM_ADMIN rights to access the system administration module.");
        }

        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        while (true) {
            System.out.println("\nHoRS System :: System Administration");
            System.out.println("1. Create New Employee");
            System.out.println("2. View All Employees");
            System.out.println("99. Exit");
            response = 0;

            while (response < 1 || response > 99) {
                System.out.print("> ");
                response = sc.nextInt();
                if (response == 1) {
                    try {
                        doCreateNewEmployee();
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println(ex.getMessage());
                    }
                } else if (response == 2) {
                    doViewAllEmployees();
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
        if(username.length() > 0 && password.length() > 0 && fullname.length() > 0) {
            EmployeeEntity newEmployee = new EmployeeEntity(employeeRole, username, password, fullname);
            employeeEntitySessionBeanRemote.createNewEmployee(newEmployee);
        } else {
             throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    private void doViewAllEmployees() {
        System.out.println("\nViewing All Employee Records:\n");
        List<EmployeeEntity> employees = employeeEntitySessionBeanRemote.retrieveAllEmployees();
        for (EmployeeEntity e : employees) {
            System.out.println("ID: " + e.getId() + " Full Name: " + e.getFullName() + 
                    " Username: " + e.getUsername() + " Password: " + e.getPassword());
        }
        System.out.print("\nPress any key to continue.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(SystemAdminModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
