/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeEntitySessionBeanRemote;
import ejb.session.stateless.PartnerEntitySessionBeanRemote;
import ejb.session.stateless.RoomEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import javax.ejb.EJB;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;
import ejb.session.stateless.RoomRateEntitySessionBeanRemote;

/**
 *
 * @author mattl
 */
public class Main {

    @EJB(name = "RoomRateEntitySessionBeanRemote")
    private static RoomRateEntitySessionBeanRemote roomRateEntitySessionBean;

    @EJB(name = "RoomEntitySessionBeanRemote")
    private static RoomEntitySessionBeanRemote roomEntitySessionBean;

    @EJB(name = "PartnerEntitySessionBeanRemote")
    private static PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote;

    @EJB(name = "EmployeeEntitySessionBeanRemote")
    private static EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote;

    @EJB(name = "RoomTypeEntitySessionBeanRemote")
    private static RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;
    
    
    
    
    public static void main(String[] args) throws InvalidLoginCredentialException, InvalidAccessRightException {
        MainApp mainApp = new MainApp(employeeEntitySessionBeanRemote, roomTypeEntitySessionBeanRemote, 
                partnerEntitySessionBeanRemote, roomEntitySessionBean, roomRateEntitySessionBean);
        mainApp.runApp();
    }
    
}
