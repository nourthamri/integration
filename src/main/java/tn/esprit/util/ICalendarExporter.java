package tn.esprit.util;

import tn.esprit.entities.Event;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ICalendarExporter {

    public static File exportToICalendar(Event event) throws IOException {
        // Date format: YYYYMMDDTHHMMSSZ (Z = UTC time)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        ZonedDateTime startUTC = event.getDate().atZone(ZoneOffset.systemDefault()).withZoneSameInstant(ZoneOffset.UTC);


        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN:VCALENDAR\n");
        sb.append("VERSION:2.0\n");
        sb.append("PRODID:-//YourApp//iCal Exporter//EN\n");
        sb.append("BEGIN:VEVENT\n");
        sb.append("UID:").append(System.currentTimeMillis()).append("@yourapp.com\n");
        sb.append("DTSTAMP:").append(formatter.format(ZonedDateTime.now(ZoneOffset.UTC))).append("\n");
        sb.append("DTSTART:").append(formatter.format(startUTC)).append("\n");
        sb.append("SUMMARY:").append(event.getNom()).append("\n");
        sb.append("DESCRIPTION:").append(event.getDescription()).append("\n");
        sb.append("END:VEVENT\n");
        sb.append("END:VCALENDAR\n");

        // Write to file
        File file = new File("event_" + event.getNom().replaceAll("\\s+", "_") + ".ics");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(sb.toString());
        }

        return file;
    }
}
