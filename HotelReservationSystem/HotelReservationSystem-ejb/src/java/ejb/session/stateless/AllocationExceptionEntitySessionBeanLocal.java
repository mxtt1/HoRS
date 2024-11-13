/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entities.AllocationExceptionEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Mark
 */
@Local
public interface AllocationExceptionEntitySessionBeanLocal {

    public List<AllocationExceptionEntity> getExceptionReportsForDate(Date date);
    
}
