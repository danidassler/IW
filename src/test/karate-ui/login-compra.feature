Feature: login, acceso a perfil, acceso a tienda y compra del primer producto

Background:
  # para escribir tus propias pruebas, lee https://github.com/intuit/karate/tree/master/karate-core
  # driver: chromium bajo linux; si usas google-chrome, puedes quitar executable (que es lo que usaría por defecto)
  * configure driver = { type: 'chrome', showDriverLog: true }
    
Scenario: login & buy

  Given driver 'http://localhost:8080/login'
  * input('#username', 'danidassler')
  * input('#password', 'aa')
  * submit().click("button[type=submit]")
  * match html('title') contains 'Perfil'
  * driver.screenshot()
  * delay(500)

  * click("a[class=tienda]")
  * match html('title') contains 'NewChance Shop'
  * driver.screenshot()
  * click("a[class=productoTienda]")
  * match html('title') contains 'Producto'
  * driver.screenshot()
  * click("a[class=productoComprarYa]")
  * match html('title') contains 'Finalizar Compra'
  * driver.screenshot()
  * click("button[id=confirmarCompra]")
  * match html('title') contains 'Confirmacion'
  * driver.screenshot()

  * click("button[class=logout]")
  * match html('title') contains 'Login'

  