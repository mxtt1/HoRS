/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import entities.EmployeeEntity;
import java.util.Scanner;
import util.enums.EmployeeRole;
import util.exception.InvalidAccessRightException;

/**
 *
 * @author Mark
 */
public class SystemAdminModule {
    private EmployeeEntitySessionBeanRemote employeeEntitySessionBeanRemote;
    private RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;
    
    private EmployeeEntity currentEmployeeEntity;
    
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
            System.out.println("HoRS System :: System Administration");
            System.out.println("1. New Function");
            response = 0;
            
            while(response < 1 || response > 99) {
                System.out.println(">");
                response = sc.nextInt();
                if (response == 1) {
                    // NEW METHOD
                }
            }
        }
    }
    
}
