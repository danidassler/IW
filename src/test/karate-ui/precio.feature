Feature: login, acceso a tienda, pujar en el primer producto, comprobación en el perfil y logout.

Scenario: login, precio & confirm

  #IMPORTANTE: no repetir la ejecucion de los features sin volver a ejecutar la app.
  #estan hechos para que funcionen con los datos iniciales, ya que por ejemplo, un usuario no puede pujar 2 veces por el mismo producto y te redirige a diferentes paginas.

  # Hacemos login en la aplicacion con el usuario "alvaro09" y nos redirecciona al perfil del usuario
  * call read('logins.feature@alvaro')

  # Accedemos a la tienda
  * click("a[class=tienda]")
  * match html('title') contains 'NewChance Shop'
  * driver.screenshot()

  # Accedemos al primer producto de la tienda --> Adidas Yeezy 700
  * click("a[class~=productoTienda]")
  * match html('title') contains 'Producto'
  * driver.screenshot()
  
  # Pulsamos el botón de "Fijar precio" y comprobamos que nos redirige a la pagina de fijar un precio.
  * click("a[class~=precioProducto]")
  * match html('title') contains 'Fijar precio del producto'
  * driver.screenshot()

  # Incluimos la cantidad de dinero, el campo de "Tiempo de expiración" lo dejamos por defecto a 1 dia
  * input('#precio', '480')

  # Pulsamos el boton para realizar la puja, comprobamos que nos ha redirigido a la misma página.
  # Si nuestra precio es el mas bajo, aparecerá en la informacion de la parte izquierda de la pagina.
  * click("button[id=precioProducto]")
  * match html('title') contains 'Fijar precio del producto'
  * driver.screenshot()

  # Accedemos a nuestro perfil para comprobar que el precio aparece en la tabla de "Productos que estas vendiendo".
  * click("a[class~=perfilUser]")
  * match html('title') contains 'Perfil'
  * driver.screenshot()
