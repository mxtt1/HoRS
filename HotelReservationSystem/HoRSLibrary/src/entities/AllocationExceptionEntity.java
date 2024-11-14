/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author mattl
 */
@Entity
public class AllocationExceptionEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotNull
    private boolean resolved;
    
    @Column(nullable = false)
    @NotNull
    private String message;
    
    @OneToOne(optional = false) 
    @JoinColumn(nullable = false)
    private ReservationRoomEntity reservationRoom;

    public AllocationExceptionEntity() {
    }

    public AllocationExceptionEntity(boolean resolved, String message) {
        this.resolved = resolved;
        this.message = message;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AllocationExceptionEntity)) {
            return false;
        }
        AllocationExceptionEntity other = (AllocationExceptionEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.AllocationExceptionEntity[ id=" + id + " ]";
    }
    
    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ReservationRoomEntity getReservationRoom() {
        return reservationRoom;
    }

    public void setReservationRoom(ReservationRoomEntity reservationRoom) {
        this.reservationRoom = reservationRoom;
    }
}
