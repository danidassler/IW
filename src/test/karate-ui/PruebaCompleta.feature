Feature: Admin agrega un producto, un usuario pone un precio al producto nuevo y otro usuario compra el producto con su precio.
    
Scenario: ADMIN: login, add product | User 1: login & place price | User 2: login, buy & confirm

  #IMPORTANTE: no repetir la ejecucion de los features sin volver a ejecutar la app.

  # ------------------------- USUARIO ADMINISTRADOR --------------------------------------------------
  # Hacemos login en la aplicacion con el usuario "TheRealJin" y nos redirecciona al administrador de usuarios
  * call read('logins.feature@admin')

  # Accedemos a la tienda
  * click("a[class=tienda]")   
  * match html('title') contains 'NewChance Shop'
  * driver.screenshot()

  # Pulsamos el botón de "Administrar Productos" Para acceder a la vista del admin
  * click("a[class~=adminProductos]")
  * match html('title') contains 'Administrador Productos'
  * driver.screenshot()

  # Pulsamos el botón de "Nuevo Producto" Para acceder al formulario de crear un producto nuevo
  * click("a[class~=newProduct]")
  * match html('title') contains 'Formulario para un producto'
  * driver.screenshot()

  #Rellenamos el formulario del nuevo producto
  * input('#nombre', 'Adidas Supreme 1000')
  * input('#categorias', 'Zapatillas')
  * input('#desc', 'Muy supreme, con un diseño vanguardista')
  * input('#talla', '44')
  * click("button[id=addProducto]")
  * click("a[class=tienda]")
  * driver.screenshot()
  * call read('logout.feature')
  
  #----------------------------- USUARIO 1 (Alvaro09) ----------------------------------------------------

  # Alvaro09 fija un precio nuevo 
  * call read('precio.feature')
  * call read('logout.feature')

  #----------------------------- USUARIO 2 (danidassler) --------------------------------------------------

  #Danidassler realiza una compra con el precio fijado por Alvaro09
  * call read('compra.feature')
