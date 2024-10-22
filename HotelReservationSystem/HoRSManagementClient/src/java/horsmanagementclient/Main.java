/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import javax.ejb.EJB;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author mattl
 */
public class Main {

    @EJB(name = "EmployeeEntitySessionBeanRemote")
    private static EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote;

    @EJB(name = "RoomTypeEntitySessionBeanRemote")
    private static RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;
    
    
    public static void main(String[] args) throws InvalidLoginCredentialException, InvalidAccessRightException {
        MainApp mainApp = new MainApp(employeeEntitySessionBeanRemote, roomTypeEntitySessionBeanRemote);
        mainApp.runApp();
    }
    
}
