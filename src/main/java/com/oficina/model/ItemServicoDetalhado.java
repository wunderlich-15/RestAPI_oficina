package com.oficina.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ItemServicoDetalhado {
    private Long servicoId;
    private String nomeServico;
    private BigDecimal valorBase;
}