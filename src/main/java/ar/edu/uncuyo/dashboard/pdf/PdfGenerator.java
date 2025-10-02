package ar.edu.uncuyo.dashboard.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.stream.Stream;
import ar.edu.uncuyo.dashboard.dto.ProveedorDto;
import java.util.List;

@Component
public class PdfGenerator {
    public static void generarPdf(List<ProveedorDto> proveedores) throws DocumentException, FileNotFoundException {
        Document doc = new Document();

        PdfWriter.getInstance(doc, new FileOutputStream("empleados.pdf"));

        doc.open();
        // Título
        doc.add(new Paragraph("Lista de Empleados"));
        doc.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(4);
        tableHeader(table);
        addRow(table, proveedores);
        doc.add(table);

        doc.close();
    }

    private static void tableHeader(PdfPTable table){
        Stream.of("Id", "Nombre", "Apellido", "Mail").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            table.addCell(header);
        });
    }
    private static void addRow(PdfPTable table, List<ProveedorDto> proveedores){
        for (ProveedorDto emp : proveedores) {
            table.addCell(emp.getId().toString());
            table.addCell(emp.getPersona().getNombre());
            table.addCell(emp.getPersona().getApellido());
            table.addCell(emp.getPersona().getCorreoElectronico());
        }
    }
}
