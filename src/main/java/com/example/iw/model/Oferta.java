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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import lombok.Data;

@Entity
@Data
@NamedQueries({
	@NamedQuery(name="Oferta.byUser",
			query="SELECT o FROM Oferta o "
					+ "WHERE o.usuario.id = :userId"),
    @NamedQuery(name="Oferta.mejorPuja",
            query="SELECT o FROM Oferta o " + 
            "WHERE o.precio = (SELECT MAX(precio) FROM Oferta WHERE tipo = 0 AND producto.id = :productoId)"),
    @NamedQuery(name="Oferta.menorPrecio",
            query="SELECT o FROM Oferta o " + 
            "WHERE o.precio = (SELECT MIN(precio) FROM Oferta WHERE tipo = 1 AND producto.id = :productoId)"),
    @NamedQuery(name="Oferta.pujas",
            query="SELECT o FROM Oferta o WHERE tipo = 0 AND producto.id = :productoId"),
    @NamedQuery(name="Oferta.precios", 
            query="SELECT o FROM Oferta o WHERE tipo = 1 AND producto.id = :productoId"),

    @NamedQuery(name="Oferta.pujasUser",
            query="SELECT o FROM Oferta o WHERE tipo = 0 AND usuario.id = :userId"),
    @NamedQuery(name="Oferta.preciosUser", 
            query="SELECT o FROM Oferta o WHERE tipo = 1 AND usuario.id = :userId")
        
        })
            
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

