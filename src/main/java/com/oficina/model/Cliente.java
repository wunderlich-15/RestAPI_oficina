package com.oficina.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor
public class Cliente {
    private Long id;
    private String nome;
    private String cpf;
    private String telefone;
}