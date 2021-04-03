package com.example.iw.control;
import java.util.Random;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.time.*;
import org.json.*;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import javax.transaction.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDateTime;

import com.example.iw.model.*;

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
        
        List<BigDecimal> menorPrecio = new ArrayList<>();
        List<BigDecimal> mejorPuja = new ArrayList<>();
        List<?> prods = new ArrayList<>();
        prods = entityManager.createQuery("SELECT p FROM Producto p").getResultList();
        
        /*for (int i=0; i<prods.size(); i++){
            List<Oferta> mejPuja = entityManager.createNamedQuery("Oferta.mejorPuja").setParameter("productoId", prods.get(i).getId()).getResultList();
            List<Oferta> minPrecio = entityManager.createNamedQuery("Oferta.menorPrecio").setParameter("productoId", prods.get(i).getId()).getResultList(); 
    
            if((minPrecio.size() > 0)){
                menorPrecio.add(minPrecio.get(0).getPrecio());
            }
            else{
                menorPrecio.add(new BigDecimal("0"));
            }
            if((mejPuja.size() > 0)){
                mejorPuja.add(mejPuja.get(0).getPrecio());
            }
            else{
                mejorPuja.add(new BigDecimal("0"));
            }
        }
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);   
        */
        model.addAttribute("prods", prods);
            
        return "tienda";                     
    }

    @GetMapping("/producto/{id}") 
    @Transactional
    public String producto(@PathVariable long id, Model model) {
        //@RequestParam long id
        Producto prod = entityManager.find(Producto.class, id);
        List<Transaccion> trans = new ArrayList<>();
        BigDecimal ultimaVenta = new BigDecimal("0");
        BigDecimal menorPrecio = new BigDecimal("0");
        BigDecimal mejorPuja = new BigDecimal("0");
        trans = prod.getTransaccion();
        
        if(trans.size() > 0){
            ultimaVenta = trans.get(0).getOferta().getPrecio();
        }

        List<Oferta> mejPuja = entityManager.createNamedQuery("Oferta.mejorPuja").setParameter("productoId", id).getResultList();
        List<Oferta> minPrecio = entityManager.createNamedQuery("Oferta.menorPrecio").setParameter("productoId", id).getResultList(); 

        if((minPrecio.size() > 0)){
            menorPrecio = minPrecio.get(0).getPrecio();
        }
        
        if((mejPuja.size() > 0)){
            mejorPuja = mejPuja.get(0).getPrecio();
        }

        //cuando lo hagamos bien, habra que borrar la oferta de la BBDD una vez se haya realizado la transacción
        // http://localhost:8080/producto?id=1

        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("ultimaVenta", ultimaVenta);
        model.addAttribute("menorPrecio", menorPrecio);
        return "producto";                     
    }

    @GetMapping("/venta/{id}")
    @Transactional 
    public String venta(@PathVariable long id, Model model) {    
        Producto prod = entityManager.find(Producto.class, id);
        BigDecimal menorPrecio = new BigDecimal("0");
        BigDecimal mejorPuja = new BigDecimal("0");
        
        List<Oferta> mejPuja = entityManager.createNamedQuery("Oferta.mejorPuja").setParameter("productoId", id).getResultList();
        List<Oferta> minPrecio = entityManager.createNamedQuery("Oferta.menorPrecio").setParameter("productoId", id).getResultList(); 

        if((minPrecio.size() > 0)){
            menorPrecio = minPrecio.get(0).getPrecio();
        }
        
        if((mejPuja.size() > 0)){
            mejorPuja = mejPuja.get(0).getPrecio();
        }
        
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);    
        return "venta";                     
    }

    //HACER EL POST de compra y venta, donde restamos y sumamos precios al saldo de usuarios, creamos transaccion y eliminamos oferta

    @GetMapping("/compra/{id}")
    @Transactional 
    public String compra(@PathVariable long id, Model model) {    
        Producto prod = entityManager.find(Producto.class, id);
        BigDecimal menorPrecio = new BigDecimal("0");
        BigDecimal mejorPuja = new BigDecimal("0");

        List<Oferta> mejPuja = entityManager.createNamedQuery("Oferta.mejorPuja").setParameter("productoId", id).getResultList();
        List<Oferta> minPrecio = entityManager.createNamedQuery("Oferta.menorPrecio").setParameter("productoId", id).getResultList(); 

        if((minPrecio.size() > 0)){
            menorPrecio = minPrecio.get(0).getPrecio();
        }
        
        if((mejPuja.size() > 0)){
            mejorPuja = mejPuja.get(0).getPrecio();
        }
        
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);    
        return "compra";                     
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

    @GetMapping("/depositarFondo/{id}")
    @Transactional 
    public String depositarFondo(@PathVariable long id, Model model) {    
        Usuario prof = entityManager.find(Usuario.class, id);
        model.addAttribute("prof", prof);
        return "depositarFondo";                     
    }

    @PostMapping("/depositarFondo/{id}")
    @Transactional 
    public String depositarFondo(@PathVariable long id, 
        @RequestParam BigDecimal saldo,
        Model model, HttpSession session) {    

        Usuario u = entityManager.find(Usuario.class, id);
        Usuario prof = entityManager.find(Usuario.class, ((Usuario)session.getAttribute("u")).getId());
		if(u.getId() != prof.getId()){
			//response.sendError(HttpServletResponse.SC_FORBIDDEN,  "Este no es tu perfil");
			log.info("ESTE NO ES TU PERFIL.");
			return "depositarFondo";
		}
        BigDecimal nuevoSaldo = prof.getSaldo().add(saldo);
        prof.setSaldo(nuevoSaldo);
        entityManager.merge(prof);
        model.addAttribute("prof", prof);     
        return "depositarFondo";                     
    }

    
    @GetMapping("/retirarFondo/{id}")
    @Transactional 
    public String retirarFondo(@PathVariable long id, Model model) {    
        Usuario prof = entityManager.find(Usuario.class, id);
        model.addAttribute("prof", prof);
        return "retirarFondo";                     
    }

    @PostMapping("/retirarFondo/{id}")
    @Transactional 
    public String retirarFondo(@PathVariable long id, 
        @RequestParam BigDecimal saldo,
        Model model, HttpSession session) {    

        Usuario u = entityManager.find(Usuario.class, id);
        Usuario prof = entityManager.find(Usuario.class, ((Usuario)session.getAttribute("u")).getId());
		if(u.getId() != prof.getId()){
			//response.sendError(HttpServletResponse.SC_FORBIDDEN,  "Este no es tu perfil");
			log.info("ESTE NO ES TU PERFIL.");
			return "retirarFondo";
		}

        if(saldo.compareTo(prof.getSaldo()) == 1){ //PREGUNTAR AL PROFESOR POR QUÉ PETA 
            // ERROR: Selected 'text/html' given [text/html, application/xhtml+xml, image/avif, image/webp, image/apng, application/xml;q=0.9, application/signed-exchange;v=b3;q=0.9, */*;q=0.8]
			//response.sendError(HttpServletResponse.SC_FORBIDDEN,  "Este no es tu perfil");
			log.info("NO PUEDES RETIRAR MAS DINERO DE LO QUE TIENES");
            model.addAttribute("prof", prof);     
			return "retirarFondo";
        }
        BigDecimal nuevoSaldo = prof.getSaldo().subtract(saldo);
        prof.setSaldo(nuevoSaldo);
        entityManager.merge(prof);
        model.addAttribute("prof", prof);     
        return "retirarFondo";                     
    }


    @GetMapping("/formularioProducto")
    @Transactional
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
    @Transactional
    public String modificarProducto(@PathVariable long id, Model model){
        Producto prod = entityManager.find(Producto.class, id);
        model.addAttribute("prod", prod); 
        return "modificarProducto";
    }

    @PostMapping("/modificarProducto/{id}")
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

    @GetMapping("/confirmacionProducto/{id}")
    @Transactional
    public String confirmacionProducto(@PathVariable long id, Model model){
        
        Producto prod = entityManager.find(Producto.class, id);
        BigDecimal precioCompra = new BigDecimal("0");
        List<Transaccion> t = new ArrayList<>();
        t = prod.getTransaccion();
        if(t != null)
            precioCompra = t.get(0).getOferta().getPrecio();

        model.addAttribute("precioCompra", precioCompra);
        model.addAttribute("prod", prod); 

        return "confirmacionProducto";
    }

    @GetMapping("/pujar/{id}")
    @Transactional 
    public String pujar(@PathVariable long id, Model model) {    
        Producto prod = entityManager.find(Producto.class, id);
        BigDecimal menorPrecio = new BigDecimal("0");
        BigDecimal mejorPuja = new BigDecimal("0");

        List<Oferta> mejPuja = entityManager.createNamedQuery("Oferta.mejorPuja").setParameter("productoId", id).getResultList();
        List<Oferta> minPrecio = entityManager.createNamedQuery("Oferta.menorPrecio").setParameter("productoId", id).getResultList(); 

        if((minPrecio.size() > 0)){
            menorPrecio = minPrecio.get(0).getPrecio();
        }
        
        if((mejPuja.size() > 0)){
            mejorPuja = mejPuja.get(0).getPrecio();
        }
        
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);

        return "pujar";                     
    }

    @PostMapping("/pujar/{id}")
    @Transactional
    public String pujar(
        @PathVariable long id,
        @RequestParam BigDecimal precio,
        @RequestParam int tiempoExpiracion,
        Model model, HttpSession session){

        LocalDateTime localDateTime = LocalDateTime.now();
        Oferta puja = new Oferta();
        Producto prod = entityManager.find(Producto.class, id);
        Usuario u = entityManager.find(Usuario.class, ((Usuario)session.getAttribute("u")).getId());

        //CONTROLAR QUE TIENE SALDO DISPONIBLE
        puja.setProducto(prod);
        puja.setPrecio(precio);
        puja.setFechaInicio(localDateTime);
        puja.setFechaExpiracion(localDateTime.plusDays(tiempoExpiracion));
        puja.setUsuario(u);
        puja.setTipo(Oferta.Tipo.PUJA);
        entityManager.persist(puja);

        BigDecimal menorPrecio = new BigDecimal("0");
        BigDecimal mejorPuja = new BigDecimal("0");

        List<Oferta> mejPuja = entityManager.createNamedQuery("Oferta.mejorPuja").setParameter("productoId", id).getResultList();
        List<Oferta> minPrecio = entityManager.createNamedQuery("Oferta.menorPrecio").setParameter("productoId", id).getResultList(); 

        if((minPrecio.size() > 0)){
            menorPrecio = minPrecio.get(0).getPrecio();
        }
        
        if((mejPuja.size() > 0)){
            mejorPuja = mejPuja.get(0).getPrecio();
        }
       
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);
        
        return "pujar";
    }
    
    @PostMapping("/fijarPrecio/{id}")
    @Transactional
    public String fijarPrecio(
        @PathVariable long id,
        @RequestParam BigDecimal precio,
        @RequestParam int tiempoExpiracion,
        Model model, HttpSession session){

        LocalDateTime localDateTime = LocalDateTime.now();
        Oferta prec = new Oferta();
        Producto prod = entityManager.find(Producto.class, id);
        Usuario u = entityManager.find(Usuario.class, ((Usuario)session.getAttribute("u")).getId());

        prec.setProducto(prod);
        prec.setPrecio(precio);
        prec.setFechaInicio(localDateTime);
        prec.setFechaExpiracion(localDateTime.plusDays(tiempoExpiracion));
        prec.setUsuario(u);
        prec.setTipo(Oferta.Tipo.PRECIO);
        entityManager.persist(prec);

        BigDecimal menorPrecio = new BigDecimal("0");
        BigDecimal mejorPuja = new BigDecimal("0");

        List<Oferta> mejPuja = entityManager.createNamedQuery("Oferta.mejorPuja").setParameter("productoId", id).getResultList();
        List<Oferta> minPrecio = entityManager.createNamedQuery("Oferta.menorPrecio").setParameter("productoId", id).getResultList(); 

        if((minPrecio.size() > 0)){
            menorPrecio = minPrecio.get(0).getPrecio();
        }
        
        if((mejPuja.size() > 0)){
            mejorPuja = mejPuja.get(0).getPrecio();
        }

        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);  
        
        return "fijarPrecio";
    }

    @GetMapping("/fijarPrecio/{id}")
    @Transactional 
    public String fijarPrecio(@PathVariable long id, Model model) {    
        Producto prod = entityManager.find(Producto.class, id);
        BigDecimal menorPrecio = new BigDecimal("0");
        BigDecimal mejorPuja = new BigDecimal("0");

        List<Oferta> mejPuja = entityManager.createNamedQuery("Oferta.mejorPuja").setParameter("productoId", id).getResultList();
        List<Oferta> minPrecio = entityManager.createNamedQuery("Oferta.menorPrecio").setParameter("productoId", id).getResultList(); 

        if((minPrecio.size() > 0)){
            menorPrecio = minPrecio.get(0).getPrecio();
        }
        
        if((mejPuja.size() > 0)){
            mejorPuja = mejPuja.get(0).getPrecio();
        }

        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);    
        return "fijarPrecio";                     
    }

    @GetMapping("/listaPujas/{id}")
    @Transactional 
    public String listaPujas(@PathVariable long id, Model model) {  
        
        BigDecimal menorPrecio = new BigDecimal("0");
        BigDecimal mejorPuja = new BigDecimal("0");
            
        Producto prod = entityManager.find(Producto.class, id);
        
        List<Oferta> pujas = entityManager.createNamedQuery("Oferta.pujas").setParameter("productoId", id).getResultList();

        List<Oferta> mejPuja = entityManager.createNamedQuery("Oferta.mejorPuja").setParameter("productoId", id).getResultList();
        List<Oferta> minPrecio = entityManager.createNamedQuery("Oferta.menorPrecio").setParameter("productoId", id).getResultList(); 

        if((minPrecio.size() > 0)){
            menorPrecio = minPrecio.get(0).getPrecio();
        }
        
        if((mejPuja.size() > 0)){
            mejorPuja = mejPuja.get(0).getPrecio();
        }
       
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);
        model.addAttribute("pujas", pujas); 
            
        return "listaPujas";                     
    }

    @GetMapping("/listaPrecios/{id}")
    @Transactional 
    public String listaPrecios(@PathVariable long id, Model model) {    
        BigDecimal menorPrecio = new BigDecimal("0");
        BigDecimal mejorPuja = new BigDecimal("0");

        Producto prod = entityManager.find(Producto.class, id);
        List<Oferta> precios = entityManager.createNamedQuery("Oferta.precios").setParameter("productoId", id).getResultList();

        List<Oferta> mejPuja = entityManager.createNamedQuery("Oferta.mejorPuja").setParameter("productoId", id).getResultList();
        List<Oferta> minPrecio = entityManager.createNamedQuery("Oferta.menorPrecio").setParameter("productoId", id).getResultList(); 

        if((minPrecio.size() > 0)){
            menorPrecio = minPrecio.get(0).getPrecio();
        }
        
        if((mejPuja.size() > 0)){
            mejorPuja = mejPuja.get(0).getPrecio();
        }
        
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);
        model.addAttribute("precios", precios); 
            
        return "listaPrecios";                     
    }

    

}  
