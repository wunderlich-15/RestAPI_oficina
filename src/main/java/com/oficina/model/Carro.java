package com.oficina.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Carro extends Veiculo {

    private Integer numeroPortas;
    private String chassi;

    @Override
    public double getMultiplicadorMaoDeObra() {
        return 1.0;
    }

    @Override
    public String getTipoVeiculo() {
        return "CARRO";
    }
}