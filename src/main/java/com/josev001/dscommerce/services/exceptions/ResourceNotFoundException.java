package com.josev001.dscommerce.services.exceptions;

public class ResourceNotFoundException extends RuntimeException { // RuntimeException,não vai  exigir o try-catch

    public ResourceNotFoundException(String msg){
        super(msg);
    }

}
