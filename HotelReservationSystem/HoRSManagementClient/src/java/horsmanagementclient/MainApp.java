/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.singleton.RoomAllocationSessionBeanRemote;
import ejb.session.stateless.EmployeeEntitySessionBeanRemote;
import ejb.session.stateless.GuestEntitySessionBeanRemote;
import ejb.session.stateless.PartnerEntitySessionBeanRemote;
import ejb.session.stateless.ReservationEntitySessionBeanRemote;
import ejb.session.stateless.RoomEntitySessionBeanRemote;
import ejb.session.stateless.RoomRateEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import ejb.session.stateless.UnregisteredGuestEntitySessionBeanRemote;
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
    private PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote;
    private RoomEntitySessionBeanRemote roomEntitySessionBeanRemote;
    private RoomRateEntitySessionBeanRemote roomRateEntitySessionBeanRemote;
    private GuestEntitySessionBeanRemote guestEntitySessionBeanRemote;
    private UnregisteredGuestEntitySessionBeanRemote unregisteredGuestEntitySessionBeanRemote;
    private ReservationEntitySessionBeanRemote reservationEntitySessionBeanRemote;
    private RoomAllocationSessionBeanRemote allocationSessionBeanRemote;
    

    private SystemAdminModule systemAdminModule;
    private HotelOperationModule hotelOperationModule;
    private FrontOfficeModule frontOfficeModule;

    private EmployeeEntity currentEmployeeEntity;

    private final Scanner sc = new Scanner(System.in);

    public MainApp() {

    }

    public MainApp(EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote, RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote,
            PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote, RoomEntitySessionBeanRemote roomEntitySessionBeanRemote,
            RoomRateEntitySessionBeanRemote roomRateEntitySessionBeanRemote, GuestEntitySessionBeanRemote guestEntitySessionBeanRemote,
            UnregisteredGuestEntitySessionBeanRemote unregisteredGuestEntitySessionBeanRemote, ReservationEntitySessionBeanRemote reservationEntitySessionBeanRemote,
            RoomAllocationSessionBeanRemote allocationSessionBeanRemote) {
        this.employeeEntitySessionBeanRemote = employeeEntitySessionBeanRemote;
        this.roomTypeEntitySessionBeanRemote = roomTypeEntitySessionBeanRemote;
        this.partnerEntitySessionBeanRemote = partnerEntitySessionBeanRemote;
        this.roomEntitySessionBeanRemote = roomEntitySessionBeanRemote;
        this.roomRateEntitySessionBeanRemote = roomRateEntitySessionBeanRemote;
        this.guestEntitySessionBeanRemote = guestEntitySessionBeanRemote;
        this.unregisteredGuestEntitySessionBeanRemote = unregisteredGuestEntitySessionBeanRemote;
        this.reservationEntitySessionBeanRemote = reservationEntitySessionBeanRemote;
        this.allocationSessionBeanRemote = allocationSessionBeanRemote;
    }

    public void runApp() throws InvalidLoginCredentialException, InvalidAccessRightException {
        Integer response = 0;

        while (true) {

            System.out.println("\nWelcome to HoRS Management Client");
            System.out.println("1: Login");
            System.out.println("2: Exit");
            response = 0;

            while (response < 1 || response > 99) {
                System.out.print("> ");
                response = sc.nextInt();
                sc.nextLine();
                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login Successful");
                        systemAdminModule = new SystemAdminModule(employeeEntitySessionBeanRemote, roomTypeEntitySessionBeanRemote, partnerEntitySessionBeanRemote,
                                currentEmployeeEntity, allocationSessionBeanRemote);
                        hotelOperationModule = new HotelOperationModule(employeeEntitySessionBeanRemote, roomTypeEntitySessionBeanRemote,
                                roomEntitySessionBeanRemote, currentEmployeeEntity, roomRateEntitySessionBeanRemote, allocationSessionBeanRemote);
                        frontOfficeModule = new FrontOfficeModule(employeeEntitySessionBeanRemote, roomTypeEntitySessionBeanRemote,
                                roomEntitySessionBeanRemote, currentEmployeeEntity, roomRateEntitySessionBeanRemote, guestEntitySessionBeanRemote,
                                unregisteredGuestEntitySessionBeanRemote, reservationEntitySessionBeanRemote);
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
            if (response == 2) {
                break;
            }
        }
    }

    private void doLogin() throws InvalidLoginCredentialException {
        String username = "";
        String password = "";

        System.out.println("\nLogin: ");
        System.out.print("Enter username> ");
        username = sc.nextLine().trim();
        System.out.print("Enter password> ");
        password = sc.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            currentEmployeeEntity = employeeEntitySessionBeanRemote.employeeLogin(username, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    private void menuMain() throws InvalidAccessRightException {
        Integer response = 0;
        
        while (true) {
            System.out.println("\nHoRS Management Client");
            System.out.println("You are logged in as " + currentEmployeeEntity.getFullName());
            System.out.println("1: System Administration");
            System.out.println("2: Hotel Operation");
            System.out.println("3: Front Office");
            System.out.println("99: Logout"); // high so we can add more stuff
            response = 0;

            while (response < 1 || response > 99) {
                System.out.print("> ");
                response = sc.nextInt();
                sc.nextLine();
                
                if (response == 1) {
                    try {
                        systemAdminModule.menuSystemAdmin();
                    } catch (InvalidAccessRightException ex) {
                        System.out.println(ex.getMessage());
                    }
                } else if (response == 2) {
                    try {
                        hotelOperationModule.menuHotelOperation();
                    } catch (InvalidAccessRightException ex) {
                        System.out.println(ex.getMessage());
                    }
                } else if (response == 3) {
                    try {
                        frontOfficeModule.menuGuestRelation();
                    } catch (InvalidAccessRightException ex) {
                        System.out.println(ex.getMessage());
                    }
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
}
