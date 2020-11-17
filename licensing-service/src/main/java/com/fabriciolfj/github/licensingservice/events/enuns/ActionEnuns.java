package com.fabriciolfj.github.licensingservice.events.enuns;

import lombok.AllArgsConstructor;

import java.util.stream.Stream;

@AllArgsConstructor
public enum ActionEnuns {

    CREATE("created"), UPDATE("update"), DELETE("delete");

    private String description;

    public String getDescription() {
        return description;
    }

    public static ActionEnuns toEnum(final String value) {
        return Stream.of(ActionEnuns.values())
                .filter(p -> p.getDescription().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Ação não localizada"));
    }
}
