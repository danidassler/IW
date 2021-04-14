Feature: csrf and sign-in end point

Background:
    * url 'http://localhost:8080'
    * def util = Java.type('karate.KarateTests')

Scenario: get login page, capture csrf, send login with User who is not admin
    * path 'login'
    * method get
    * status 200
    * print response
    * print responseCookies
    # ... name="_csrf" value="0a7c65e8-4e8e-452f-ad44-40b995bb91d6" => 0a7c65e8-4e8e-452f-ad44-40b995bb91d6"
    * def csrf = karate.extract(response, '"_csrf" value="([^"]*)"', 1) 
    * print csrf

    * path 'login'
    * form field username = 'danidassler'
    * form field password = 'aa'
    * form field _csrf = csrf
    * method post    
    * status 200
    * print response
    * print responseCookies
    * def h3s = util.selectHtml(response, "h3");  
    * print h3s
    * match h3s contains 'Perfil'