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
		long adminId = 0;
		model.addAttribute("users", entityManager.createQuery(
			"SELECT u FROM Usuario u").getResultList());
		try {
			adminId = (long)entityManager.createNamedQuery("Mensaje.findAdmin").setParameter("userId", id).getSingleResult();
		}catch (Exception e) {
			log.info("Ningún admin te ha contestado");
		}
		finally{
			model.addAttribute("adminId", adminId); // Utilizado únicamente por el cliente
		}
		return "chat";
	}

	@GetMapping(path = "/userReceived/{adminId}", produces = "application/json") //Mensajes recibidos por el usuario
	@Transactional // para no recibir resultados inconsistentes
	@ResponseBody  // para indicar que no devuelve vista, sino un objeto (jsonizado)
	public List<Mensaje.Transfer> messagesFromAdmin(@PathVariable long adminId, HttpSession session) {
		List<Mensaje.Transfer> mensajes = new ArrayList<>(); //Ordenar por sent para que tenga coherencia
		if(adminId != 0){ //En caso de asignarle un admin, se mostrarán el chat
			mensajes = getAllMessages2(adminId, session);
		}
		 //En caso de no tener ningún admin asignado, no se muestra ningún mensaje

		return  mensajes;
	}

	@GetMapping(path = "/adminReceived/{clientId}", produces = "application/json") //Mensajes recibidos por el admin
	@Transactional
	@ResponseBody
	public List<Mensaje.Transfer> messagesFromClient(@PathVariable long clientId, HttpSession session) {
		List<Mensaje.Transfer> mensajes = new ArrayList<>();
		if(clientId != 0){
			mensajes = getAllMessages2(clientId, session);
		}
		else{ //En caso de ser clientId = 0, se mostrarán los mensajes de Atención al Cliente aún no contestados.
			List<Mensaje> mensajesPending = entityManager.createNamedQuery("Mensaje.findNullMsg").getResultList();
			log.info(mensajesPending);
			mensajes.addAll(mensajesPending.stream().map(Transferable::toTransfer).collect(Collectors.toList()));
		}
		return  mensajes;
	}

	//Funciones Auxiliares
	public List<Mensaje.Transfer> getAllMessages(long receptorId, HttpSession session){
		List<Mensaje.Transfer> mensajes = new ArrayList<>();
		long userId = ((Usuario)session.getAttribute("u")).getId();		
		List<Mensaje> mensajesRecibidos = entityManager.createNamedQuery("Mensaje.recibidos").setParameter("userId", userId).setParameter("clienteId", receptorId).getResultList();
		List<Mensaje> mensajesEnviados = entityManager.createNamedQuery("Mensaje.enviados").setParameter("userId", userId).setParameter("clienteId", receptorId).getResultList();
		mensajes.addAll(mensajesRecibidos.stream().map(Transferable::toTransfer).collect(Collectors.toList()));
		mensajes.addAll(mensajesEnviados.stream().map(Transferable::toTransfer).collect(Collectors.toList()));
		return mensajes;
	}

	public List<Mensaje.Transfer> getAllMessages2(long receptorId, HttpSession session){
		List<Mensaje.Transfer> mensajes = new ArrayList<>();
		long userId = ((Usuario)session.getAttribute("u")).getId();		
		List<Mensaje> msjs = entityManager.createNamedQuery("Mensaje.total").setParameter("userId", userId).setParameter("clienteId", receptorId).getResultList();
		mensajes.addAll(msjs.stream().map(Transferable::toTransfer).collect(Collectors.toList()));
		return mensajes;
	}
}