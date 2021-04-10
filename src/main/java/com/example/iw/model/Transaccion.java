package com.example.iw.model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import lombok.Data;

@Entity
@Data
@NamedQueries({
	@NamedQuery(name="Transaccion.ventas",
			query="SELECT o FROM Transaccion o WHERE o.vendedor.id = :vendedorId"),
    @NamedQuery(name="Transaccion.compras",
            query="SELECT o FROM Transaccion o WHERE o.comprador.id = :compradorId")
         
})

public class Transaccion {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

    private LocalDateTime fecha;

    private String estado;

    @ManyToOne
	private Usuario comprador;

    @ManyToOne
    private Usuario vendedor;

    @OneToOne
    private Oferta oferta;

    @ManyToOne
    private Producto producto; 
	
	@Override
	public String toString() {
		return "Transaccion #" + id;
	}
    
    public Transaccion(){

    }
    
}
