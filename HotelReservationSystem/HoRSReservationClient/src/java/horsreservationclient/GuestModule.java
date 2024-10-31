/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsreservationclient;

import ejb.session.stateless.GuestEntitySessionBeanRemote;
import ejb.session.stateless.PartnerEntitySessionBeanRemote;
import ejb.session.stateless.RoomEntitySessionBeanRemote;
import ejb.session.stateless.RoomRateEntitySessionBeanRemote;
import ejb.session.stateless.RoomTypeEntitySessionBeanRemote;
import entities.GuestEntity;
import java.util.Scanner;
import util.exception.InvalidAccessRightException;

/**
 *
 * @author Mark
 */
class GuestModule {

    private GuestEntitySessionBeanRemote guestEntitySessionBeanRemote;
    private RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote;
    private PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote;
    private RoomEntitySessionBeanRemote roomEntitySessionBeanRemote;
    private RoomRateEntitySessionBeanRemote roomRateEntitySessionBeanRemote;
    
    private GuestEntity currentGuestEntity;
    
    private final Scanner sc = new Scanner(System.in);
    
    GuestModule(GuestEntitySessionBeanRemote guestEntitySessionBeanRemote, RoomTypeEntitySessionBeanRemote roomTypeEntitySessionBeanRemote, PartnerEntitySessionBeanRemote partnerEntitySessionBeanRemote, GuestEntity currentGuestEntity) {
        this.guestEntitySessionBeanRemote = guestEntitySessionBeanRemote;
        this.roomTypeEntitySessionBeanRemote = roomTypeEntitySessionBeanRemote;
        this.partnerEntitySessionBeanRemote = partnerEntitySessionBeanRemote;
        this.roomEntitySessionBeanRemote = roomEntitySessionBeanRemote;
        this.roomRateEntitySessionBeanRemote = roomRateEntitySessionBeanRemote;
    }
    
    public void menuSystemGuest() throws InvalidAccessRightException {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        while (true) {
            System.out.println("\nHoRS System :: Reservation Client");
            System.out.println("1. not done");
            System.out.println("99. Exit");
            response = 0;

            while (response < 1 || response > 99) {
                System.out.print("> ");
                response = sc.nextInt();
                if (response == 1) {
                    
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
