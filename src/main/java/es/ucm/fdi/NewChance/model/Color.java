package es.ucm.fdi.NewChance.model;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import lombok.Data;

@Entity
@Data
public class Color{
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

    private String nombre;

    @ManyToMany(mappedBy="colores", fetch = FetchType.EAGER)
    private List<Producto> productos;
}