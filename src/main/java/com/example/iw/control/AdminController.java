package com.example.iw.control;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.example.iw.LocalData;
import com.example.iw.model.Oferta;
import com.example.iw.model.Producto;
import com.example.iw.model.Usuario;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import com.example.iw.model.Mensaje;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.RequestBody;
/**
 * Admin-only controller
 * @author mfreire
 */
@Controller()
@RequestMapping("admin")
public class AdminController {
	
	private static final Logger log = LogManager.getLogger(AdminController.class);
	
	@Autowired 
	private EntityManager entityManager;

	@Autowired
	private LocalData localData;
	
	@Autowired
	private Environment env;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("activeProfiles", env.getActiveProfiles());
		model.addAttribute("basePath", env.getProperty("com.example.base-path"));
		model.addAttribute("debug", env.getProperty("com.example.debug"));

		model.addAttribute("usuarios", entityManager.createQuery(
				"SELECT u FROM Usuario u").getResultList());
		
		return "admin";
	}
	
	@PostMapping("/toggleuser")
	@Transactional
	public String delUser(Model model,	@RequestParam long id) {
		Usuario target = entityManager.find(Usuario.class, id);
		
		if (target.getEnabled() == 1) {
			// remove profile photo
			File f = localData.getFile("usuario", ""+id);
			if (f.exists()) {
				f.delete();
			}
			// disable user
			target.setEnabled((byte)0); 
		} else {
			// enable user
			target.setEnabled((byte)1);
		}
		return index(model);
	}

	@GetMapping("/formularioProducto")
    public String formularioProducto(){
        return "formularioProducto";
    }

    @PostMapping("/formularioProducto")
    @Transactional
    public String formularioProducto(
        @RequestParam String nombre,
        @RequestParam String desc,
        @RequestParam String categorias,
        @RequestParam String talla,
        Model model, 
        HttpServletResponse response,
        @RequestParam("photo") MultipartFile photo,  
        HttpSession session) throws IOException{
        
        long n = (long) entityManager.createNamedQuery("Producto.hasProducto")
        .setParameter("nombre", nombre).setParameter("talla", talla)
        .getSingleResult();
        
        int errorP = 0;
        if(n > 0){ //ya esta el producto
            errorP = 1;
            model.addAttribute("errorP", errorP);
            return "formularioProducto";
        }

        Producto prod = new Producto();
        prod.setNombre(nombre);
        prod.setDesc(desc);
        prod.setCategorias(categorias);
        prod.setTalla(talla);
        entityManager.persist(prod);

        //para incluir la foto del nuevo producto en la carpeta
        long id = prod.getId();
        log.info("Including photo for product {}", id);
		File f = localData.getFile("img", "producto"+id);
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

        return "formularioProducto";
    }

	@GetMapping("/modificarProducto/{id}")
    public String modificarProducto(@PathVariable long id, Model model){
        Producto prod = entityManager.find(Producto.class, id);
        model.addAttribute("prod", prod); 
        return "modificarProducto";
    }

    @PostMapping("modificarProducto/{id}")
    @Transactional
    public String modificarProducto(
        @PathVariable long id,
        @RequestParam String nombre,
        @RequestParam String desc,
        @RequestParam String categorias,
        @RequestParam String talla,
        Model model){
        
        Producto prod = entityManager.find(Producto.class, id);

        long n = (long) entityManager.createNamedQuery("Producto.hasProducto2")
        .setParameter("nombre", nombre).setParameter("talla", talla).setParameter("id", id)
        .getSingleResult();

        int errorP = 0;
        if(n > 0){ //ya esta el producto
            errorP = 1;
            model.addAttribute("errorP", errorP);
            model.addAttribute("prod", prod);
            return "modificarProducto";
        }

        prod.setNombre(nombre);
        prod.setDesc(desc);
        prod.setCategorias(categorias);
        prod.setTalla(talla);
        entityManager.merge(prod);
        model.addAttribute("prod", prod);     

        return "modificarProducto";
    }

	@GetMapping("/adminUsuarios")
    @Transactional 
    public String adminUsuarios(Model model) {    
            
        List<?> users = entityManager.createQuery("SELECT u FROM Usuario u").getResultList();

        model.addAttribute("users", users); 
            
        return "adminUsuarios";                     
    }

    @GetMapping("/administrarUsuario/{id}")
    @Transactional 
    public String administrarUsuario(@PathVariable long id, Model model) {    
            
        Usuario user = entityManager.find(Usuario.class, id); 
        List<Oferta> pujas = entityManager.createNamedQuery("Oferta.pujasUser").setParameter("userId", id).getResultList(); //Aqui se necesita pujas altas
		List<Oferta> precios = entityManager.createNamedQuery("Oferta.preciosUser").setParameter("userId", id).getResultList();

        model.addAttribute("pujas", pujas); 
        model.addAttribute("precios", precios); 
        model.addAttribute("user", user); 
            
        return "administrarUsuario";                     
    }

    @GetMapping("/adminProductos")
    @Transactional 
    public String adminProductos(Model model) {    
            
        List<?> prods = entityManager.createQuery("SELECT p FROM Producto p").getResultList();
        
        model.addAttribute("prods", prods); 
            
        return "adminProductos"; 
                   
		/*List<?> prods = entityManager.createQuery("SELECT p FROM Producto p").getResultList();
		List<?> matrix = new ArrayList<>();
		int  numrows = prods.size()/4;
		model.addAttribute("numrows", numrows); 
		model.addAttribute("prods", prods); 
		int col = 0;
		for(int row = 0; row < prods.size()/4; row++){
			for(; col < 4; col++){
				
			}
		}

            
        return "adminProductos";*/
    }

    @PostMapping("banearUsuario/{id}")
    @Transactional
    public String banearUsuario(
        @PathVariable long id,
        Model model){
            Usuario user = entityManager.find(Usuario.class, id); 
            if(user.getEnabled() == 1){ //aun esta activo
                user.setEnabled((byte)0);
                entityManager.merge(user);
            }
            else{ //esta baneado
                user.setEnabled((byte)1);
                entityManager.merge(user);
            }
            
            model.addAttribute("user", user);

            return "administrarUsuario";
    }
    
    //Websockets
    @GetMapping("/adminChat/{id}")
    public String adminChat(@PathVariable long id, Model model, HttpSession session) {
        model.addAttribute("users", entityManager.createQuery(
            "SELECT u FROM Usuario u").getResultList());
        Usuario admin = entityManager.find(Usuario.class, id);
        model.addAttribute("username", admin.getUsername());
        return "adminChat";
    }

    @PostMapping("/{id}/msg")
    @ResponseBody
    @Transactional
    public String postMsg(@PathVariable long id, @RequestBody JsonNode o, Model model, HttpSession session) 
        throws JsonProcessingException {

        String text = o.get("men").asText();
        Usuario receiver = entityManager.find(Usuario.class, id);
        Usuario admin = entityManager.find(Usuario.class, ((Usuario)session.getAttribute("u")).getId());
        model.addAttribute("user", receiver);

        List<Mensaje> mensajesPending = entityManager.createNamedQuery("Mensaje.findNullMsgbyId").setParameter("userId", id).getResultList(); //Recoge todos los mensajes enviados a Atención al cliente del 
        for(Mensaje msg: mensajesPending){
            msg.setReceptor(admin);
            entityManager.merge(msg);
        }
        // construye mensaje, lo guarda en BD
        Mensaje m = new Mensaje();
        m.setReceptor(receiver);
        m.setEmisor(admin);
        m.setFechaEnvio(LocalDateTime.now());
        m.setMensaje(text);
        entityManager.persist(m);
        entityManager.flush();

        // construye json
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("from", admin.getUsername());
        rootNode.put("fromId", admin.getId());
        rootNode.put("to", receiver.getId());
        rootNode.put("text", text);
        //rootNode.put("id", m.getId());
        String json = mapper.writeValueAsString(rootNode);

        log.info("Sending a message to {} with contents '{}'", id, json);

        messagingTemplate.convertAndSend("/user/"+receiver.getUsername()+"/queue/updates", json);
        return "{\"result\": \"message sent.\"}";
    }
}
