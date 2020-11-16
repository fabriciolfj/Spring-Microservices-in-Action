package com.optimagrowth.organization.events.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActionEnuns {

    CREATE("created"), UPDATE("update"), DELETE("delete");

    private String description;


}
