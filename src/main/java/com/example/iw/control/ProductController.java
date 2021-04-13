package com.example.iw.control;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.example.iw.model.Oferta;
import com.example.iw.model.Producto;
import com.example.iw.model.Usuario;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller()
@RequestMapping("producto")
public class ProductController {
	private static final Logger log = LogManager.getLogger(UserController.class);

	@Autowired 
	private EntityManager entityManager;
	
	@GetMapping("/{id}") 
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

    @GetMapping("/confirmacionProducto/{id}")
    public String confirmacionProductoC(@PathVariable long id, Model model){
        
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

    @PostMapping("confirmacionProducto/{id}")
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
                        int deleteCount = entityManager.createNamedQuery("Oferta.borrar").setParameter("idOferta", p.getId()).executeUpdate();
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

}