package com.goMaddy.multithreaded_http_fileserver.exception;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String mess){
        super(mess);
    }
}
