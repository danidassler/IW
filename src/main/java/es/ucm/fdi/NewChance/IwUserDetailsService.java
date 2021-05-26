package es.ucm.fdi.NewChance;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import es.ucm.fdi.NewChance.model.Usuario;

public class IwUserDetailsService implements UserDetailsService {

	private static Logger log = LogManager.getLogger(IwUserDetailsService.class);

    private EntityManager entityManager;
    
    @PersistenceContext
    public void setEntityManager(EntityManager em){
        this.entityManager = em;
    }

    public UserDetails loadUserByUsername(String username){
    	try {
	        Usuario u = entityManager.createNamedQuery("Usuario.byUsername", Usuario.class)
                    .setParameter("username", username)
                    .getSingleResult();
	        // build UserDetails object
	        String rol = u.getRol().toString();
			ArrayList<SimpleGrantedAuthority> rolUser = new ArrayList<>();

			rolUser.add(new SimpleGrantedAuthority("ROLE_" + u.getRol()));
		    log.info("Rol for " + username + " include " + rol);
			
	        return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), rolUser);
	    } catch (Exception e) {
    		log.info("No such user: " + username + "(e = " + e.getMessage() + ")");
    		throw new UsernameNotFoundException(username);
    	}
    }
}