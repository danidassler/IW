Feature: login como distintos usuarios

@admin
Scenario: admin

  * driver 'http://localhost:8080/login'
  * waitForUrl("/login")
  * input('#username', 'TheRealJin', 100)
  * input('#password', 'aa', 100)  
  * click("button[id=login]")
  * waitForUrl("/admin/adminUsuarios/")

@alvaro
Scenario: no-admin

  * driver 'http://localhost:8080/login'
  * waitForUrl("/login")
  * input('#username', 'alvaro09', 100)
  * input('#password', 'bb', 100)
  * click("button[id=login]")
  * waitForUrl("/perfil/3")

@dani
Scenario: danidassler

  * driver 'http://localhost:8080/login'
  * waitForUrl("/login")
  * input('#username', 'danidassler')
  * input('#password', 'aa')
  * click("button[id=login]")
  * waitForUrl("/perfil/1")

@sergio
Scenario: sergio

  * driver 'http://localhost:8080/login'
  * waitForUrl("/login")
  * input('#username', 'sergiom23')
  * input('#password', 'aa')
  * click("button[id=login]")
  * waitForUrl("/perfil/2")
