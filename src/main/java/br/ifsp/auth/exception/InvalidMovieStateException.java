package br.ifsp.auth.exception;

public class InvalidMovieStateException extends RuntimeException {
    public InvalidMovieStateException(String message) {
        super(message);
    }
}
