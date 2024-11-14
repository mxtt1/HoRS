/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author mattl
 */
@Entity
@NamedQueries ({
    @NamedQuery(name = "findActiveRoomTypes", query = "SELECT rt from RoomTypeEntity rt WHERE rt.disabled = FALSE"),
    @NamedQuery(name = "findUpgradedRoomType", query = "SELECT rt from RoomTypeEntity rt WHERE rt.disabled = FALSE AND rt.ranking = :givenRanking")
})
public class RoomTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 32, unique = true)
    @NotNull
    @Size(min = 1, max = 32)
    private String name;
    
    @Column(nullable = false)
    @NotNull
    private String description;
    
    @Column(nullable = false)
    @NotNull
    private int roomSize;
    
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String bedType;
    
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private int capacity;
    
    @Column(nullable = false)
    @NotNull
    private String amenities;
    
    @Column(nullable = false)
    @NotNull
    private int ranking;
    
    @Column(nullable = false)
    @NotNull
    private boolean disabled;
    
    @OneToMany(mappedBy = "roomType")
    private List<RoomRateEntity> allRates = new ArrayList<>();
    
    @OneToOne()   
    private RoomRateEntity normalRate;
    
    @OneToOne()
    private RoomRateEntity publishedRate;
    
    @OneToMany (mappedBy = "roomType")
    private List<RoomEntity> rooms = new ArrayList<>();
    
    @OneToMany(mappedBy = "roomType")
    private List<ReservationEntity> reservations = new ArrayList<>();
    
    public RoomTypeEntity() {
        this.disabled = false;
    }

    public RoomTypeEntity(String name, String description, String bedType, String amenities, int capacity, int roomSize) {
        this.name = name;
        this.description = description;
        this.roomSize = roomSize;
        this.bedType = bedType;
        this.capacity = capacity;
        this.amenities = amenities;
        this.ranking = 0;
        this.disabled = false;
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
        if (!(object instanceof RoomTypeEntity)) {
            return false;
        }
        RoomTypeEntity other = (RoomTypeEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.RoomTypeEntity[ id=" + id + " ]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public List<RoomRateEntity> getAllRates() {
        return allRates;
    }

    public void setAllRates(List<RoomRateEntity> allRates) {
        this.allRates = allRates;
    }

    public RoomRateEntity getNormalRate() {
        return normalRate;
    }

    public void setNormalRate(RoomRateEntity normalRate) {
        this.normalRate = normalRate;
    }

    public RoomRateEntity getPublishedRate() {
        return publishedRate;
    }

    public void setPublishedRate(RoomRateEntity publishedRate) {
        this.publishedRate = publishedRate;
    }

    public int getRoomSize() {
        return roomSize;
    }

    public void setRoomSize(int roomSize) {
        this.roomSize = roomSize;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public List<RoomEntity> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomEntity> rooms) {
        this.rooms = rooms;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public List<ReservationEntity> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationEntity> reservations) {
        this.reservations = reservations;
    }
    
}
