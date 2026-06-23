package com.oficina.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data 
@AllArgsConstructor 
@NoArgsConstructor
public abstract class Pessoa {
    protected Long id;
    protected String nome;
}
