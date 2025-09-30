package ar.edu.uncuyo.dashboard.init.geo;

import lombok.Data;

@Data
public class LocalidadDTO {
    private String id;
    private String nombre;
    private DepartamentoDTO departamento;
    private ProvinciaDTO provincia;

    public Long getIdAsLong() {
        return Long.parseLong(id);
    }
}
