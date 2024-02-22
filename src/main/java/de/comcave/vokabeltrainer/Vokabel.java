package de.comcave.vokabeltrainer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vokabel {
    private String deutsch;
    private String englisch;

    public Vokabel(String deutsch, String englisch) {
        this.deutsch = deutsch;
        this.englisch = englisch;
    }
}