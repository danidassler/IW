package com.example.iw.control;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;
import com.example.iw.model.*;
import java.util.Collection;
/**
 * User-administration controller
 * 
 * @author mfreire
 */
@Controller()
@RequestMapping("chat")
public class MensajeController {
	
	private static final Logger log = LogManager.getLogger(MensajeController.class);
	
	@Autowired 
	private EntityManager entityManager;
		
	@GetMapping("/{id}")
	public String getMessages(@PathVariable long id, Model model, HttpSession session) {
		model.addAttribute("users", entityManager.createQuery(
			"SELECT u FROM Usuario u").getResultList());
		return "chat";
	}
	@GetMapping(path = "/received", produces = "application/json")
	@Transactional // para no recibir resultados inconsistentes
	@ResponseBody  // para indicar que no devuelve vista, sino un objeto (jsonizado)
	public List<Mensaje.Transfer> retrieveMessages(HttpSession session) {
		long userId = ((Usuario)session.getAttribute("u")).getId();		
		Usuario u = entityManager.find(Usuario.class, userId);
		log.info("Generating message list for user {} ({} messages)", 
				u.getUsername(), u.getMensajesRecibidos().size());
		//List<Mensaje.Transfer> mensajes = Stream.concat(u.getMensajesRecibidos().stream().map(Transferable::toTransfer), u.getMensajesEnviados().stream().map(Transferable::toTransfer)).collect(Collectors.toList());
		
		List<Mensaje.Transfer> mensajes = new ArrayList<>();
		mensajes.addAll(u.getMensajesRecibidos().stream().map(Transferable::toTransfer).collect(Collectors.toList()));
		mensajes.addAll(u.getMensajesEnviados().stream().map(Transferable::toTransfer).collect(Collectors.toList()));


		return  mensajes;
	}	

	/*
	@GetMapping(path = "/unread", produces = "application/json")
	@ResponseBody
	public String checkUnread(HttpSession session) {
		long userId = ((Usuario)session.getAttribute("u")).getId();		
		long unread = entityManager.createNamedQuery("Mensaje.countUnread", Long.class)
			.setParameter("userId", userId)
			.getSingleResult();
		session.setAttribute("unread", unread);
		return "{\"unread\": " + unread + "}";
	}*/
}