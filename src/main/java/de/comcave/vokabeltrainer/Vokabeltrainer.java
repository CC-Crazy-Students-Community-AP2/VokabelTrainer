package de.comcave.vokabeltrainer;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class Vokabeltrainer {
    private final Map<String, Vokabel> vokabeln;
    private final String benutzerName = ermittleBenutzerName();
    private int richtigBeantwortet;
    private int falschBeantwortet;

    public Vokabeltrainer(String vokabelDatei) {
        vokabeln = new HashMap<>();
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
        boolean shouldStop = false;

        for (Vokabel vokabel : vokabeln.values()) {
            try {
                System.out.println("Übersetze: " + vokabel.getDeutsch());
                String antwort = reader.readLine();

                if ("!stop".equalsIgnoreCase(antwort)) {
                    shouldStop = true;
                    break;
                }

                if (antwort.equalsIgnoreCase("!stats")) {
                    zeigeMeineStats();
                } else if (antwort.equalsIgnoreCase(vokabel.getEnglisch())) {
                    System.out.println("\tRichtig!\n\n");
                    richtigBeantwortet++;
                } else {
                    System.out.println("\tFalsch. Die richtige Antwort ist: " + vokabel.getEnglisch() + "\n\n");
                    falschBeantwortet++;
                }
                speichereStats();
            } catch (IOException e) {
                log.error("Fehler beim Lesen der Eingabe", e);
            }
        }

        if (shouldStop) {
            zeigeMeineStats();
            System.exit(0);
        }
    }

    public void zeigeMeineStats() {
        int AlleRichtigBeantwortet = 0;
        int AlleFalschBeantwortet = 0;

        try (InputStream input = new FileInputStream(benutzerName + ".properties")) {
            Properties properties = new Properties();
            properties.load(input);

            AlleRichtigBeantwortet = Integer.parseInt(properties.getProperty("richtigBeantwortet", "0"));
            AlleFalschBeantwortet = Integer.parseInt(properties.getProperty("falschBeantwortet", "0"));

        } catch (IOException e) {
            log.error("Fehler beim Laden der Datei, keine Sorge", e);
        }

        System.out.println("\n\n\n\n\naktuelle Statistik:");
        System.out.println("\tRichtig beantwortet: " + richtigBeantwortet);
        System.out.println("\tFalsch beantwortet: " + falschBeantwortet);
        System.out.println("\n\ngesamte Statistik:");
        System.out.println("\tRichtig beantwortet: " + AlleRichtigBeantwortet);
        System.out.println("\tFalsch beantwortet: " + AlleFalschBeantwortet);
        System.out.println("\n\n\n\n\n");
    }

    private void speichereStats() {
        try (InputStream input = new FileInputStream(benutzerName + ".properties")) {
            Properties properties = new Properties();
            properties.load(input);

            int vorhandenRichtig = Integer.parseInt(properties.getProperty("richtigBeantwortet", "0"));
            int vorhandenFalsch = Integer.parseInt(properties.getProperty("falschBeantwortet", "0"));

            vorhandenRichtig += richtigBeantwortet;
            vorhandenFalsch += falschBeantwortet;

            properties.setProperty("richtigBeantwortet", String.valueOf(vorhandenRichtig));
            properties.setProperty("falschBeantwortet", String.valueOf(vorhandenFalsch));

            try (OutputStream output = new FileOutputStream(benutzerName + ".properties")) {
                properties.store(output, null);
            }

        } catch (IOException e) {
            log.error("Fehler beim Speichern der Datei", e);
        }
    }

    public String ermittleBenutzerName() {
        try {
            return InetAddress.getLocalHost().getHostName().toLowerCase();
        } catch (UnknownHostException e) {
            log.error("Fehler beim Ermitteln des Computernamens", e);

            return "UnbekannterBenutzer";
        }
    }
}
