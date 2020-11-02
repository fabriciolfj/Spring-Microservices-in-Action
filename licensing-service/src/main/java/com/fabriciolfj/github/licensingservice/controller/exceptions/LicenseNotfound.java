package com.fabriciolfj.github.licensingservice.controller.exceptions;

public class LicenseNotfound extends RuntimeException {

    public LicenseNotfound(final String msg) {
        super(msg);
    }
}
