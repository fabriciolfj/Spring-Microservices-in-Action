package com.fabriciolfj.github.licensingservice.controller.exceptions;

public class OrganizationNotfound extends RuntimeException {

    public OrganizationNotfound(final String msg) {
        super(msg);
    }
}
