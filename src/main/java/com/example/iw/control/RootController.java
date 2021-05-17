package com.example.iw.control;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.example.iw.model.Mensaje;
import com.example.iw.model.Usuario;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RootController {

    private static final Logger log = LogManager.getLogger(UserController.class);

    @Autowired
    private EntityManager entityManager;


    @GetMapping("/")
	public String index(Model model) {
		return "index";
	}

    @GetMapping("/error")
	public String error(Model model) {
		return "error";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}

    @GetMapping("/tienda") 
    public String tienda(Model model) {   
        
        /*List<BigDecimal> menorPrecio = new ArrayList<>();
        List<BigDecimal> mejorPuja = new ArrayList<>();*/
        List<?> prods = new ArrayList<>();
        prods = entityManager.createQuery("SELECT p FROM Producto p").getResultList();
        model.addAttribute("prods", prods);
            
        return "tienda";                     
    }

    @GetMapping("/tienda/{categoria}") 
    public String tiendaF(Model model, @PathVariable String categoria) {   
        
        /*List<BigDecimal> menorPrecio = new ArrayList<>();
        List<BigDecimal> mejorPuja = new ArrayList<>();*/
        List<?> prods = new ArrayList<>();
        prods = entityManager.createQuery("SELECT p FROM Producto p WHERE p.categorias LIKE '%" + categoria + "%'").getResultList();
        model.addAttribute("prods", prods);
            
        return "tienda";                     
    }

    @GetMapping("/tienda/{categoria}") 
    public String tiendaF(Model model, @PathVariable String categoria) {   
        
        /*List<BigDecimal> menorPrecio = new ArrayList<>();
        List<BigDecimal> mejorPuja = new ArrayList<>();*/
        List<?> prods = new ArrayList<>();
        prods = entityManager.createQuery("SELECT p FROM Producto p WHERE p.categorias LIKE '%" + categoria + "%'").getResultList();
        
        model.addAttribute("prods", prods);
            
        return "tienda";                     
    }
    /*
    @GetMapping("/chat") //HABLAR CON EL PROFE SOBRE ESTO
    @Transactional
    public String chat(@RequestParam long id, Model model) {    
            
        Usuario user = entityManager.find(Usuario.class, id);
        
        List<Mensaje> recibidos = user.getMensajesRecibidos();
        List<Mensaje> enviados = user.getMensajesEnviados();

        model.addAttribute("user", user);
        model.addAttribute("recibidos", recibidos);
        model.addAttribute("enviados", enviados); 
            
        return "chat";                     
    }
    */
	@GetMapping("/registro") 
	public String registro(Model model){
		return "registro";                     
	}

	@PostMapping("/registro")
    @Transactional 
	public String registro(HttpServletResponse response,
	@RequestParam String username, 
	@RequestParam String name, 
	@RequestParam String surname,
	@RequestParam String password,
	@RequestParam String password2,
	Model model) {  
        byte enabled = 1;
		Usuario user = new Usuario();
		user.setUsername(username);
		user.setNombre(name);
        user.setSaldo(new BigDecimal("0"));
		user.setApellidos(surname);
        user.setRol("USER");
        user.setEnabled(enabled);

        int errorRU = 0;
        int errorRC = 0;
        long n = (long) entityManager.createNamedQuery("Usuario.hasUsername").setParameter("username", username).getSingleResult();
        if(n > 0){ //ya existe el username en la base de datos
            errorRU = 1;
            model.addAttribute("errorRU", errorRU);
            return "registro";
        }
        else{ //el usuario no existe en la base de datos
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if(password.equals(password2)){
                user.setPassword("{bcrypt}"+passwordEncoder.encode(password));
            }else{
                errorRC = 1;
                model.addAttribute("errorRC", errorRC);
                return "registro"; 
            }
            entityManager.persist(user);
        }
		return "login";
	}

}  
