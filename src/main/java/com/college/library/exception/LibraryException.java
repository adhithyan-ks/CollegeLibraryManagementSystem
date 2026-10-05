package com.college.library.exception;

/**
 * Custom exception class for representing errors in library operations.
 * Examples: book not found, book already issued, invalid return, etc.
 */
public class LibraryException extends Exception {

    public LibraryException(String message) {
        super(message);
    }

    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
