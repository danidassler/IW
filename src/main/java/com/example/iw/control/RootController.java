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
        //@RequestParam long id, Hay que revisar esto 
        
        //FALTA METER MEJOR PRECIO Y PUJA MAS ALTA
        List<?> prods = entityManager.createQuery("SELECT p FROM Producto p").getResultList();
        
        model.addAttribute("prods", prods);
            
        return "tienda";                     
    }

    @GetMapping("/producto/{id}") 
    public String producto(@PathVariable long id, Model model) {
        //@RequestParam long id
        Producto prod = entityManager.find(Producto.class, id);
        List<Oferta> ofertas = prod.getOferta();
        List<Transaccion> trans = prod.getTransaccion();

        BigDecimal ultimaVenta = trans.get(0).getOferta().getPrecio();
        BigDecimal mejorPuja = new BigDecimal("0");
        BigDecimal menorPrecio = ofertas.get(0).getPrecio();

        //cuando lo hagamos bien, habra que borrar la oferta de la BBDD una vez se haya realizado la transacción
        // http://localhost:8080/producto?id=1

        for(Oferta oferta : ofertas){
            if(oferta.getTipo() == Oferta.Tipo.PUJA){
                if(oferta.getPrecio().compareTo(mejorPuja) == 1 ){
                    mejorPuja = oferta.getPrecio();
                }
            }
            else{
                if(oferta.getPrecio().compareTo(menorPrecio) == -1){
                    menorPrecio = oferta.getPrecio();
                }
            }
        }
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("ultimaVenta", ultimaVenta);
        model.addAttribute("menorPrecio", menorPrecio);
        return "producto";                     
    }



    @GetMapping("/venta/{id}") 
    public String venta(@PathVariable long id, Model model) {    
        Producto prod = entityManager.find(Producto.class, id);
        List<Oferta> ofertas = prod.getOferta();
        BigDecimal mejorPuja = new BigDecimal("0");
        BigDecimal menorPrecio = ofertas.get(0).getPrecio();

        for(Oferta oferta : ofertas){
            if(oferta.getTipo() == Oferta.Tipo.PUJA){
                if(oferta.getPrecio().compareTo(mejorPuja) == 1){
                    mejorPuja = oferta.getPrecio();
                }
            }
            else{
                if(oferta.getPrecio().compareTo(menorPrecio) == -1){
                    menorPrecio = oferta.getPrecio();
                }
            }
        }
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);    
        return "venta";                     
    }

    //HACER EL POST de compra y venta, donde restamos y sumamos precios al saldo de usuarios, creamos transaccion y eliminamos oferta

    @GetMapping("/compra/{id}") 
    public String compra(@PathVariable long id, Model model) {    
        Producto prod = entityManager.find(Producto.class, id);
        List<Oferta> ofertas = prod.getOferta();
        BigDecimal mejorPuja = new BigDecimal("0");
        BigDecimal menorPrecio = ofertas.get(0).getPrecio();

        for(Oferta oferta : ofertas){
            if(oferta.getTipo() == Oferta.Tipo.PUJA){
                if(oferta.getPrecio().compareTo(mejorPuja) == 1){
                    mejorPuja = oferta.getPrecio();
                }
            }
            else{
                if(oferta.getPrecio().compareTo(menorPrecio) == -1){
                    menorPrecio = oferta.getPrecio();
                }
            }
        }
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);    
        return "compra";                     
    }

    /*@GetMapping("/perfil") 
    public String perfil(@RequestParam long id, Model model) {    
        Usuario user = entityManager.find(Usuario.class, id);
        
        List<Oferta> ofertas = user.getOferta(); //Aqui 
        List<Oferta> pujas = new ArrayList<>(); //Aqui se necesita pujas altas
        List<Oferta> precios = new ArrayList<>(); //Aqui se necesita precios bajos

        //estos gets no sabemos si estan bien, porque tenemos una transaccion
        //la transaccion es: sergio vende a dani un producto aceptando su puja mas alta
        //en los perfiles a dani si le sale la compra pero a sergio no le sale la venta
        List<Transaccion> tVentas = user.getTransaccionesVenta();
        List<Transaccion> tCompras = user.getTransaccionesCompra();
        
        //JsonArray pujas = new JsonArray();
        //JsonArray precios = new JsonArray();
        
        for(Oferta oferta : ofertas){
            if(oferta.getTipo() == Oferta.Tipo.PUJA){
                pujas.add(oferta);
                /*Producto p = oferta.getProducto();
                int menorPrecio = 0;
                int mejorPuja = 0;
                for(Oferta ofertaProd : p.getOferta()){
                    if(mejorPuja < ofertaProd.getPrecio()){
                        mejorPuja = ofertaProd.getPrecio();
                    }
                    if(menorPrecio > ofertaProd.getPrecio()){
                        menorPrecio = ofertaProd.getPrecio();
                    }
                }
                pujas.put(oferta, mejorPuja, menorPrecio);
            }
            else{
                precios.add(oferta);
                /*Producto p = oferta.getProducto();
                int menorPrecio = 0;
                int mejorPuja = 0;
                for(Oferta ofertaProd : p.getOferta()){
                    if(mejorPuja < ofertaProd.getPrecio()){
                        mejorPuja = ofertaProd.getPrecio();
                    }
                    if(menorPrecio > ofertaProd.getPrecio()){
                        menorPrecio = ofertaProd.getPrecio();
                    }
                }
                precios.put(oferta, mejorPuja, menorPrecio);
            }
        }
        /*List<int> listMejorPuja = new ArrayList<>();
        for(Oferta oferta : pujas){
            int mejorPuja = 0;
            Producto p = oferta.getProducto();
            for(Oferta ofertaProd : p.getOferta()){
                if(mejorPuja < ofertaProd.getPrecio()){
                    mejorPuja = ofertaProd.getPrecio();
                }
            }
            listMejorPuja.add(mejorPuja);
        }

        List<int> listMenorPrecio = new ArrayList<>();
        for(Oferta oferta : precios){
            int menorPrecio = 0;
            Producto p = oferta.getProducto();
            for(Oferta ofertaProd : p.getOferta()){
                if(menorPrecio > ofertaProd.getPrecio()){
                    menorPrecio = ofertaProd.getPrecio();
                }
            }
            listMenorPrecio.add(menorPrecio);
        }
        

        
        model.addAttribute("user", user); 

        model.addAttribute("tVentas", tVentas); 
        model.addAttribute("tCompras", tCompras); 

        model.addAttribute("pujas", pujas); 
        //model.addAttribute("listMejorPuja", listMejorPuja);
        //model.addAttribute("array", array);
        model.addAttribute("precios", precios); 
        //model.addAttribute("listMenorPrecio", listMenorPrecio);
            
        return "perfil";
    }*/

    @GetMapping("/adminUsuarios") 
    public String adminUsuarios(Model model) {    
            
        List<?> users = entityManager.createQuery("SELECT u FROM Usuario u").getResultList();

        model.addAttribute("users", users); 
            
        return "adminUsuarios";                     
    }

    @GetMapping("/administrarUsuario/{id}") 
    public String administrarUsuario(@PathVariable long id, Model model) {    
            
        Usuario user = entityManager.find(Usuario.class, id); 

        model.addAttribute("user", user); 
            
        return "administrarUsuario";                     
    }

    @GetMapping("/adminProductos") 
    public String adminProductos(Model model) {    
            
        List<?> prods = entityManager.createQuery("SELECT p FROM Producto p").getResultList();
        
        model.addAttribute("prods", prods); 
            
        return "adminProductos";                     
    }

    @GetMapping("/chat") //HABLAR CON EL PROFE SOBRE ESTO
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

    @GetMapping("/confirmacionProducto")
    public String confirmacionProducto(@RequestParam long id, Model model){
        
        Producto prod = entityManager.find(Producto.class, id);
        List<Transaccion> t = prod.getTransaccion();
        
        BigDecimal precioCompra = t.get(0).getOferta().getPrecio();

        model.addAttribute("precioCompra", precioCompra);
        model.addAttribute("prod", prod); 

        return "confirmacionProducto";
    }

    @GetMapping("/pujar/{id}") 
    public String pujar(@PathVariable long id, Model model) {    
        Producto prod = entityManager.find(Producto.class, id);
        List<Oferta> ofertas = prod.getOferta();
        BigDecimal mejorPuja = new BigDecimal("0");
        BigDecimal menorPrecio = ofertas.get(0).getPrecio();

        for(Oferta oferta : ofertas){
            if(oferta.getTipo() == Oferta.Tipo.PUJA){
                if(oferta.getPrecio().compareTo(mejorPuja) == 1){
                    mejorPuja = oferta.getPrecio();
                }
            }
            else{
                if(oferta.getPrecio().compareTo(menorPrecio) == -1){
                    menorPrecio = oferta.getPrecio();
                }
            }
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

        List<Oferta> ofertas = prod.getOferta();
        BigDecimal mejorPuja = new BigDecimal("0");
        BigDecimal menorPrecio = ofertas.get(0).getPrecio();

        for(Oferta oferta : ofertas){
            if(oferta.getTipo() == Oferta.Tipo.PUJA){
                if(oferta.getPrecio().compareTo(mejorPuja) == 1){
                    mejorPuja = oferta.getPrecio();
                }
            }
            else{
                if(oferta.getPrecio().compareTo(menorPrecio) == -1){
                    menorPrecio = oferta.getPrecio();
                }
            }
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
        prec.setTipo(Oferta.Tipo.PUJA);
        entityManager.persist(prec);

        List<Oferta> ofertas = prod.getOferta();
        BigDecimal mejorPuja = new BigDecimal("0");
        BigDecimal menorPrecio = ofertas.get(0).getPrecio();

        for(Oferta oferta : ofertas){
            if(oferta.getTipo() == Oferta.Tipo.PUJA){
                if(oferta.getPrecio().compareTo(mejorPuja) == 1){
                    mejorPuja = oferta.getPrecio();
                }
            }
            else{
                if(oferta.getPrecio().compareTo(menorPrecio) == -1){
                    menorPrecio = oferta.getPrecio();
                }
            }
        }
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);  
        
        return "pujar";
    }

    @GetMapping("/fijarPrecio/{id}") 
    public String fijarPrecio(@PathVariable long id, Model model) {    
        Producto prod = entityManager.find(Producto.class, id);
        List<Oferta> ofertas = prod.getOferta();
        BigDecimal mejorPuja = new BigDecimal("0");
        BigDecimal menorPrecio = ofertas.get(0).getPrecio();

        for(Oferta oferta : ofertas){
            if(oferta.getTipo() == Oferta.Tipo.PUJA){
                if(oferta.getPrecio().compareTo(mejorPuja) == 1){
                    mejorPuja = oferta.getPrecio();
                }
            }
            else{
                if(oferta.getPrecio().compareTo(menorPrecio) == -1){
                    menorPrecio = oferta.getPrecio();
                }
            }
        }
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);    
        return "fijarPrecio";                     
    }

    @GetMapping("/listaPujas/{id}") 
    public String listaPujas(@PathVariable long id, Model model) {    
            
        Producto prod = entityManager.find(Producto.class, id);
        
        List<Oferta> ofertas = prod.getOferta();
        List<Oferta> pujas = new ArrayList<>();
        List<Oferta> precios = new ArrayList<>();
        
        for(Oferta oferta : ofertas){
            if(oferta.getTipo() == Oferta.Tipo.PUJA){
                pujas.add(oferta); 
            }
            else{
                precios.add(oferta);
            }
        }

        BigDecimal mejorPuja = new BigDecimal(0);
        BigDecimal menorPrecio = ofertas.get(0).getPrecio();

        for(Oferta oferta : ofertas){
            if(oferta.getTipo() == Oferta.Tipo.PUJA){
                if(oferta.getPrecio().compareTo(mejorPuja) == 1){
                    mejorPuja = oferta.getPrecio();
                }
            }
            else{
                if(oferta.getPrecio().compareTo(menorPrecio) == -1){
                    menorPrecio = oferta.getPrecio();
                }
            }
        }
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);
        model.addAttribute("pujas", pujas); 
            
        return "listaPujas";                     
    }

    @GetMapping("/listaPrecios/{id}") 
    public String listaPrecios(@PathVariable long id, Model model) {    
            
        Producto prod = entityManager.find(Producto.class, id);
        
        List<Oferta> ofertas = prod.getOferta();
        List<Oferta> pujas = new ArrayList<>();
        List<Oferta> precios = new ArrayList<>();
        
        for(Oferta oferta : ofertas){
            if(oferta.getTipo() == Oferta.Tipo.PUJA){
                pujas.add(oferta); 
            }
            else{
                precios.add(oferta);
            }
        }

        BigDecimal mejorPuja = new BigDecimal(0);
        BigDecimal menorPrecio = ofertas.get(0).getPrecio();

        for(Oferta oferta : ofertas){
            if(oferta.getTipo() == Oferta.Tipo.PUJA){
                if(oferta.getPrecio().compareTo(mejorPuja) == 1){
                    mejorPuja = oferta.getPrecio();
                }
            }
            else{
                if(oferta.getPrecio().compareTo(menorPrecio) == -1){
                    menorPrecio = oferta.getPrecio();
                }
            }
        }
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);
        model.addAttribute("precios", precios); 
            
        return "listaPrecios";                     
    }

    

}  
