package com.example.iw.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.lang.Object;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.example.iw.LocalData;
import com.example.iw.model.*;

/**
 * User-administration controller
 * 
 * @author mfreire
 */
@Controller()
@RequestMapping("perfil")
public class UserController {
	
	private static final Logger log = LogManager.getLogger(UserController.class);

	@Autowired 
	private EntityManager entityManager;
	
	@Autowired
	private LocalData localData;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@GetMapping("/{id}")
	public String getUser(@PathVariable long id, Model model, HttpSession session) 			
			throws JsonProcessingException {		
		Usuario u = entityManager.find(Usuario.class, id);
		model.addAttribute("user", u);
		Usuario user = entityManager.find(Usuario.class, ((Usuario)session.getAttribute("u")).getId());

		List<Oferta> ofertas = entityManager.createNamedQuery("Oferta.byUser")
				.setParameter("userId", id)
				.getResultList();

        List<Oferta> pujas = new ArrayList<>(); //Aqui se necesita pujas altas
        List<Oferta> precios = new ArrayList<>(); //Aqui se necesita precios bajos

        //estos gets no sabemos si estan bien, porque tenemos una transaccion
        //la transaccion es: sergio vende a dani un producto aceptando su puja mas alta
        //en los perfiles a dani si le sale la compra pero a sergio no le sale la venta
        List<Transaccion> tVentas = entityManager.createQuery("SELECT o FROM Transaccion o where o.vendedor = '" + id + "'").getResultList();//user.getTransaccionesVenta();
        List<Transaccion> tCompras = entityManager.createQuery("SELECT o FROM Transaccion o where o.comprador = '" + id + "'").getResultList();//user.getTransaccionesCompra();

		for(Oferta oferta : ofertas){
            if(oferta.getTipo() == Oferta.Tipo.PUJA){
                pujas.add(oferta);
			}
            else{
                precios.add(oferta);
			}
        }

		model.addAttribute("tVentas", tVentas); 
        model.addAttribute("tCompras", tCompras); 
        model.addAttribute("pujas", pujas); 
        model.addAttribute("precios", precios); 

		// construye y envía mensaje JSON
		Usuario request = (Usuario)session.getAttribute("u");
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("text", request.getUsername() + " is looking up " + u.getUsername());
		String json = mapper.writeValueAsString(rootNode);
		
		messagingTemplate.convertAndSend("/topic/admin", json);

		return "perfil";
	}	
	
	@ResponseStatus(
		value=HttpStatus.FORBIDDEN, 
		reason="No eres administrador, y éste no es tu perfil")  // 403
	public static class NoEsTuPerfilException extends RuntimeException {}

	@PostMapping("/{id}")
	@Transactional
	public String postUser(
			HttpServletResponse response,
			@PathVariable long id, 
			@ModelAttribute Usuario edited, 
			@RequestParam(required=false) String pass2,
			Model model, HttpSession session) throws IOException {
		Usuario target = entityManager.find(Usuario.class, id);
		model.addAttribute("user", target);
		
		Usuario requester = (Usuario)session.getAttribute("u");
		if (requester.getId() != target.getId() &&
				! requester.hasRole(Usuario.Rol.ADMIN)) {
			throw new NoEsTuPerfilException();
		}
		
		if (edited.getPassword() != null && edited.getPassword().equals(pass2)) {
			// save encoded version of password
			target.setPassword(target.encodePassword(edited.getPassword()));
		}		
		target.setUsername(edited.getUsername());
		return "perfil";
	}	
	
	@GetMapping(value="/{id}/photo")
	public StreamingResponseBody getPhoto(@PathVariable long id, Model model) throws IOException {		
		File f = localData.getFile("usuario", ""+id);
		InputStream in;
		if (f.exists()) {
			in = new BufferedInputStream(new FileInputStream(f));
		} else {
			in = new BufferedInputStream(getClass().getClassLoader()
					.getResourceAsStream("static/img/ipsum.jpg"));
		}
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileCopyUtils.copy(in, os);
			}
		};
	}
	
	@PostMapping("/{id}/msg")
	@ResponseBody
	@Transactional
	public String postMsg(@PathVariable long id, 
			@RequestBody JsonNode o, Model model, HttpSession session) 
		throws JsonProcessingException {
		
		String text = o.get("mensaje").asText();
		Usuario u = entityManager.find(Usuario.class, id);
		Usuario sender = entityManager.find(
			Usuario.class, ((Usuario)session.getAttribute("u")).getId());
		model.addAttribute("user", u);
		
		// construye mensaje, lo guarda en BD
		Mensaje m = new Mensaje();
		m.setReceptor(u);
		m.setEmisor(sender);
		m.setFechaEnvio(LocalDateTime.now());
		m.setMensaje(text);
		entityManager.persist(m);
		entityManager.flush(); // to get Id before commit
		
		// construye json
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("from", sender.getUsername());
		rootNode.put("to", u.getUsername());
		rootNode.put("text", text);
		rootNode.put("id", m.getId());
		String json = mapper.writeValueAsString(rootNode);
		
		log.info("Sending a message to {} with contents '{}'", id, json);

		messagingTemplate.convertAndSend("/usuario/"+u.getUsername()+"/queue/updates", json);
		return "{\"result\": \"message sent.\"}";
	}	
	
	@PostMapping("/{id}/photo")
	public String postPhoto(
			HttpServletResponse response,
			@RequestParam("photo") MultipartFile photo,
			@PathVariable("id") String id, Model model, HttpSession session) throws IOException {
		Usuario target = entityManager.find(Usuario.class, Long.parseLong(id));
		model.addAttribute("user", target);
		
		// check permissions
		Usuario requester = (Usuario)session.getAttribute("u");
		if (requester.getId() != target.getId() &&
				! requester.hasRole(Usuario.Rol.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, 
					"No eres administrador, y éste no es tu perfil");
			return "perfil";
		}
		
		log.info("Updating photo for user {}", id);
		File f = localData.getFile("usuario", id);
		if (photo.isEmpty()) {
			log.info("failed to upload photo: emtpy file?");
		} else {
			try (BufferedOutputStream stream =
					new BufferedOutputStream(new FileOutputStream(f))) {
				byte[] bytes = photo.getBytes();
				stream.write(bytes);
			} catch (Exception e) {
				log.warn("Error uploading " + id + " ", e);
			}
			log.info("Successfully uploaded photo for {} into {}!", id, f.getAbsolutePath());
		}
		return "perfil";
	}
	
	@GetMapping("/modificarPerfil/{id}") 
    public String modificarPerfil(@PathVariable long id, Model model) {    
        Usuario user = entityManager.find(Usuario.class, id);
        model.addAttribute("user", user);
        return "modificarPerfil";                     
    }

    @PostMapping("/modificarPerfil/{id}")
    @Transactional 
    public String modificarPerfil(@PathVariable long id, 
		HttpServletResponse response,
        @RequestParam String nombre, 
		@RequestParam String apellidos,
		@RequestParam (required = false) String newpassword, 
		@RequestParam String password,
		@RequestParam String password2,
        Model model, HttpSession session) {    
		
		Usuario u = entityManager.find(Usuario.class, id);
        Usuario user = entityManager.find(
			Usuario.class, ((Usuario)session.getAttribute("u")).getId());

		if(u.getId() != user.getId()){
			//response.sendError(HttpServletResponse.SC_FORBIDDEN,  "Este no es tu perfil");
			log.info("ESTE NO ES TU PERFIL.");
			return "modificarPerfil";
		}

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		
		if(password.equals(password2) && passwordEncoder.matches(password, user.getPassword().substring(8))/*user.matchesPassword(password, user.getPassword().substring(8))*/){
			
			user.setNombre(nombre);
			user.setApellidos(apellidos);
			if(newpassword != null){
				user.setPassword("{bcrypt}"+passwordEncoder.encode(newpassword));
			}
			entityManager.merge(user);
			model.addAttribute("user", user);  
		}   
        return "modificarPerfil";
    }
}
