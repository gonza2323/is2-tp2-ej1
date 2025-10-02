package ar.edu.uncuyo.dashboard.txt;

import ar.edu.uncuyo.dashboard.dto.DireccionDto;
import ar.edu.uncuyo.dashboard.dto.PersonaDto;
import ar.edu.uncuyo.dashboard.dto.ProveedorDto;
import ar.edu.uncuyo.dashboard.repository.LocalidadRepository;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Component
public class TxtImporter {
    private LocalidadRepository localidadRepository;

    public static void main(String[] args) {

    }

    public List<ProveedorDto> leerArchivo() {
        List<ProveedorDto> proveedores = new ArrayList<ProveedorDto>();
        String path="migracion.txt";
        File file = new File(path);
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                System.out.println(line);
                String[] parts = line.split(";");

                System.out.println(Arrays.stream(parts).toList());

                ProveedorDto proveedor = ProveedorDto.builder()
                        .cuit(parts[4])
                        .persona(PersonaDto.builder()
                                .nombre(parts[0])
                                .apellido(parts[1])
                                .telefono(parts[2])
                                .correoElectronico(parts[3])
                                .build())
                        .direccion(DireccionDto.builder()
                                .calle(parts[5])
                                .numeracion(parts[6])
                                // acá solo vienen los nombres, los IDs se resuelven en el Service
                                .nombreLocalidad(parts[7])
                                .nombreDepartamento(parts[8])
                                .nombreProvincia(parts[9])
                                .nombrePais(parts[10])
                                .build())
                        .build();

                proveedores.add(proveedor);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return proveedores;
    }
}
