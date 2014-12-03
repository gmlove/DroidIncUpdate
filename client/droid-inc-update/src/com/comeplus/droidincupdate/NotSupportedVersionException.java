package com.comeplus.droidincupdate;

public class NotSupportedVersionException extends Exception {

    private static final long serialVersionUID = -8701226808447217502L;

    public NotSupportedVersionException() {
    }

    public NotSupportedVersionException(String detailMessage) {
        super(detailMessage);
    }

    public NotSupportedVersionException(Throwable throwable) {
        super(throwable);
    }

    public NotSupportedVersionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
