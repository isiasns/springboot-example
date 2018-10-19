package com.mastering.spring.springbootexample.exception;

public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException(String msg) {
        super(msg);
    }
}
