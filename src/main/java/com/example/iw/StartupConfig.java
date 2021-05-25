package com.example.iw;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * This code will execute when the application first starts.
 * 
 * @author mfreire
 */
@Component
public class StartupConfig {
	
	private static final Logger log = LogManager.getLogger(StartupConfig.class);
	
	@Autowired
	private Environment env;

	@Autowired
    private EntityManager entityManager;

	@Autowired
	private ServletContext context;
	
	@EventListener(ContextRefreshedEvent.class)
	public void contextRefreshedEvent() {
		String debugProperty = env.getProperty("com.example.debug");
		context.setAttribute("debug", debugProperty != null 
				&& Boolean.parseBoolean(debugProperty.toLowerCase()));
		log.info("Setting global debug property to {}", 
				context.getAttribute("debug"));

		Double impuestosProperty =  Double.parseDouble(env.getProperty("com.example.impuestos"));
		context.setAttribute("impuestos", impuestosProperty);
		log.info("Setting global impuestos property to {}", 
				context.getAttribute("impuestos"));

		Double envioProperty = Double.parseDouble(env.getProperty("com.example.envio"));
		context.setAttribute("envio", envioProperty);
		log.info("Setting global envio property to {}", 
				context.getAttribute("envio"));

		List<?> categorias = entityManager.createNamedQuery("Producto.categories").getResultList();
        context.setAttribute("categorias", categorias);

		
		// see http://www.ecma-international.org/ecma-262/5.1/#sec-15.9.1.15
		// and https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
		context.setAttribute("dateFormatter", 
				new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss.sssZ"));
	}
}