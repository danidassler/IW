package com.example.iw.model;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Oferta {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
   
    public enum Tipo{
        PUJA,
        PRECIO
    };
    private Tipo tipo; 
    private BigDecimal precio;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaExpiracion;

    @ManyToOne
    private Usuario usuario;
   
    @ManyToOne
    private Producto producto;

    @OneToOne
    private Transaccion transaccion;

    @Override
    public String toString() {
        return "Oferta #" + id;
    }

    public Oferta(){

    }
}

