package br.ifsp.auth.exception;

public class InvalidReviewStateException extends RuntimeException {
    public InvalidReviewStateException(String message) {
        super(message);
    }
}
