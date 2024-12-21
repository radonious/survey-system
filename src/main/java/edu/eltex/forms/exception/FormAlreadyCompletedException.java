package edu.eltex.forms.exception;

public class FormAlreadyCompletedException extends RuntimeException {
    public FormAlreadyCompletedException(String message) {
        super(message);
    }
}
