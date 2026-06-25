package com.oficina.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Moto extends Veiculo {

    private Integer cilindradas;
    private String tipoMoto;

    @Override
    public double getMultiplicadorMaoDeObra() {
        return 0.8;
    }

    @Override
    public String getTipoVeiculo() {
        return "MOTO";
    }
}