/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package util.exception;

/**
 *
 * @author baron
 */
public class AlreadyExistsException extends Exception {

    /**
     * Creates a new instance of <code>AlreadyExistsException</code> without
     * detail message.
     */
    public AlreadyExistsException() {
    }

    public AlreadyExistsException(String msg) {
        super(msg);
    }
}
