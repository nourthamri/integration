package utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class PdfExporter {
    public static <T> void exportToPDF(
            List<T> data,
            String filePath,
            String[] headers,
            BiConsumer<PdfPTable, T> rowFiller
    ) {
        Document document = new Document(PageSize.A4.rotate()); // Format paysage
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Style du titre
            Paragraph title = new Paragraph("Export des Données",
                    new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY));
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Configuration du tableau
            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setHeaderRows(1);

            // Style des en-têtes
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new BaseColor(33, 150, 243)); // Bleu Material Design
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                cell.setBorderWidth(1.5f);
                table.addCell(cell);
            }

            // Style des données
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);
            table.setSplitLate(false); // Gestion du débordement

            // Remplissage des données
            for (T item : data) {
                rowFiller.accept(table, item);
            }

            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
    public static void exportStatsToPDF(
            Map<String, Integer> statsStatut,
            Map<String, Integer> statsCategorie,
            Map<String, Integer> statsMois,
            String filePath
    ) {
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Titre principal
            Paragraph title = new Paragraph("Statistiques des Réclamations",
                    new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Section Statuts
            addSection(document, "Statuts", statsStatut);
            // Section Catégories
            addSection(document, "Catégories", statsCategorie);
            // Section Mois
            addSection(document, "Réclamations par Mois", statsMois);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    private static void addSection(Document document, String sectionTitle, Map<String, Integer> data) throws DocumentException {
        // Sous-titre
        Paragraph subTitle = new Paragraph(sectionTitle,
                new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
        subTitle.setSpacingBefore(20f);
        document.add(subTitle);

        // Tableau
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        // En-têtes
        PdfPCell headerCell = new PdfPCell(new Phrase("Nom", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE)));
        headerCell.setBackgroundColor(new BaseColor(33, 150, 243));
        table.addCell(headerCell);
        headerCell = new PdfPCell(new Phrase("Valeur", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE)));
        headerCell.setBackgroundColor(new BaseColor(33, 150, 243));
        table.addCell(headerCell);

        // Données
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            table.addCell(new Phrase(entry.getKey(), new Font(Font.FontFamily.HELVETICA, 10)));
            table.addCell(new Phrase(String.valueOf(entry.getValue()), new Font(Font.FontFamily.HELVETICA, 10)));
        }

        document.add(table);
    }
}