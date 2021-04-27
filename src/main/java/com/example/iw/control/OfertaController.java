package com.example.iw.control;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.example.iw.model.Oferta;
import com.example.iw.model.Producto;
import com.example.iw.model.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller()
@RequestMapping("oferta")
public class OfertaController {

    @Autowired
    private EntityManager entityManager;
    
    @GetMapping("/pujar/{id}")
    public String pujar(@PathVariable long id, Model model, HttpSession session) {    
        Producto prod = entityManager.find(Producto.class, id);

        BigDecimal mejorPuja = obtenerMejorPuja(id);
        BigDecimal menorPrecio = obtenerMenorPrecio(id);

        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);

        return "pujar";                     
    }

    @PostMapping("pujar/{id}")
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

        BigDecimal mejorPuja = obtenerMejorPuja(id);
        BigDecimal menorPrecio = obtenerMenorPrecio(id);

        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);
        
        return "pujar";
    }


    @GetMapping("/eliminarOferta/{id}")
    public String eliminarPuja(Model model, HttpSession session){

        return "eliminarOferta";
    }

    @PostMapping("/eliminarOferta/{id}") //el id recibido en la ruta es el de la oferta
    @Transactional
    public String eliminarOferta(
        @PathVariable long id,
        @RequestParam BigDecimal precio,
        Model model, HttpSession session){
        
        Usuario u = entityManager.find(Usuario.class, ((Usuario)session.getAttribute("u")).getId());
        Oferta oferta = entityManager.find(Oferta.class, id);
        Oferta.Tipo tipo = oferta.getTipo();

        if(tipo == Oferta.Tipo.PUJA){ //si es una puja devolvemos el dinero al usuario 
            BigDecimal aniadir = precio.add(precio.multiply(new BigDecimal("9")).divide(new BigDecimal("100"))).add(new BigDecimal("10"));
            BigDecimal nuevoSaldo = u.getSaldo().add(aniadir);
            u.setSaldo(nuevoSaldo);
            entityManager.merge(u);
            entityManager.remove(oferta);
        }
        else{ // si es un precio solo hace falta borrar la oferta de la bbdd
            entityManager.remove(oferta);
        }
        
        model.addAttribute("idUser", u.getId());

        return "eliminarOferta";    
    }


    @GetMapping("/errorPuja")
    @Transactional
    public String errorPuja(Model model, HttpSession session){

        return "errorPuja";
    }
    
    @PostMapping("fijarPrecio/{id}")
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
        prec.setVendedor(u);
        prec.setEstado(Oferta.Estado.PENDIENTE);
        prec.setTipo(Oferta.Tipo.PRECIO);
        entityManager.persist(prec);

        BigDecimal mejorPuja = obtenerMejorPuja(id);
        BigDecimal menorPrecio = obtenerMenorPrecio(id);

        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);  
        
        return "fijarPrecio";
    }

    @GetMapping("/fijarPrecio/{id}")
    @Transactional 
    public String fijarPrecio(@PathVariable long id, Model model) {    
        Producto prod = entityManager.find(Producto.class, id);

        BigDecimal mejorPuja = obtenerMejorPuja(id);
        BigDecimal menorPrecio = obtenerMenorPrecio(id);

        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);    
        return "fijarPrecio";                     
    }

    @GetMapping("/listaPujas/{id}")
    @Transactional 
    public String listaPujas(@PathVariable long id, Model model) {  
        
        Producto prod = entityManager.find(Producto.class, id);
        
        List<Oferta> pujas = entityManager.createNamedQuery("Oferta.pujas").setParameter("productoId", id).getResultList();

        BigDecimal mejorPuja = obtenerMejorPuja(id);
        BigDecimal menorPrecio = obtenerMenorPrecio(id);

        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);
        model.addAttribute("pujas", pujas); 
            
        return "listaPujas";                     
    }

    @GetMapping("/listaPrecios/{id}")
    @Transactional 
    public String listaPrecios(@PathVariable long id, Model model) {    

        Producto prod = entityManager.find(Producto.class, id);
        List<Oferta> precios = entityManager.createNamedQuery("Oferta.precios").setParameter("productoId", id).getResultList();

        BigDecimal mejorPuja = obtenerMejorPuja(id);
        BigDecimal menorPrecio = obtenerMenorPrecio(id);

        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);
        model.addAttribute("precios", precios); 
            
        return "listaPrecios";                     
    }

    @GetMapping("/listaVentas/{id}")
    @Transactional
    public String listaVentas(@PathVariable long id, Model model) {  
        
        Producto prod = entityManager.find(Producto.class, id);
        
        List<Oferta> ventas = entityManager.createNamedQuery("Oferta.transaction").setParameter("productoId", id).getResultList();

        BigDecimal mejorPuja = obtenerMejorPuja(id);
        BigDecimal menorPrecio = obtenerMenorPrecio(id);
        
        model.addAttribute("prod", prod); 
        model.addAttribute("mejorPuja", mejorPuja);
        model.addAttribute("menorPrecio", menorPrecio);
        model.addAttribute("ventas", ventas); 
            
        return "listaVentas";                     
    }


    //funciones para obtener los precios mas bajos y pujas mas altas de los productos
    public BigDecimal obtenerMejorPuja(long id){
        BigDecimal mejorPuja = (BigDecimal)entityManager.createNamedQuery("Oferta.mejorPuja").setParameter("productoId", id).getSingleResult();
        
        if(mejorPuja == null){
            mejorPuja = new BigDecimal("0");
        }
        
        return mejorPuja;
    }

    public BigDecimal obtenerMenorPrecio(long id){
        BigDecimal menorPrecio = (BigDecimal)entityManager.createNamedQuery("Oferta.menorPrecio").setParameter("productoId", id).getSingleResult();
               
        if(menorPrecio == null){
            menorPrecio = new BigDecimal("0");
        }

        return menorPrecio;
    }

    
}