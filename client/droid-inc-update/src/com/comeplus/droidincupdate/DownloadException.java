package com.comeplus.droidincupdate;

public class DownloadException extends Exception {

    private static final long serialVersionUID = 4470640314232137340L;

    public DownloadException() {
    }

    public DownloadException(String detailMessage) {
        super(detailMessage);
    }

    public DownloadException(Throwable throwable) {
        super(throwable);
    }

    public DownloadException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
