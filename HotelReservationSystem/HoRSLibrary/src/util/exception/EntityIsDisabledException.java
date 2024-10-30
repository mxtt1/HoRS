/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package util.exception;

/**
 *
 * @author mattl
 */
public class EntityIsDisabledException extends Exception {

    /**
     * Creates a new instance of <code>EntityIsDisabledException</code> without
     * detail message.
     */
    public EntityIsDisabledException() {
    }

    /**
     * Constructs an instance of <code>EntityIsDisabledException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public EntityIsDisabledException(String msg) {
        super(msg);
    }
}
