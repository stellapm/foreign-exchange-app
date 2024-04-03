package com.fea.foreign.exchange.app.exceptions;

import static com.fea.foreign.exchange.app.constants.Exceptions.NO_RESULT;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(){
        super(NO_RESULT);
    }
}
