/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entities.EmployeeEntity;
import entities.GuestEntity;
import entities.ReservationEntity;
import entities.UnregisteredGuestEntity;
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
import util.exception.UserAlreadyRegisteredException;

/**
 *
 * @author Mark
 */
@Stateless
public class GuestEntitySessionBean implements GuestEntitySessionBeanRemote, GuestEntitySessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Inject
    private Validator validator;

    @Override
    public long createNewGuest(GuestEntity newGuest) throws UserAlreadyRegisteredException, InputDataValidationException, AlreadyExistsException, UnknownPersistenceException {
        try {
            Set<ConstraintViolation<GuestEntity>> constraintViolations = validator.validate(newGuest);
            if (constraintViolations.isEmpty()) {
                List<UnregisteredGuestEntity> duplicate = em.createQuery("SELECT g FROM UnregisteredGuestEntity g WHERE g.passportNum = :passportNum")
                        .setParameter("passportNum", newGuest.getPassportNum())
                        .getResultList();

                if (!duplicate.isEmpty() && !(duplicate.get(0) instanceof GuestEntity)) { // if there exists a UNREGISTERED guest with same passport number
                    UnregisteredGuestEntity guestToDelete = duplicate.get(0);
                    List<ReservationEntity> reservations = guestToDelete.getReservations();
                    String passportNum = newGuest.getPassportNum();
                    newGuest.setPassportNum("TEMPORARY");

                    for (ReservationEntity re : reservations) {
                        re.setOccupant(newGuest);
                    }
                    newGuest.getReservations().addAll(reservations);
                    guestToDelete.getReservations().clear();
                    em.remove(guestToDelete);
                    em.persist(newGuest);
                    em.flush();
                    newGuest.setPassportNum(passportNum);
                } else if (!duplicate.isEmpty() && duplicate.get(0) instanceof GuestEntity) {
                    throw new UserAlreadyRegisteredException("Error: User has already been registered. Please log in instead.");
                } else { //
                    em.persist(newGuest);
                    em.flush();
                }
                return newGuest.getId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new AlreadyExistsException("Guest already exists");
                }
            }
            throw new UnknownPersistenceException(ex.getMessage());

        }
    }

    @Override
    public GuestEntity guestLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            GuestEntity currentGuest = this.retrieveGuestByUsername(username);
            if (currentGuest.getPassword().equals(password)) {
                return currentGuest;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (NoResultException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }

    @Override
    public GuestEntity retrieveGuestByUsername(String username) {
        GuestEntity guest = em.createQuery("SELECT e FROM GuestEntity e WHERE e.name = :name", GuestEntity.class)
                .setParameter("name", username)
                .getSingleResult();
        return guest;
    }

    @Override
    public List<UnregisteredGuestEntity> retrieveGuestByPassportNo(String passportNo) {
        List<UnregisteredGuestEntity> guests = em.createQuery("SELECT e FROM UnregisteredGuestEntity e WHERE e.passportNum = :passportNo", UnregisteredGuestEntity.class)
                .setParameter("passportNo", passportNo).getResultList();

        return guests;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<GuestEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
