/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

/**
 *
 * @author mattl
 */
@Entity
@NamedQueries ({
    @NamedQuery(name = "findOverlappingReservations", query = "SELECT re FROM ReservationEntity re WHERE (re.startDate < :givenEndDate AND re.endDate > :givenStartDate) AND re.roomType = :roomType")
})
public class ReservationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date startDate;
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endDate;
    @Column(nullable = false)
    private int quantity;
    
    @Column(precision = 16, scale = 2, nullable = false)
    private BigDecimal fee;
    
    @ManyToOne(optional = true)
    private EmployeeEntity employee;
    
    @ManyToOne(optional = true)
    private GuestEntity booker;
    
    @ManyToOne(optional = true)
    private PartnerEntity partner;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private UnregisteredGuestEntity occupant;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private RoomTypeEntity roomType;
    
    @OneToMany(mappedBy = "reservation")
    private List<ReservationRoomEntity> reservationRooms = new ArrayList<>();
    
    @ManyToMany()
    private List<RoomRateEntity> roomRates = new ArrayList<>();

    public ReservationEntity() {
    }

    public ReservationEntity(Date startDate, Date endDate, int quantity) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.quantity = quantity;
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
        if (!(object instanceof ReservationEntity)) {
            return false;
        }
        ReservationEntity other = (ReservationEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ReservationEntity[ id=" + id + " ]";
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public EmployeeEntity getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeEntity employee) {
        this.employee = employee;
    }

    public RoomTypeEntity getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomTypeEntity roomType) {
        this.roomType = roomType;
    }

    public List<RoomRateEntity> getRoomRates() {
        return roomRates;
    }

    public void setRoomRates(List<RoomRateEntity> roomRates) {
        this.roomRates = roomRates;
    }

    public GuestEntity getBooker() {
        return booker;
    }

    public void setBooker(GuestEntity booker) {
        this.booker = booker;
    }

    public PartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(PartnerEntity partner) {
        this.partner = partner;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<ReservationRoomEntity> getReservationRooms() {
        return reservationRooms;
    }

    public void setReservationRooms(List<ReservationRoomEntity> reservationRooms) {
        this.reservationRooms = reservationRooms;
    }

    public UnregisteredGuestEntity getOccupant() {
        return occupant;
    }

    public void setOccupant(UnregisteredGuestEntity occupant) {
        this.occupant = occupant;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

}
