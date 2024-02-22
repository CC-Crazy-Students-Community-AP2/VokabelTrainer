package de.comcave.vokabeltrainer;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class Vokabeltrainer extends Methods {

    private final Map<String, Vokabel> vokabeln;
    private final File benutzerDatei;
    private int richtigBeantwortet, falschBeantwortet;

    public Vokabeltrainer(String vokabelDatei) {
        vokabeln = new HashMap<>();
        benutzerDatei = erstelleDatei(ermittleBenutzer() + ".properties");

        ladeVokabeln(vokabelDatei);
    }

    private void ladeVokabeln(String vokabelDatei) {
        try (InputStream input = new FileInputStream(vokabelDatei)) {
            Properties properties = new Properties();
            properties.load(input);

            for (String deutsch : properties.stringPropertyNames()) {
                String englisch = properties.getProperty(deutsch);
                vokabeln.put(deutsch, new Vokabel(deutsch, englisch));
            }
        } catch (IOException e) {
            log.error("Fehler beim Laden der Vokabeln aus der Datei: {}", vokabelDatei, e);
        }
    }

    public void startTraining() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        for (Vokabel vokabel : vokabeln.values()) {
            try {
                int richtigSpeichern = 0;
                int falschSpeichern = 0;

                System.out.println("Übersetze: " + vokabel.getDeutsch());
                String antwort = reader.readLine();

                if ("!stop".equalsIgnoreCase(antwort) || "!ende".equalsIgnoreCase(antwort)) {
                    zeigeMeineStats(benutzerDatei, richtigBeantwortet, falschBeantwortet);
                    System.out.println("Die Übungen wurden beendet. Danke fürs Nutzen");
                    System.exit(0);
                }

                if (antwort.equalsIgnoreCase("!stats")) {
                    zeigeMeineStats(benutzerDatei, richtigBeantwortet, falschBeantwortet);
                } else {
                    if (antwort.equalsIgnoreCase(vokabel.getEnglisch())) {
                        System.out.println("\tRichtig!\n\n");
                        richtigBeantwortet++;
                        richtigSpeichern++;
                    } else {
                        System.out.println("\tFalsch. Die richtige Antwort ist: " + vokabel.getEnglisch() + "\n\n");
                        falschBeantwortet++;
                        falschSpeichern++;
                    }
                    speichereMeineStats(benutzerDatei, richtigSpeichern, falschSpeichern);
                }
            } catch (IOException e) {
                log.error("Fehler beim Lesen der Eingabe", e);
            }
        }
    }
}