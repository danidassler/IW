Feature: login, acceso a perfil, acceso a tienda, compra del primer producto, comprobacion de pedido en el perfil y logout

Background:
  # para escribir tus propias pruebas, lee https://github.com/intuit/karate/tree/master/karate-core
  # driver: chromium bajo linux; si usas google-chrome, puedes quitar executable (que es lo que usaría por defecto)
  * configure driver = { type: 'chrome', showDriverLog: true }
    
Scenario: login & buy & confirm

  #IMPORTANTE: no repetir la ejecucion de los features sin volver a ejecutar la app.
  #estan hechos para que funcionen con los datos iniciales, ya que por ejemplo, un usuario no puede pujar 2 veces por el mismo producto y te redirige a diferentes paginas.

  # Hacemos login en la aplicacion con el usuario "danidassler" y nos redirecciona al perfil del usuario
  * call read('login-danidassler.feature')

  # Accedemos a la tienda
  * click("a[class=tienda]")   
  * match html('title') contains 'NewChance Shop'
  * driver.screenshot()

  # Accedemos al primer producto de la tienda --> Adidas Yeezy 700
  * click("a[class=productoTienda]")
  * match html('title') contains 'Producto'
  * driver.screenshot()

  # Pulsamos el botón de "Comprar Ya" y comprobamos que nos redirige a la pagina para finalizar la compra.
  * click("a[class=productoComprarYa]")
  * match html('title') contains 'Finalizar Compra'
  * driver.screenshot()

  # Pulsamos el botón de "Confirmar Compra" para comprar el producto aceptando el precio mas bajo y nos redirige a la pagina que muestra los detalles del pedido.
  * click("button[id=confirmarCompra]")
  * match html('title') contains 'Confirmacion'
  * driver.screenshot()

  # Accedemos a nuestro perfil para comprobar que nuestro saldo ha disminuido y la compra aparece en la tabla de "Compras Realizadas", donde podemos ver el estado del pedido.
  * click("a[class=perfilUser]")
  * match html('title') contains 'Perfil'
  * driver.screenshot()
  
  # Hacemos logout despues de realizar la compra.
  * click("button[class=logout]")
  * match html('title') contains 'Login'

  