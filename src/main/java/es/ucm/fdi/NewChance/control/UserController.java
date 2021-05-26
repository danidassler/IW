package es.ucm.fdi.NewChance.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

import es.ucm.fdi.NewChance.LocalData;
import es.ucm.fdi.NewChance.model.Mensaje;
import es.ucm.fdi.NewChance.model.Oferta;
import es.ucm.fdi.NewChance.model.Usuario;

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
		
		/*controlamos si el ID de la URL es el mismo que el de la sesion
		para que otro usuario no pueda acceder al perfil de otra persona*/
		boolean ok = comprobarUsuario(u, user, model);
		if(!ok){
			return "errorUser";
		}

        List<Oferta> pujas = entityManager.createNamedQuery("Oferta.pujasUser")
								.setParameter("userId", id)
								.getResultList(); //Aqui se necesita pujas altas

		List<Oferta> precios = entityManager.createNamedQuery("Oferta.preciosUser")
								.setParameter("userId", id)
								.getResultList();

        List<Oferta> tVentas = entityManager.createNamedQuery("Oferta.ventasUser")
								.setParameter("userId" , id)
								.getResultList();//user.getTransaccionesVenta();

        List<Oferta> tCompras = entityManager.createNamedQuery("Oferta.comprasUser")
								.setParameter("userId" , id)
								.getResultList();//user.getTransaccionesCompra();

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
		
		Usuario requester = entityManager.find(Usuario.class, ((Usuario)session.getAttribute("u")).getId());
		
		boolean ok = comprobarUsuario(target, requester, model);
		if(!ok){
			return "errorUser";
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
		File f = localData.getFile("img", "usuario"+id); //Preguntar profesor, lo más probable es un error con la ruta relativa
		//File f = new File("static/img/ipsum.jpg");
		InputStream in;
		if (f.exists()) {
			in = new BufferedInputStream(new FileInputStream(f));
		} else {
			in = new BufferedInputStream(getClass().getClassLoader()
					.getResourceAsStream("static/img/user-unknown.jpg"));  //foto por defecto
		}
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileCopyUtils.copy(in, os);
			}
		};
	}
	//Envía un mensaje específico
	@PostMapping("/{id}/msg")
	@ResponseBody
	@Transactional
	public String postMsg(@PathVariable long id, 
			@RequestBody JsonNode o, Model model, HttpSession session) 
		throws JsonProcessingException {
		
		String text = o.get("men").asText();
		Usuario receiver = entityManager.find(Usuario.class, id);
		Usuario sender = entityManager.find(Usuario.class, ((Usuario)session.getAttribute("u")).getId());
		model.addAttribute("user", receiver);

		// construye mensaje, lo guarda en BD
		Mensaje m = new Mensaje();
		m.setReceptor(receiver);
		m.setEmisor(sender);
		m.setFechaEnvio(LocalDateTime.now());
		m.setMensaje(text);
		entityManager.persist(m);
		entityManager.flush(); // to get Id before commit
		
		// construye json
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("from", sender.getUsername()); // Nickname del emisor
		rootNode.put("fromId", sender.getId()); // Id del emisor
		rootNode.put("to", receiver.getId()); // Id del receptor
		rootNode.put("text", text); // Mensaje
		String json = mapper.writeValueAsString(rootNode);
	
		log.info("Sending a message to {} with contents '{}'", id, json);

		messagingTemplate.convertAndSend("/user/"+receiver.getUsername()+"/queue/updates", json);
		return "{\"result\": \"message sent.\"}";
	}	
	//Envía un mensaje al canal de los admines, a un admin global con id = 0
	@PostMapping("/chatconadmin")
	@ResponseBody
	@Transactional
	public String postMsg3( 
			@RequestBody JsonNode o, Model model, HttpSession session) 
		throws JsonProcessingException {
		
		String text = o.get("men").asText();
		Usuario sender = entityManager.find(Usuario.class, ((Usuario)session.getAttribute("u")).getId());
		Usuario admin = entityManager.find(Usuario.class, (long) 0);
		// construye mensaje, lo guarda en BD
		Mensaje m = new Mensaje();
		m.setReceptor(admin); 
		m.setEmisor(sender);
		m.setFechaEnvio(LocalDateTime.now());
		m.setMensaje(text);
		entityManager.persist(m);
		entityManager.flush(); // to get Id before commit
			
		// construye json
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("from", sender.getUsername()); //Nombre de usuario del emisor
		rootNode.put("fromId", sender.getId()); //Id del emisor
		rootNode.put("to", "0"); // Se lo envía a canal Admin
		rootNode.put("text", text);
		String json = mapper.writeValueAsString(rootNode);
	
		messagingTemplate.convertAndSend("/topic/admin", json);

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
		Usuario requester = entityManager.find(Usuario.class, ((Usuario)session.getAttribute("u")).getId());

		boolean ok = comprobarUsuario(target, requester, model);
		if(!ok){
			return "errorUser";
		}
		
		log.info("Updating photo for user {}", id);
		File f = localData.getFile("img", "usuario"+id);
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
    public String modificarPerfil(@PathVariable long id, Model model, HttpSession session) {    
		//AQUI HABRIA QUE CONTROLARLO TAMBIEN? 
        Usuario user = entityManager.find(Usuario.class, id);
		Usuario u = (Usuario)session.getAttribute("u");

		boolean ok = comprobarUsuario(user, u, model);
		if(!ok){
			return "errorUser";
		}

        model.addAttribute("user", user);
        return "modificarPerfil";                     
    }

    @PostMapping("modificarPerfil/{id}")
    @Transactional 
    public String modificarPerfil(@PathVariable long id, 
		HttpServletResponse response,
        @RequestParam String nombre, 
		@RequestParam String apellidos,
		@RequestParam (required = false) String newpassword, 
		@RequestParam String password,
		@RequestParam String password2,
        Model model, HttpSession session) {    
		
        Usuario user = entityManager.find(Usuario.class, ((Usuario)session.getAttribute("u")).getId());

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		
		if(password.equals(password2) && passwordEncoder.matches(password, user.getPassword().substring(8))/*user.matchesPassword(password, user.getPassword().substring(8))*/){
			
			user.setNombre(nombre);
			user.setApellidos(apellidos);
			if(newpassword != ""){
				user.setPassword("{bcrypt}"+passwordEncoder.encode(newpassword));
			}
			else{
				//mostrar mensaje de error o crear pagina como errorPuja
			}
			entityManager.merge(user);
			model.addAttribute("user", user);  
		}   
		model.addAttribute("user", user);  
        return "modificarPerfil";
    }

	@GetMapping("/depositarFondo/{id}")
    public String depositarFondo(@PathVariable long id, Model model, HttpSession session) {    
        Usuario prof = entityManager.find(Usuario.class, id);
		Usuario u = (Usuario)session.getAttribute("u");

		boolean ok = comprobarUsuario(prof, u, model);
		if(!ok){
			return "errorUser";
		}

        model.addAttribute("prof", prof);
        return "depositarFondo";                     
    }

    @PostMapping("depositarFondo/{id}")
    @Transactional 
    public String depositarFondo(@PathVariable long id, 
        @RequestParam BigDecimal saldo,
        Model model, HttpSession session, HttpServletResponse response) {    

        Usuario prof = entityManager.find(Usuario.class, ((Usuario)session.getAttribute("u")).getId());

        BigDecimal nuevoSaldo = prof.getSaldo().add(saldo);
        prof.setSaldo(nuevoSaldo);
        entityManager.merge(prof);
        model.addAttribute("prof", prof);     
        return "depositarFondo";                     
    }

    
    @GetMapping("/retirarFondo/{id}")
    public String retirarFondo(@PathVariable long id, Model model, HttpSession session) {    
        Usuario prof = entityManager.find(Usuario.class, id);
		Usuario u = (Usuario)session.getAttribute("u");

		boolean ok = comprobarUsuario(prof, u, model);
		if(!ok){
			return "errorUser";
		}

        model.addAttribute("prof", prof);
        return "retirarFondo";                     
    }

    @PostMapping("retirarFondo/{id}")
    @Transactional 
    public String retirarFondo(@PathVariable long id, 
        @RequestParam BigDecimal saldo,
        Model model, HttpSession session) {    

        Usuario prof = entityManager.find(Usuario.class, ((Usuario)session.getAttribute("u")).getId());

		int errorRF = 0;
        if(saldo.compareTo(prof.getSaldo()) == 1){ 
			errorRF = 1;
			model.addAttribute("errorRF", errorRF);
            model.addAttribute("prof", prof);     
			return "retirarFondo";
        }
        BigDecimal nuevoSaldo = prof.getSaldo().subtract(saldo);
        prof.setSaldo(nuevoSaldo);
        entityManager.merge(prof);
        model.addAttribute("prof", prof);     
        return "retirarFondo";                     
    }

	@GetMapping("/errorUser")
    @Transactional
    public String errorUser(Model model, HttpSession session){

        return "errorUser";
    }

	/*controlamos si el ID de la URL es el mismo que el de la sesion
	para que otro usuario no pueda acceder al perfil de otra persona*/
	public Boolean comprobarUsuario(Usuario u, Usuario user, Model model){
		int userDiferente = 0;
		if (u.getId() != user.getId() &&
			! user.hasRole(Usuario.Rol.ADMIN)) {
			userDiferente = 1;
			model.addAttribute("userDiferente", userDiferente);
			model.addAttribute("idUser", user.getId());
			return false;
		}
		return true;
	}

}
