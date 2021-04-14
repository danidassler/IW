Feature: Entrar en la tienda y pujar en el primer producto

Background:
  # para escribir tus propias pruebas, lee https://github.com/intuit/karate/tree/master/karate-core
  # driver: chromium bajo linux; si usas google-chrome, puedes quitar executable (que es lo que usaría por defecto)
  * configure driver = { type: 'chrome', showDriverLog: true }
    
Scenario: login & puja

  Given driver 'http://localhost:8080/login'
  * input('#username', 'sergiom23')
  * input('#password', 'aa')
  * submit().click("button[type=submit]")
  * match html('title') contains 'Perfil'
  * driver.screenshot()
  
  * click("a[class=tienda]")
  * match html('title') contains 'NewChance Shop'
  * driver.screenshot()

  * click("a[class=productoTienda]")
  * match html('title') contains 'Producto'
  * driver.screenshot()
  
  * click("a[class=pujarProducto]")
  * match html('title') contains 'Pujar un producto'

  * input('#precio', '300')

  * click("button[id=pujaProducto]")
  * match html('title') contains 'Pujar un producto'

  * click("button[class=logout]")
  * match html('title') contains 'Login'