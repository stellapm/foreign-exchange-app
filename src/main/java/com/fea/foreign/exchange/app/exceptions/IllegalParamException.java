package com.fea.foreign.exchange.app.exceptions;

import static com.fea.foreign.exchange.app.constants.Exceptions.INVALID_PARAMETER;

public class IllegalParamException extends RuntimeException {
    public IllegalParamException(String paramType){
        super(INVALID_PARAMETER + paramType);
    }
}
