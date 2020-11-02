package com.fabriciolfj.github.licensingservice.controller.exceptions.api;

import com.fabriciolfj.github.licensingservice.controller.exceptions.LicenseNotfound;
import com.fabriciolfj.github.licensingservice.controller.exceptions.OrganizationNotfound;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;

@ControllerAdvice
public class ExceptionhandlerApi extends ResponseEntityExceptionHandler {

    private static final String ERROR = "Error system";

    @ExceptionHandler(LicenseNotfound.class)
    public ResponseEntity<?> handleLicenseNotFound(final LicenseNotfound e, final WebRequest request) {
        var status = HttpStatus.NOT_FOUND;
        var problem = createBuilder(status, e.getMessage());
        return handleExceptionInternal(e,problem, new HttpHeaders(),status, request);
    }

    @ExceptionHandler(OrganizationNotfound.class)
    public ResponseEntity<?> handleOrganizationNotfound(final OrganizationNotfound e, final WebRequest request) {
        var status = HttpStatus.NOT_FOUND;
        var problem = createBuilder(status, e.getMessage());
        return handleExceptionInternal(e,problem, new HttpHeaders(),status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (body == null) {
            body = StandartError.builder()
                    .timestamp(OffsetDateTime.now())
                    .title(status.getReasonPhrase())
                    .status(status.value())
                    .userMessage(ERROR)
                    .build();
        } else if (body instanceof String) {
            body = StandartError.builder()
                    .timestamp(OffsetDateTime.now())
                    .title((String) body)
                    .status(status.value())
                    .userMessage(ERROR)
                    .build();
        }

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    private StandartError createBuilder(final HttpStatus status, final String detail) {
        return StandartError.builder()
                .title(status.getReasonPhrase())
                .detail(detail)
                .status(status.value())
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
