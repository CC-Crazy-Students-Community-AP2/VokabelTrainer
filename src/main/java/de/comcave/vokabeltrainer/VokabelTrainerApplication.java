package de.comcave.vokabeltrainer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class VokabelTrainerApplication {

    public static void main(String[] args) {
        Vokabeltrainer vokabeltrainer = new Vokabeltrainer("vokabeln_en.properties");
        vokabeltrainer.startTraining();
    }
}