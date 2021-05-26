Feature: se restauran los valores
    
Scenario: añadir dinero

  # Hacemos login en la aplicacion con el usuario "danidassler" y nos redirecciona al perfil del usuario
  * call read('logins.feature@dani')
  * click("a[class~=depositarFondo]")
  * input('#nombre', 'Dani')
  * input('#correo', 'hola@a.com')  
  * input('#direccion', 'aqui')  
  * input('#tarjetaDebito', '5555555')  
  * input('#cvv', '222')  
  * input('#saldo', '1000')  
  * click("button[id=depositarFondo]")

  # Accedemos a nuestro perfil para comprobar el saldo
  * click("a[class~=perfilUser]")
  * match html('title') contains 'Perfil'
  * driver.screenshot()



