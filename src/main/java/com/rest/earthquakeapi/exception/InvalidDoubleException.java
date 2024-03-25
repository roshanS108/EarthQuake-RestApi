package com.rest.earthquakeapi.exception;
public class InvalidDoubleException {
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
