/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entities.EmployeeEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author mattl
 */
@Local
public interface EmployeeEntitySessionBeanLocal {

    long createNewEmployee(EmployeeEntity newEmployee)throws InputDataValidationException;

    List<EmployeeEntity> retrieveAllEmployees();
    
    public EmployeeEntity retrieveEmployeeByUsername(String username); 

    public EmployeeEntity employeeLogin(String username, String password) throws InvalidLoginCredentialException;
    
}
