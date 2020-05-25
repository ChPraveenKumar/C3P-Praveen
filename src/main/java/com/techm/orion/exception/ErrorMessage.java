package com.techm.orion.exception;


import java.io.Serializable;

public class ErrorMessage implements Serializable {
    @SuppressWarnings("compatibility:7992816020855811459")
    private static final long serialVersionUID = 6678565272353275871L;

    private String errorCode = null;
    private String errorMessage = null;
    private String errorMessageExt = null;
    private String errorAttr[] = null;

    public ErrorMessage() {
        super();
    }

    public ErrorMessage(String errorCode, String errorMessage, String errorMessageExt, String[] errorAttr) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorMessageExt = errorMessageExt;
        this.errorAttr = errorAttr;
    }


    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessageExt(String errorMessageExt) {
        this.errorMessageExt = errorMessageExt;
    }

    public String getErrorMessageExt() {
        return errorMessageExt;
    }

    public void setErrorAttr(String[] errorAttr) {
        this.errorAttr = errorAttr;
    }

    public String[] getErrorAttr() {
        return errorAttr;
    }
}
