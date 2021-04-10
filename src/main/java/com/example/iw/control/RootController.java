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
        
        //oreguntar profe como conseguir mostrar los precios en la tienda

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
        BigDecimal ultimaVenta = new BigDecimal("0");
        BigDecimal menorPrecio = new BigDecimal("0");
        BigDecimal mejorPuja = new BigDecimal("0");

        List<Oferta> trans = entityManager.createNamedQuery("Oferta.transaction").setParameter("productoId", id).getResultList();
        if(trans.size() > 0){
            ultimaVenta = trans.get(0).getPrecio();
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
        long idOferta = -1L;
        
        List<Oferta> mejPuja = entityManager.createNamedQuery("Oferta.mejorPuja").setParameter("productoId", id).getResultList();
        List<Oferta> minPrecio = entityManager.createNamedQuery("Oferta.menorPrecio").setParameter("productoId", id).getResultList(); 

        if((minPrecio.size() > 0)){
            menorPrecio = minPrecio.get(0).getPrecio();
        }
        
        if((mejPuja.size() > 0)){
            mejorPuja = mejPuja.get(0).getPrecio();
            idOferta = mejPuja.get(0).getId();
        }

        BigDecimal precio = mejorPuja;
        BigDecimal impuestos = new BigDecimal("0");
        BigDecimal precioFinal = mejorPuja;
        BigDecimal envio = new BigDecimal("10");
        if(!mejorPuja.equals(0)){ //obtenemos el 9% de "mejorPuja" ya que son las tasas por usar la web
            impuestos = mejorPuja.multiply(new BigDecimal("9")).divide(new BigDecimal("100"));
        }

        precioFinal = precioFinal.subtract(impuestos).subtract(envio);
        
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);
        model.addAttribute("precioFinal", precioFinal);
        model.addAttribute("envio", envio);
        model.addAttribute("impuestos", impuestos);
        model.addAttribute("precio", precio);
        model.addAttribute("idOferta", idOferta); 

        return "venta";                     
    }

    //HACER EL POST de compra y venta, donde restamos y sumamos precios al saldo de usuarios, creamos transaccion y eliminamos oferta

    @GetMapping("/compra/{id}")
    @Transactional 
    public String compra(@PathVariable long id, Model model) {    
        Producto prod = entityManager.find(Producto.class, id);
        BigDecimal menorPrecio = new BigDecimal("0");
        BigDecimal mejorPuja = new BigDecimal("0");
        long idOferta = -1L;

        List<Oferta> mejPuja = entityManager.createNamedQuery("Oferta.mejorPuja").setParameter("productoId", id).getResultList();
        List<Oferta> minPrecio = entityManager.createNamedQuery("Oferta.menorPrecio").setParameter("productoId", id).getResultList(); 

        if((minPrecio.size() > 0)){
            menorPrecio = minPrecio.get(0).getPrecio();
            idOferta = minPrecio.get(0).getId();
        }
        
        if((mejPuja.size() > 0)){
            mejorPuja = mejPuja.get(0).getPrecio();
        }

        BigDecimal precio = menorPrecio;
        BigDecimal impuestos = new BigDecimal("0");
        BigDecimal precioFinal = menorPrecio;
        BigDecimal envio = new BigDecimal("10");
        if(!menorPrecio.equals(0)){ //obtenemos el 9% de "menorPrecio" ya que son los impuestos
            impuestos = menorPrecio.multiply(new BigDecimal("9")).divide(new BigDecimal("100"));
        }

        precioFinal = precioFinal.add(impuestos).add(envio);
        
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);
        model.addAttribute("precioFinal", precioFinal);
        model.addAttribute("envio", envio);
        model.addAttribute("impuestos", impuestos);
        model.addAttribute("precio", precio);
        model.addAttribute("idOferta", idOferta);

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
        Usuario prof = (Usuario)session.getAttribute("u");
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
        Usuario prof = (Usuario)session.getAttribute("u");
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
        List<Oferta> t = new ArrayList<>();
        t = entityManager.createNamedQuery("Oferta.transaction").setParameter("productoId", id).getResultList();
        if(t != null)
            precioCompra = t.get(0).getPrecio();

        model.addAttribute("precioCompra", precioCompra);
        model.addAttribute("prod", prod); 

        return "confirmacionProducto";
    }

    @PostMapping("/confirmacionProducto/{id}")
    @Transactional
    public String confirmacionProducto(
        @PathVariable long id,
        @RequestParam BigDecimal precio,
        @RequestParam BigDecimal precioFinal,
        @RequestParam BigDecimal envio,
        @RequestParam BigDecimal impuestos,
        @RequestParam long idOferta,
        Model model,
        HttpSession session){

        Oferta oferta = entityManager.find(Oferta.class, idOferta);
        Producto prod = entityManager.find(Producto.class, id);
        Usuario user = (Usuario)session.getAttribute("u");
        int dif = 0; //Para diferenciar si es una compra o venta, siendo venta = 0 y compra = 1
        
        Oferta.Tipo tipo = oferta.getTipo();
        if(tipo == Oferta.Tipo.PUJA){ //aqui se entra cuando el vendedor acepta una puja

            Usuario comp = oferta.getComprador();
            oferta.setEstado(Oferta.Estado.TRANSACCION_REALIZADA);
            oferta.setFechaTransaccion(LocalDateTime.now());
            user.setSaldo(user.getSaldo().add(precioFinal));
            //comp.setSaldo(precio.add(envio).add(impuestos)); --> no hace falta ya que el dinero se le resta cuando hace la puja
            oferta.setVendedor(user);
            entityManager.merge(comp);

        }
        else{ //aqui se entra cuando el comprador acepta un precio
            
            int errorCompra = 0;
            int puedeS = 1;
            BigDecimal nuevoSaldo = user.getSaldo().subtract(precioFinal);
            if(nuevoSaldo.compareTo(new BigDecimal("0")) == -1){ //  nuevoSaldo < 0 --> no tiene dinero suficiente, no puede comprar ya
                errorCompra = 1;
                puedeS = 0;
                model.addAttribute("errorCompra", errorCompra);
                model.addAttribute("puedeS", puedeS);
                return "errorPuja";
            }
            else{
                List<Oferta> pujasUser = entityManager.createNamedQuery("Oferta.pujasUser").setParameter("userId", user.getId()).getResultList(); //cogemos la lista de pujas actuales del usuario
                for(Oferta p: pujasUser){ //si el usuario que compra tiene una puja activa por el producto que está comprando, esta puja se elimmina al haber comprado ya el producto por un precio asignado por un vendedor
                    if(p.getProducto().getId() == id){
                        // AQUI FALTA EJECUTAR LA QUERY QUE ELIMINA UNA OFERTA
                    }
                }
                
                user.setSaldo(nuevoSaldo);  
                oferta.setEstado(Oferta.Estado.TRANSACCION_REALIZADA);
                oferta.setFechaTransaccion(LocalDateTime.now());
                Usuario vend = oferta.getVendedor();
                vend.setSaldo(vend.getSaldo().add(precio.subtract(envio).subtract(impuestos)));
                oferta.setComprador(user);
                entityManager.merge(vend);
                dif = 1;
            }
        }

        entityManager.merge(user);
        entityManager.merge(oferta);

        long nPedido = (long)(Math.random()*9999999 + 1);

        model.addAttribute("prod", prod);
        model.addAttribute("precio", precio);
        model.addAttribute("dif", dif);
        model.addAttribute("precioFinal", precioFinal);
        model.addAttribute("impuestos", impuestos);
        model.addAttribute("envio", envio);
        model.addAttribute("nPedido", nPedido);
        model.addAttribute("dif", dif);

        return "confirmacionProducto";
    }

    @GetMapping("/pujar/{id}")
    @Transactional 
    public String pujar(@PathVariable long id, Model model, HttpSession session) {    
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
        Usuario u = (Usuario)session.getAttribute("u");

        //CONTROLAMOS QUE TIENE SALDO DISPONIBLE Y QUE NO PUEDA PUJAR DOS VECES POR EL MISMO PRODUCTO
        int puedeP = 1; //entero para controlar si el usuario ya tiene una puja activa por el producto por el cual quiere pujar
        int puedeS = 1;
        int errorCompra = 0;
        BigDecimal saldo = u.getSaldo(); // --> cogemos el saldo actual del usuario
        List<Oferta> pujasUser = entityManager.createNamedQuery("Oferta.pujasUser").setParameter("userId", u.getId()).getResultList(); //cogemos la lista de pujas actuales del usuario

        for(Oferta p: pujasUser){ //miramos si el usuario ya tiene una puja activa para el producto 
            if(p.getProducto().getId() == id){
                puedeP = 0; 
            }
        }

        BigDecimal saldoSiPuja = saldo.subtract(precio.add(precio.multiply(new BigDecimal("9")).divide(new BigDecimal("100"))).add(new BigDecimal("10"))); //calculamos el saldo del usuario si este realizase la puja que está intentando realizar

        if(saldoSiPuja.compareTo(new BigDecimal("0")) == -1 || puedeP == 0){ //saldoSiPuja < 0 --> la puja NO puede realizarse
            
            model.addAttribute("puedeP", puedeP);
            if(saldoSiPuja.compareTo(new BigDecimal("0")) == -1){ puedeS = 0;}
            model.addAttribute("puedeS", puedeS);
            model.addAttribute("idUser", u.getId());
            model.addAttribute("errorCompra", errorCompra);

            return "errorPuja";

        }
        else{ //saldoSiPuja >= 0 --> la puja puede realizarse
            puja.setProducto(prod);
            puja.setPrecio(precio);
            puja.setFechaInicio(localDateTime);
            puja.setFechaExpiracion(localDateTime.plusDays(tiempoExpiracion));
            puja.setComprador(u);
            puja.setTipo(Oferta.Tipo.PUJA);
            puja.setEstado(Oferta.Estado.PENDIENTE);
            entityManager.persist(puja);

            u.setSaldo(saldoSiPuja);
            entityManager.merge(u);
        }

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


    @GetMapping("/eliminarOferta/{id}")
    @Transactional
    public String eliminarPuja(Model model, HttpSession session){

        return "eliminarOferta";
    }

    @PostMapping("/eliminarOferta/{id}") //el id recibido en la ruta es el de la oferta
    @Transactional
    public String eliminarOferta(
        @PathVariable long id,
        @RequestParam BigDecimal precio,
        Model model, HttpSession session){
        
        Usuario u = (Usuario)session.getAttribute("u");
        Oferta oferta = entityManager.find(Oferta.class, id);
        Oferta.Tipo tipo = oferta.getTipo();

        if(tipo == Oferta.Tipo.PUJA){ //si es una puja devolvemos el dinero al usuario 
            BigDecimal aniadir = precio.add(precio.multiply(new BigDecimal("9")).divide(new BigDecimal("100"))).add(new BigDecimal("10"));
            BigDecimal nuevoSaldo = u.getSaldo().add(aniadir);
            u.setSaldo(nuevoSaldo);
            entityManager.merge(u);
            //AQUI FALTA EJECUTAR LA QUERY PARA ELIMINAR UNA OFERTA DE LA BBDD
        }
        else{ // si es un precio solo hace falta borrar la oferta de la bbdd
            //AQUI FALTA EJECUTAR LA QUERY PARA ELIMINAR UNA OFERTA DE LA BBDD
        }
        
        model.addAttribute("idUser", u.getId());

        return "eliminarOferta";    
    }


    @GetMapping("/errorPuja")
    @Transactional
    public String errorPuja(Model model, HttpSession session){

        return "errorPuja";
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
        Usuario u = (Usuario)session.getAttribute("u");

        prec.setProducto(prod);
        prec.setPrecio(precio);
        prec.setFechaInicio(localDateTime);
        prec.setFechaExpiracion(localDateTime.plusDays(tiempoExpiracion));
        prec.setVendedor(u);
        prec.setEstado(Oferta.Estado.PENDIENTE);
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

    @GetMapping("/listaVentas/{id}")
    @Transactional
    public String listaVentas(@PathVariable long id, Model model) {  
        
        BigDecimal menorPrecio = new BigDecimal("0");
        BigDecimal mejorPuja = new BigDecimal("0");
            
        Producto prod = entityManager.find(Producto.class, id);
        
        List<Oferta> ventas = entityManager.createNamedQuery("Oferta.transaction").setParameter("productoId", id).getResultList();

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
        model.addAttribute("ventas", ventas); 
            
        return "listaVentas";                     
    }
}  
