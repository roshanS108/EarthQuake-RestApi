package com.rest.earthquakeapi.exception;
public class InvalidDoubleException extends RuntimeException {

    public InvalidDoubleException(String message) {
        super(message);
    }

    public InvalidDoubleException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDoubleException(Throwable cause) {
        super(cause);
    }

    public static void valid(Object... o) throws InvalidTypeException{
            for(Object obj: o){
                if(obj instanceof Double) {
                    throw new InvalidTypeException("Valid");
                }
                else{
                    throw new InvalidTypeException("Please provide only values of type Double.");
                }
            }
    }
}
