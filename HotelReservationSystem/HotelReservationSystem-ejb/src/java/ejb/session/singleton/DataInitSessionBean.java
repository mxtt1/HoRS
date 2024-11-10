/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeEntitySessionBeanLocal;
import entities.EmployeeEntity;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enums.EmployeeRole;

/**
 *
 * @author Mark
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB(name = "EmployeeEntitySessionBeanLocal")
    private EmployeeEntitySessionBeanLocal employeeEntitySessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct() {
        if (em.find(EmployeeEntity.class, 1l)  == null) {
            EmployeeEntity adminEmployee = new EmployeeEntity(EmployeeRole.SYSTEM_ADMIN, "admin", "password", "administrator");
            employeeEntitySessionBeanLocal.createNewEmployee(adminEmployee);
            EmployeeEntity operationEmployee = new EmployeeEntity(EmployeeRole.OPS_MANAGER, "operation", "password", "operator");
            employeeEntitySessionBeanLocal.createNewEmployee(operationEmployee);
            EmployeeEntity GROEmployee = new EmployeeEntity(EmployeeRole.GRO, "GRO", "password", "matt");
            employeeEntitySessionBeanLocal.createNewEmployee(GROEmployee);
        }
        
    }

    
}
