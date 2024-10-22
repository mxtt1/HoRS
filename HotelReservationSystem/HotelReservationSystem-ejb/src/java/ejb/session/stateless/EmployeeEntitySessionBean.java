/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.EmployeeEntity;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mattl
 */
@Stateless
public class EmployeeEntitySessionBean implements EmployeeEntitySessionBeanRemote, EmployeeEntitySessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public long createNewEmployee(EmployeeEntity newEmployee) {
        em.persist(newEmployee);
        return newEmployee.getId();
    }

    @Override
    public List<EmployeeEntity> retrieveAllEmployees() {
        return em.createQuery("SELECT e FROM EmployeeEntity e", EmployeeEntity.class).getResultList();
    }

    @Override
    public EmployeeEntity retrieveEmployeeByUsername(String username) {
        EmployeeEntity employee = em.createQuery("SELECT e FROM EmployeeEntity e WHERE e.username = :username", EmployeeEntity.class)
             .setParameter("username", username)
             .getSingleResult();
        employee.getReservations().size();
        return employee;
    }
    
    
}
