package yoanemoudilou.cahiertexte.utils;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import yoanemoudilou.cahiertexte.model.Seance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Utilitaire de génération PDF avec iText.
 * Adapté au modèle actuel où Seance contient des identifiants.
 */
public final class PdfGenerator {

    private PdfGenerator() {
        // Empêche l'instanciation.
    }

    /**
     * Génère une fiche PDF des séances.
     *
     * @param filePath chemin complet du fichier PDF
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
            throw new IllegalArgumentException("Le chemin du fichier PDF est invalide.");
        }

        List<Seance> safeSeances = seances != null ? seances : Collections.emptyList();

        Path path = Path.of(filePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        try (PdfWriter writer = new PdfWriter(filePath);
             PdfDocument pdfDocument = new PdfDocument(writer);
             Document document = new Document(pdfDocument, PageSize.A4.rotate())) {

            document.setMargins(20, 20, 20, 20);

            Paragraph title = new Paragraph(titre != null ? titre : "Fiche de suivi pédagogique")
                    .setBold()
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER);

            Paragraph exportDate = new Paragraph("Date d'export : " + DateUtils.formatDateTime(LocalDateTime.now()))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);

            document.add(title);
            document.add(exportDate);
            document.add(new Paragraph(" "));

            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 10, 8, 8, 14, 14, 8, 16, 18}))
                    .useAllAvailableWidth();

            addHeaderCell(table, "N°");
            addHeaderCell(table, "Date");
            addHeaderCell(table, "Heure");
            addHeaderCell(table, "Durée");
            addHeaderCell(table, "Cours");
            addHeaderCell(table, "Enseignant");
            addHeaderCell(table, "Statut");
            addHeaderCell(table, "Contenu");
            addHeaderCell(table, "Observations");

            if (safeSeances.isEmpty()) {
                Cell emptyCell = new Cell(1, 9)
                        .add(new Paragraph("Aucune séance disponible."))
                        .setTextAlignment(TextAlignment.CENTER);
                table.addCell(emptyCell);
            } else {
                int index = 1;

                for (Seance seance : safeSeances) {
                    table.addCell(createDataCell(String.valueOf(index++)));
                    table.addCell(createDataCell(DateUtils.formatDate(seance.getDateSeance())));
                    table.addCell(createDataCell(DateUtils.formatTime(seance.getHeureSeance())));
                    table.addCell(createDataCell(seance.getDuree() != null ? seance.getDuree() + " min" : ""));
                    table.addCell(createDataCell(resolveLabel(coursParId, seance.getCoursId(), "Cours")));
                    table.addCell(createDataCell(resolveLabel(enseignantsParId, seance.getEnseignantId(), "Enseignant")));
                    table.addCell(createDataCell(seance.getStatut() != null ? seance.getStatut().name() : ""));
                    table.addCell(createDataCell(seance.getContenu()));
                    table.addCell(createDataCell(seance.getObservations()));
                }
            }

            document.add(table);
        }
    }

    private static void addHeaderCell(Table table, String text) {
        Cell cell = new Cell()
                .add(new Paragraph(text))
                .setBold()
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER);

        table.addHeaderCell(cell);
    }

    private static Cell createDataCell(String text) {
        return new Cell()
                .add(new Paragraph(text != null ? text : ""))
                .setTextAlignment(TextAlignment.LEFT);
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
