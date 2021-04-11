package com.example.iw.control;

import java.util.Random;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.time.*;
import org.json.*;

import java.io.File;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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

import com.example.iw.LocalData;
import com.example.iw.model.*;

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

    @PostMapping("formularioProducto")
    @Transactional
    public String formularioProducto(
        @RequestParam String nombre,
        @RequestParam String desc,
        @RequestParam String categorias,
        @RequestParam String talla,
        Model model){
        
        //CONTROLAR QUE NO SE METE UN PRODUCTO YA EXISTENTE
        Producto prod = new Producto();
        prod.setNombre(nombre);
        prod.setDesc(desc);
        prod.setCategorias(categorias);
        prod.setTalla(talla);
        entityManager.persist(prod);

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
        
        //CONTROLAR QUE NO SE METE UN PRODUCTO YA EXISTENTE
        Producto prod = entityManager.find(Producto.class, id);
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

        model.addAttribute("user", user); 
            
        return "administrarUsuario";                     
    }

    @GetMapping("/adminProductos")
    @Transactional 
    public String adminProductos(Model model) {    
            
        List<?> prods = entityManager.createQuery("SELECT p FROM Producto p").getResultList();
        
        model.addAttribute("prods", prods); 
            
        return "adminProductos";                     
    }
}
