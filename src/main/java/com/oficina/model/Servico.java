package com.oficina.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Servico {
    private Long id;
    private String nomeServico;
    private BigDecimal valorBase;   

    public BigDecimal calcularValor(Veiculo veiculo) {
        if (valorBase == null) {
            return BigDecimal.ZERO;
        }

        if (veiculo == null) {
            return valorBase.setScale(2, RoundingMode.HALF_UP);
        }

        return valorBase
                .multiply(BigDecimal.valueOf(veiculo.getMultiplicadorMaoDeObra()))
                .setScale(2, RoundingMode.HALF_UP);
    }
}