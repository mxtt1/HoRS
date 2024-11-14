/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entities.AllocationExceptionEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Mark
 */
@Remote
public interface AllocationExceptionEntitySessionBeanRemote {
    
        public List<AllocationExceptionEntity> getExceptionReportsForDate(Date date);

}
