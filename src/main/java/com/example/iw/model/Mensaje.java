package com.example.iw.model;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.time.format.DateTimeFormatter;
import lombok.Data;
import lombok.Getter;
import lombok.AllArgsConstructor;


@Entity
@Data
@NamedQueries({
	@NamedQuery(name="Mensaje.recibidos",
			query="SELECT m FROM Mensaje m "
					+ "WHERE m.emisor.id = :clienteId AND m.receptor.id = :userId"),
	@NamedQuery(name="Mensaje.enviados",
			query="SELECT m FROM Mensaje m "
					+ "WHERE m.emisor.id = :userId AND m.receptor.id = :clienteId"),
	@NamedQuery(name="Mensaje.findNullMsg",
			query="SELECT m FROM Mensaje m "
					+ "WHERE m.emisor.id = :receptorId AND m.receptor.id is NULL")

        })
public class Mensaje implements Transferable<Mensaje.Transfer>{
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    
    private String mensaje;
    private LocalDateTime fechaEnvio;

    @ManyToOne
    private Usuario emisor;

    @ManyToOne
    private Usuario receptor;

    @Override
    public String toString() {
        return "Mensaje #" + id;
    }

    public Mensaje(){

    }

    @Getter
    @AllArgsConstructor
	public static class Transfer {
		private String from;
		private String to;
		private String sent;
		private String received;
		private String text;
		long id;
		public Transfer(Mensaje m){
			this.from = m.getEmisor().getUsername();
			this.to = m.getReceptor().getUsername();
			this.sent = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(m.getFechaEnvio());
			/*this.received = m.getDateRead() == null ?
					null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(m.getDateRead());*/
			this.text = m.getMensaje();
			this.id = m.getId();
		}
	}

	@Override
	public Transfer toTransfer() {
		return new Transfer(emisor.getUsername(), receptor.getUsername(), 
			DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(fechaEnvio), null, mensaje, id
			/*dateRead == null ? null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateRead),
			text, id*/
        );
    }
}
