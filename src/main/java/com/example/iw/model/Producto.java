package com.example.iw.model;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
//import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
@NamedQueries({
	@NamedQuery(name="Producto.hasProducto",
			query="SELECT COUNT(nombre) "
					+ "FROM Producto u "
					+ "WHERE u.nombre = :nombre AND u.talla = :talla"),
    @NamedQuery(name="Producto.hasProducto2",
            query="SELECT COUNT(nombre) "
                     + "FROM Producto u "
                     + "WHERE u.nombre = :nombre AND u.talla = :talla AND u.id != :id")
})
public class Producto {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

    @Column(nullable = false)
    private String nombre;

    private String desc;
    private String categorias; //ibamos a poner una lista de categorias pero nos daba un error
    // el error de categorias era: Failed to initialize JPA EntityManagerFactory: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.MappingException: Could not determine type for: java.util.List, at table: producto, for columns: [org.hibernate.mapping.Column(categorias)]
    private String talla;

    @OneToMany
    @JoinColumn(name="producto_id")
    private List<Oferta> oferta;

    /*@OneToMany
    @JoinColumn(name="producto_id")
    private List<Transaccion> transaccion; */
	
	@Override
	public String toString() {
		return "Producto #" + id;
	}	

    public Producto(){

    }
}