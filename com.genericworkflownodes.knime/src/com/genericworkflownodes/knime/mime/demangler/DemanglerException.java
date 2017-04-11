package com.genericworkflownodes.knime.mime.demangler;

public class DemanglerException extends Exception {
    private static final long serialVersionUID = 1L;

    public DemanglerException() {
        super();
    }
    
    public DemanglerException(String msg) {
        super(msg);
    }
    
    public DemanglerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
