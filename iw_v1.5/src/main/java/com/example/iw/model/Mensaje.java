package com.example.iw.model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Entity
@Data
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
