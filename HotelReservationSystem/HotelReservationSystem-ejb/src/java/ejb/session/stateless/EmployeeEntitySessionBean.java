/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.EmployeeEntity;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import util.exception.AlreadyExistsException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author mattl
 */
@Stateless
public class EmployeeEntitySessionBean implements EmployeeEntitySessionBeanRemote, EmployeeEntitySessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Inject
    private Validator validator;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public long createNewEmployee(EmployeeEntity newEmployee) throws InputDataValidationException, AlreadyExistsException, UnknownPersistenceException {
        try {
            Set<ConstraintViolation<EmployeeEntity>> constraintViolations = validator.validate(newEmployee);
            if (constraintViolations.isEmpty()) {
                em.persist(newEmployee);
                em.flush();
                return newEmployee.getId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new AlreadyExistsException("Employee already exists");
                }
            }
            throw new UnknownPersistenceException(ex.getMessage());

        }
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

    @Override
    public EmployeeEntity employeeLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            EmployeeEntity currentEmployee = this.retrieveEmployeeByUsername(username);
            if (currentEmployee.getPassword().equals(password)) {
                return currentEmployee;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (NoResultException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<EmployeeEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
