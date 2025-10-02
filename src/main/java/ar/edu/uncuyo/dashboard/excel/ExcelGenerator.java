package ar.edu.uncuyo.dashboard.excel;

import ar.edu.uncuyo.dashboard.dto.DireccionDto;
import ar.edu.uncuyo.dashboard.dto.EmpresaDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelGenerator {

    private static final String SHEET_NAME = "Empresas";
    private static final String[] COLUMNAS = {"ID", "Nombre", "CUIT", "Dirección", "Email"};

    public void exportarEmpresas(List<EmpresaDto> empresas, OutputStream out) {
        if (out == null) {
            throw new IllegalArgumentException("El OutputStream no puede ser nulo");
        }

        RuntimeException runtimeException = null;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(SHEET_NAME);

            CellStyle headerStyle = buildHeaderStyle(workbook);
            createHeaderRow(sheet, headerStyle);

            int rowIndex = 1;
            if (empresas != null) {
                for (EmpresaDto empresa : empresas) {
                    Row row = sheet.createRow(rowIndex++);
                    fillEmpresaRow(row, empresa);
                }
            }

            for (int i = 0; i < COLUMNAS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            out.flush();
        } catch (Exception e) {
            runtimeException = (e instanceof RuntimeException)
                ? (RuntimeException) e
                : new RuntimeException("Error al generar Excel de empresas", e);
            throw runtimeException;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                if (runtimeException != null) {
                    runtimeException.addSuppressed(e);
                } else {
                    throw new RuntimeException("No se pudo cerrar el OutputStream de Excel", e);
                }
            }
        }
    }

    private CellStyle buildHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerStyle;
    }

    private void createHeaderRow(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < COLUMNAS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(COLUMNAS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillEmpresaRow(Row row, EmpresaDto empresa) {
        if (empresa == null) {
            for (int i = 0; i < COLUMNAS.length; i++) {
                row.createCell(i).setCellValue("");
            }
            return;
        }

        row.createCell(0).setCellValue(defaultString(resolveProperty(empresa, "getId")));
        row.createCell(1).setCellValue(defaultString(resolveProperty(empresa, "getNombre", "getRazonSocial")));
        row.createCell(2).setCellValue(defaultString(resolveProperty(empresa, "getCuit")));
        row.createCell(3).setCellValue(buildDireccion(empresa));
        row.createCell(4).setCellValue(defaultString(resolveProperty(empresa, "getCorreoElectronico", "getEmail")));
    }

    private String buildDireccion(EmpresaDto empresa) {
        DireccionDto direccion = empresa.getDireccion();
        List<String> partes = new ArrayList<>();

        if (direccion != null) {
            addIfPresent(partes, direccion.getCalle());
            addIfPresent(partes, direccion.getNumeracion());
            addIfPresent(partes, direccion.getBarrio());
            addIfPresent(partes, direccion.getManzanaPiso());
            addIfPresent(partes, direccion.getCasaDepartamento());
            addIfPresent(partes, direccion.getReferencia());
        }

        addIfPresent(partes, empresa.getNombreLocalidad());
        addIfPresent(partes, empresa.getNombreDepartamento());
        addIfPresent(partes, empresa.getNombreProvincia());
        addIfPresent(partes, empresa.getNombrePais());

        return String.join(", ", partes);
    }

    private void addIfPresent(List<String> partes, String valor) {
        if (valor != null && !valor.isBlank()) {
            partes.add(valor);
        }
    }

    private String defaultString(String valor) {
        return valor != null ? valor : "";
    }

    private String resolveProperty(EmpresaDto empresa, String... methodNames) {
        if (empresa == null || methodNames == null) {
            return "";
        }

        for (String methodName : methodNames) {
            if (methodName == null || methodName.isBlank()) {
                continue;
            }
            try {
                Object value = empresa.getClass().getMethod(methodName).invoke(empresa);
                if (value != null) {
                    return value.toString();
                }
            } catch (Exception ignored) {
                // el método no existe o produjo un error, se intenta con el siguiente
            }
        }
        return "";
    }
}
