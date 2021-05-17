Feature: login, acceso a tienda, pujar en el primer producto, comprobación en el perfil y logout.

Background:
  # para escribir tus propias pruebas, lee https://github.com/intuit/karate/tree/master/karate-core
  # driver: chromium bajo linux; si usas google-chrome, puedes quitar executable (que es lo que usaría por defecto)
  * configure driver = { type: 'chrome', showDriverLog: true }
    


Scenario: login & puja & confirm

  #IMPORTANTE: no repetir la ejecucion de los features sin volver a ejecutar la app.
  #estan hechos para que funcionen con los datos iniciales, ya que por ejemplo, un usuario no puede pujar 2 veces por el mismo producto y te redirige a diferentes paginas.

  # Hacemos login en la aplicacion con el usuario "sergiom23" y nos redirecciona al perfil del usuario
  * call read('login-sergiom23.feature')

  # Accedemos a la tienda
  * click("a[class=tienda]")
  * match html('title') contains 'NewChance Shop'
  * driver.screenshot()

  # Accedemos al primer producto de la tienda --> Adidas Yeezy 700
  * click("a[class=productoTienda]")
  * match html('title') contains 'Producto'
  * driver.screenshot()
  
  # Pulsamos el botón de "Pujar" y comprobamos que nos redirige a la pagina de puja.
  * click("a[class=pujarProducto]")
  * match html('title') contains 'Pujar un producto'
  * driver.screenshot()

  # Incluimos la cantidad de dinero a pujar, el campo de "Tiempo de expiración" lo dejamos por defecto a 1 dia
  * input('#precio', '480')

  # Pulsamos el boton para realizar la puja, comprobamos que nos ha redirigido a la misma página.
  # Si nuestra puja es la mejor, aparecerá en la informacion de la parte izquierda de la pagina.
  * click("button[id=pujaProducto]")
  * match html('title') contains 'Pujar un producto'
  * driver.screenshot()

  # Accedemos a nuestro perfil para comprobar que nuestro saldo ha disminuido y la puja aparece en la tabla de "Pujas Activas".
  * click("a[class=perfilUser]")
  * match html('title') contains 'Perfil'
  * driver.screenshot()

  # Hacemos logout despues de realizar la puja.
  * click("button[class=logout]")
  * match html('title') contains 'Login'