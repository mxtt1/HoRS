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
 * @author Mark
 */
@Entity
public class PartnerEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerEntityId;
    @Column(nullable = false, unique = true, length = 32)
    private String username;
    @Column(nullable = false, length = 32)
    private String password;

    @OneToMany(mappedBy = "partner")
    private List<ReservationEntity> reservations;
    
    public PartnerEntity() {
    }

    public PartnerEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    

    public Long getPartnerEntityId() {
        return partnerEntityId;
    }

    public void setPartnerEntityId(Long partnerEntityId) {
        this.partnerEntityId = partnerEntityId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (partnerEntityId != null ? partnerEntityId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the partnerEntityId fields are not set
        if (!(object instanceof PartnerEntity)) {
            return false;
        }
        PartnerEntity other = (PartnerEntity) object;
        if ((this.partnerEntityId == null && other.partnerEntityId != null) || (this.partnerEntityId != null && !this.partnerEntityId.equals(other.partnerEntityId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.PartnerEntity[ id=" + partnerEntityId + " ]";
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
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
    
}
