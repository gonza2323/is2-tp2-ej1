package ar.edu.uncuyo.dashboard.txt;

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
                System.out.println(parts[0]);
                ProveedorDto proveedor = new ProveedorDto();
                //proveedor.setId(Long.parseLong(parts[0]));
                proveedor.getPersona().setNombre(parts[0]);
                proveedor.getPersona().setApellido(parts[1]);
                proveedor.getPersona().setTelefono(parts[2]);
                proveedor.getPersona().setCorreoElectronico(parts[3]);
                proveedor.setCuit(parts[4]);
                proveedor.getDireccion().setCalle(parts[5]);
                proveedor.getDireccion().setNumeracion(parts[6]);
                String localidad = parts[7]; //ver tema localidad y id
                //long idLocalidad = localidadRepository.findByNombreAndEliminadoFalse(localidad).getId();
                //proveedor.getDireccion().setLocalidadId(idLocalidad);
                proveedores.add(proveedor);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return proveedores;
    }
}
