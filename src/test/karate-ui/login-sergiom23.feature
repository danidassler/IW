Feature: hacer login con el usuario "sergiom23", redireccionando al perfil

Background:
  # para escribir tus propias pruebas, lee https://github.com/intuit/karate/tree/master/karate-core
  # driver: chromium bajo linux; si usas google-chrome, puedes quitar executable (que es lo que usaría por defecto)
  * configure driver = { type: 'chrome', showDriverLog: true }
    
Scenario: login & view perfil

  Given driver 'http://localhost:8080/login'

  #Rellenamos el formulario de login con los 2 campos
  * input('#username', 'sergiom23')
  * input('#password', 'aa')

  #Pulsamos el botón y comprobamos que nos redirige al perfil
  * submit().click("button[type=submit]")  
  * match html('title') contains 'Perfil'
  * driver.screenshot()