package com.example.iw.model;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@NamedQueries({
	@NamedQuery(name="Usuario.byUsername",
			query="SELECT u FROM Usuario u "
					+ "WHERE u.username = :username AND u.enabled = 1"),
	@NamedQuery(name="Usuario.hasUsername",
			query="SELECT COUNT(u) "
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

	@NotNull
	private String username;

	private String nombre;

    private String apellidos;

	private String password;

    @NotNull
    public enum Rol{
		USER,
		ADMIN
	};

	private Rol rol;

	private byte enabled;

    @NotNull
    private BigDecimal saldo;

	//Duda ManyToMany: dos usuarios en cada transacción (comprador y vendedor), un usuario puede tener muchas transacciones
	@OneToMany  
	@JoinColumn(name="comprador_id")
	private List<Transaccion> transaccionesCompra;

	@OneToMany 
	@JoinColumn(name="vendedor_id")
	private List<Transaccion> transaccionesVenta;

	@OneToMany
	@JoinColumn(name="usuario_id")
	private List<Oferta> oferta;

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


	public boolean hasRole(Rol rol) {
		String rolName = rol.name();
		boolean ok = false;
		if (rolName == "ADMIN" || rolName == "USER"){
			ok = true;
		}
		return ok;
	}


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