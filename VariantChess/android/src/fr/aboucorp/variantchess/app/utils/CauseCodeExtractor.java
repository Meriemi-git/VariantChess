package fr.aboucorp.variantchess.app.utils;

import io.grpc.StatusRuntimeException;

public class CauseCodeExtractor {
    public static int getCodeValueFromCause(Throwable t){
            if(t instanceof StatusRuntimeException){
                return ((StatusRuntimeException) t).getStatus().getCode().value();
            }
            return -1;
    }
}
