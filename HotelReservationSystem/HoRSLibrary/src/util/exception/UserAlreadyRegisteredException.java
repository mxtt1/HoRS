/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package util.exception;

/**
 *
 * @author mattl
 */
public class UserAlreadyRegisteredException extends Exception {

    /**
     * Creates a new instance of <code>UserAlreadyRegisteredException</code>
     * without detail message.
     */
    public UserAlreadyRegisteredException() {
    }

    /**
     * Constructs an instance of <code>UserAlreadyRegisteredException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public UserAlreadyRegisteredException(String msg) {
        super(msg);
    }
}
