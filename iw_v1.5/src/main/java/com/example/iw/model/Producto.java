package com.example.iw.model;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Producto {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

    @NotNull
    private String nombre;

    private String desc;
    private String categorias; //ibamos a poner una lista de categorias pero nos daba un error
    // el error de categorias era: Failed to initialize JPA EntityManagerFactory: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.MappingException: Could not determine type for: java.util.List, at table: producto, for columns: [org.hibernate.mapping.Column(categorias)]
    private String talla;

    @OneToMany
    @JoinColumn(name="producto_id")
    private List<Oferta> oferta;

    @OneToMany
    @JoinColumn(name="producto_id")
    private List<Transaccion> transaccion; 
	
	@Override
	public String toString() {
		return "Producto #" + id;
	}	

    public Producto(){

    }
}