/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import entities.EmployeeEntity;
import java.util.Scanner;
import javax.ejb.EJB;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author Mark
 */
public class MainApp {
    private EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote;
    private RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;
    
    private SystemAdminModule systemAdminModule;
    
    private EmployeeEntity currentEmployeeEntity;
    
    Scanner sc = new Scanner(System.in);
    
    
    public MainApp() {
        
    }
    
    public MainApp(EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote, RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote) {
        this.employeeEntitySessionBeanRemote = employeeEntitySessionBeanRemote;
        this.roomTypeEntitySessionBeanRemote = roomTypeEntitySessionBeanRemote;
    }
    
    public void runApp() throws InvalidLoginCredentialException, InvalidAccessRightException {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            System.out.println("Welcome to HoRS Management Client");
            System.out.println("1: Login");
            System.out.println("2: Exit");
            response = 0;
            System.out.print("> ");
            response = sc.nextInt();
            if (response == 1) {
                try {
                    doLogin();
                    System.out.println("Login Successful");
                    systemAdminModule = new SystemAdminModule(employeeEntitySessionBeanRemote, roomTypeEntitySessionBeanRemote, currentEmployeeEntity);
                    menuMain();
                } catch (InvalidLoginCredentialException ex) {
                    System.out.println(ex.getMessage() + "\n");
                }
            } else if (response == 2) {
                break;
            } else {
                System.out.println("Invalid input, try again!");
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
            currentEmployeeEntity = employeeEntitySessionBeanRemote.employeeLogin(username, password);      
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    private void menuMain() throws InvalidAccessRightException {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while (true) {
            System.out.println("HoRS Management Client");
            System.out.println("You are logged in as " + currentEmployeeEntity.getFullName());
            System.out.println("1: System Administration");
            System.out.println("99: Logout"); // high so we can add more stuff
            response = 0;
            System.out.print("> ");
            response = sc.nextInt();
            
                if (response == 1) {
                    systemAdminModule.menuSystemAdmin();
                } else if (response == 99) {
                    break;
                } else {
                    System.out.println("Invalid input, try again!");
            }
        }
    }
}
