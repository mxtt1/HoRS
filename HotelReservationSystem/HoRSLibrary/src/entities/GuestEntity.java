/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author mattl
 */
@Entity
public class GuestEntity extends UnregisteredGuestEntity implements Serializable {

    @Column(nullable = false, unique = true, length = 32)
    private String name;
    
    @Column(nullable = false, length = 32)
    private String password;
    
    @OneToMany(mappedBy = "booker")
    private List<ReservationEntity> bookedReservations;

    public GuestEntity() {
    }

    public GuestEntity(String name, String passportNum, String password) {
        super(passportNum);
        this.name = name;
        this.password = password;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GuestEntity)) {
            return false;
        }
        GuestEntity other = (GuestEntity) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.GuestEntity[ id=" + getId() + " ]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public List<ReservationEntity> getBookedReservations() {
        return bookedReservations;
    }

    public void setBookedReservations(List<ReservationEntity> bookedReservations) {
        this.bookedReservations = bookedReservations;
    }
    
}
