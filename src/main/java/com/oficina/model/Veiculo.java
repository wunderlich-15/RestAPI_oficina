package com.oficina.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Veiculo {
    protected Long id;
    protected String placa;
    protected String modelo;
    protected String marca;
    protected Integer ano;
    protected Long clienteId; 

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected Cliente cliente;


    public abstract double getMultiplicadorMaoDeObra();

    public abstract String getTipoVeiculo();
}