package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ExcelExporter {
    public static <T> void exportToExcel(
            List<T> data,
            String filePath,
            String[] headers,
            BiConsumer<Row, T> rowFiller
    ) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Export");

            // Style des en-têtes
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.MEDIUM);
            headerStyle.setBorderTop(BorderStyle.MEDIUM);
            headerStyle.setBorderLeft(BorderStyle.MEDIUM);
            headerStyle.setBorderRight(BorderStyle.MEDIUM);

            // Création des en-têtes
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Style des données
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Remplissage des données
            int rowNum = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowNum++);
                rowFiller.accept(row, item);
                row.forEach(cell -> cell.setCellStyle(dataStyle));
            }

            // Ajustement automatique des colonnes
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i) + 1000, 15000));
            }

            // Sauvegarde
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void exportStatsToExcel(
            Map<String, Integer> statsStatut,
            Map<String, Integer> statsCategorie,
            Map<String, Integer> statsMois,
            String filePath
    ) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Feuille 1 : Statuts
            Sheet sheetStatut = workbook.createSheet("Statuts");
            fillSheet(sheetStatut, "Statut", "Nombre", statsStatut);

            // Feuille 2 : Catégories
            Sheet sheetCategorie = workbook.createSheet("Catégories");
            fillSheet(sheetCategorie, "Catégorie", "Nombre", statsCategorie);

            // Feuille 3 : Mois
            Sheet sheetMois = workbook.createSheet("Mois");
            fillSheet(sheetMois, "Mois", "Nombre", statsMois);

            // Sauvegarder
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fillSheet(Sheet sheet, String col1, String col2, Map<String, Integer> data) {
        // En-têtes
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue(col1);
        headerRow.createCell(1).setCellValue(col2);

        // Données
        int rowNum = 1;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }

        // Ajustement des colonnes
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
}