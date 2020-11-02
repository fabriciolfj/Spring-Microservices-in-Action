package com.fabriciolfj.github.licensingservice.controller.exceptions.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandartError {

    private Integer status;
    private String title;
    private String detail;
    private String userMessage;
    private OffsetDateTime timestamp;
    private List<Object> objects;

    @Builder
    @Getter
    public static class Object {
        private String name;
        private String userMessage;
    }
}
