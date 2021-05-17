package com.example.iw.model;
import java.math.BigDecimal;
import java.util.Arrays;
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
import javax.persistence.Transient;
//import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Data
@NamedQueries({
	@NamedQuery(name="Usuario.byUsername",
			query="SELECT u FROM Usuario u "
					+ "WHERE u.username = :username AND u.enabled = 1"),
	@NamedQuery(name="Usuario.hasUsername",
			query="SELECT COUNT(username) "
					+ "FROM Usuario u "
					+ "WHERE u.username = :username")
})
public class Usuario implements Transferable<Usuario.Transfer> {

	@Autowired
	@Transient
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private PasswordEncoder passwordEncoder;

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, unique = true)
	private String username;

	private String nombre;

    private String apellidos;

	private String password;

    @NonNull
    public enum Rol{
		USER,
		ADMIN,
	};

	private String rol;

	private byte enabled;

    @Column(nullable = false)
    private BigDecimal saldo;

	//Duda ManyToMany: dos usuarios en cada transacción (comprador y vendedor), un usuario puede tener muchas transacciones
	/*@OneToMany
	@JoinColumn(name="comprador_id")
	private List<Transaccion> transaccionesCompra;

	@OneToMany
	@JoinColumn(name="vendedor_id")
	private List<Transaccion> transaccionesVenta;*/

	@OneToMany
	@JoinColumn(name="comprador_id")
	private List<Oferta> comprar;

	@OneToMany
	@JoinColumn(name="vendedor_id")
	private List<Oferta> vender;

    @OneToMany
	@JoinColumn(name="receptor_id")
	private List<Mensaje> mensajesRecibidos;

	@OneToMany
	@JoinColumn(name="emisor_id")
	private List<Mensaje> mensajesEnviados;

	@Override
	public String toString() {
		return "Usuario #" + id;
	}	

	public Usuario(){

	}
	/**
	 * Checks whether this user has a given role.
	 * @param rol to check
	 * @return true iff this user has that role.
	 */

	public boolean hasRole(Rol rols) {
		String roleName = rols.name();
		return Arrays.stream(rol.split(","))
				.anyMatch(r -> r.equals(roleName));
	}
	
	/*
	public boolean hasRole(Rol rol) {
		String rolName = rol.name();
		boolean ok = false;
		if (rolName == "ADMIN"){
			ok = true;
		}
		return ok;
	}
	*/
	/**
	 * Encodes a password, so that it can be saved for future checking. Notice
	 * that encoding the same password multiple times will yield different
	 * encodings, since encodings contain a randomly-generated salt.
	 * @param rawPassword to encode
	 * @return the encoded password (typically a 60-character string)
	 * for example, a possible encoding of "test" is 
	 * {bcrypt}$2y$12$XCKz0zjXAP6hsFyVc8MucOzx6ER6IsC1qo5zQbclxhddR1t6SfrHm
	 */
	public String encodePassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}
	
	public Boolean matchesPassword(String rawPassword, String encodePassword) {
		return passwordEncoder.matches(rawPassword, encodePassword);
	}
	
	@Getter
    @AllArgsConstructor
    public static class Transfer {
		private long id;
        private String username;
		private int totalRecividos;
		private int totalEnviados;
    }

	@Override
    public Transfer toTransfer() {
		return new Transfer(id,	username, mensajesRecibidos.size(), mensajesEnviados.size());
    }
}
//CONTROLAR QUE NO SE METE UN PRODUCTO YA EXISTENTE