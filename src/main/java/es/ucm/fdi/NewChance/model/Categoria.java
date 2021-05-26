package es.ucm.fdi.NewChance.model;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.ManyToOne;
import lombok.Data;


public class Categoria{
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

    private String nombre;

    //private List<Producto> productos;
}