package yoanemoudilou.cahiertexte.utils;

import yoanemoudilou.cahiertexte.model.Seance;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Utilitaire de génération Excel avec Apache POI.
 * Adapté au modèle actuel où Seance contient des identifiants.
 */
public final class ExcelGenerator {

    private ExcelGenerator() {
        // Empêche l'instanciation.
    }

    /**
     * Génère un fichier Excel de suivi des séances.
     *
     * @param filePath chemin complet du fichier Excel
     * @param titre titre du document
     * @param seances liste des séances
     * @param coursParId map idCours -> libellé du cours
     * @param enseignantsParId map idEnseignant -> nom complet de l'enseignant
     */
    public static void genererFicheSeances(String filePath,
                                           String titre,
                                           List<Seance> seances,
                                           Map<Integer, String> coursParId,
                                           Map<Integer, String> enseignantsParId) throws IOException {

        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Le chemin du fichier Excel est invalide.");
        }

        List<Seance> safeSeances = seances != null ? seances : Collections.emptyList();

        Path path = Path.of(filePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = (XSSFSheet) workbook.createSheet("Séances");

            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            int rowIndex = 0;

            Row titleRow = sheet.createRow(rowIndex++);
            createCell(titleRow, 0, titre != null ? titre : "Fiche de suivi pédagogique", titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

            Row exportRow = sheet.createRow(rowIndex++);
            createCell(exportRow, 0, "Date d'export : " + DateUtils.formatDateTime(LocalDateTime.now()), dataStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));

            rowIndex++;

            Row headerRow = sheet.createRow(rowIndex++);
            String[] headers = {"N°", "Date", "Heure", "Durée", "Cours", "Enseignant", "Statut", "Contenu", "Observations"};

            for (int i = 0; i < headers.length; i++) {
                createCell(headerRow, i, headers[i], headerStyle);
            }

            if (safeSeances.isEmpty()) {
                Row row = sheet.createRow(rowIndex);
                createCell(row, 0, "Aucune séance disponible.", dataStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 8));
            } else {
                int numero = 1;

                for (Seance seance : safeSeances) {
                    Row row = sheet.createRow(rowIndex++);

                    createCell(row, 0, String.valueOf(numero++), dataStyle);
                    createCell(row, 1, DateUtils.formatDate(seance.getDateSeance()), dataStyle);
                    createCell(row, 2, DateUtils.formatTime(seance.getHeureSeance()), dataStyle);
                    createCell(row, 3, seance.getDuree() != null ? seance.getDuree() + " min" : "", dataStyle);
                    createCell(row, 4, resolveLabel(coursParId, seance.getCoursId(), "Cours"), dataStyle);
                    createCell(row, 5, resolveLabel(enseignantsParId, seance.getEnseignantId(), "Enseignant"), dataStyle);
                    createCell(row, 6, seance.getStatut() != null ? seance.getStatut().name() : "", dataStyle);
                    createCell(row, 7, seance.getContenu(), dataStyle);
                    createCell(row, 8, seance.getObservations(), dataStyle);
                }
            }

            for (int i = 0; i < 9; i++) {
                sheet.autoSizeColumn(i);
            }

            sheet.createFreezePane(0, 4);

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setWrapText(true);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private static void createCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(sanitizeForExcel(value != null ? value : ""));
        cell.setCellStyle(style);
    }

    private static String sanitizeForExcel(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        if (value.startsWith("=") || value.startsWith("+") || value.startsWith("-") || value.startsWith("@")) {
            return "'" + value;
        }

        return value;
    }

    private static String resolveLabel(Map<Integer, String> labels, Integer id, String prefix) {
        if (id == null) {
            return "";
        }

        if (labels != null && labels.containsKey(id)) {
            return labels.get(id);
        }

        return prefix + " #" + id;
    }
}
