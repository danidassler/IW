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
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
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
                     + "WHERE u.nombre = :nombre AND u.talla = :talla AND u.id != :id"),
    @NamedQuery(name="Producto.categories",
            query="SELECT DISTINCT categorias "
                     + "FROM Producto u"),
    @NamedQuery(name="Producto.selectCat",
            query="SELECT p FROM Producto p WHERE p.enabled = 1 AND p.categorias LIKE concat('%', :categoria ,'%')"),
    @NamedQuery(name="Producto.busqueda",
            query="SELECT p FROM Producto p WHERE p.enabled = 1 AND LOWER(p.nombre) LIKE lower(concat('%', :busqueda1 ,'%')) OR LOWER(p.categorias) LIKE lower(concat('%', :busqueda1 ,'%')) OR LOWER(p.talla) LIKE lower(concat('%', :busqueda1 ,'%'))")
})
public class Producto {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

    @Column(nullable = false)
    private String nombre;

    private String desc;
    private String categorias;
    private String talla;
    private byte enabled;

    @ManyToMany(targetEntity = Color.class, fetch = FetchType.EAGER)
    private List<Color> colores;

    @OneToMany
    @JoinColumn(name="producto_id")
    private List<Oferta> oferta;
	
	@Override
	public String toString() {
		return "Producto #" + id;
	}	

    public Producto(){

    }
}