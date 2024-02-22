package de.comcave.vokabeltrainer;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

@Slf4j
public abstract class Methods {

    protected static void emptyLines(int lines) {
        for (int i = 0; i < lines; i++) {
            System.out.println();
        }
    }

    protected static String ermittleBenutzer() {
        try {
            return InetAddress.getLocalHost().getHostName().toLowerCase();
        } catch (UnknownHostException e) {
            log.error("Fehler beim Ermitteln des Computernamens", e);

            return "UnbekannterBenutzer";
        }
    }

    public static File erstelleDatei(String datei) {
        File file = new File(datei);
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    log.error("Fehler beim Erstellen der Datei");
                    return null;
                }
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void zeigeMeineStats(File benutzerDatei, int richtigBeantwortet, int falschBeantwortet) {
        int meineRichtigenAntworten = 0;
        int meineFalschenAntworten = 0;

        try (InputStream input = new FileInputStream(benutzerDatei)) {
            Properties properties = new Properties();
            properties.load(input);

            meineRichtigenAntworten = Integer.parseInt(properties.getProperty("richtigBeantwortet", "0"));
            meineFalschenAntworten = Integer.parseInt(properties.getProperty("falschBeantwortet", "0"));
        } catch (IOException e) {
            log.error("Fehler beim Laden der Datei, keine Sorge", e);
        }
        emptyLines(50);
        System.out.println("\naktuelle Statistik:");
        System.out.println("\tRichtig beantwortet: " + richtigBeantwortet);
        System.out.println("\tFalsch beantwortet: " + falschBeantwortet);
        System.out.println("\ngesamte Statistik:");
        System.out.println("\tRichtig beantwortet: " + meineRichtigenAntworten);
        System.out.println("\tFalsch beantwortet: " + meineFalschenAntworten);
        emptyLines(4);
    }

    protected static void speichereMeineStats(File benutzerDatei, int richtigTemp, int falschTemp) {
        try (InputStream input = new FileInputStream(benutzerDatei)) {
            Properties properties = new Properties();
            properties.load(input);

            int vorhandenRichtig = Integer.parseInt(properties.getProperty("richtigBeantwortet", "0"));
            int vorhandenFalsch = Integer.parseInt(properties.getProperty("falschBeantwortet", "0"));

            vorhandenRichtig += richtigTemp;
            vorhandenFalsch += falschTemp;

            properties.setProperty("richtigBeantwortet", String.valueOf(vorhandenRichtig));
            properties.setProperty("falschBeantwortet", String.valueOf(vorhandenFalsch));

            try (OutputStream output = new FileOutputStream(benutzerDatei)) {
                properties.store(output, null);
            }
        } catch (IOException e) {
            log.error("Fehler beim Speichern der Datei", e);
        }
    }
}