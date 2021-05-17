Feature: login, acceso a tienda, pujar en el primer producto, comprobación en el perfil y logout.

Background:
  # para escribir tus propias pruebas, lee https://github.com/intuit/karate/tree/master/karate-core
  # driver: chromium bajo linux; si usas google-chrome, puedes quitar executable (que es lo que usaría por defecto)
  * configure driver = { type: 'chrome', showDriverLog: true }
    


Scenario: login, precio & confirm

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
  * click("a[class=precioProducto]")
  * match html('title') contains 'Fijar precio del producto'
  * driver.screenshot()

  # Incluimos la cantidad de dinero, el campo de "Tiempo de expiración" lo dejamos por defecto a 1 dia
  * input('#precio', '300')

  # Pulsamos el boton para realizar la puja, comprobamos que nos ha redirigido a la misma página.
  # Si nuestra puja es la mejor, aparecerá en la informacion de la parte izquierda de la pagina.
  * click("button[id=precioProducto]")
  * match html('title') contains 'Fijar precio del producto'
  * driver.screenshot()

  # Hacemos logout despues de fijar el precio.
  * click("button[class=logout]")
  * match html('title') contains 'Login'