# Chat-App
Projekat iz predmeta Agentske tehnologije

Aplikacija radi na wildfly 11.0 serveru i Javi 1.8.
Posto je u standalone-full-ha public interface izmijenjen tako da cuva ip adresu mog racunara (169.254.209.90), tako se ne konektujem na localhost:8080/WAR2020 nego 169.254.209.90:8080/WAR2020.
Takodje, ws je podesen na 169.254.209.90:8080 umjesto na localhost:8080 kako je ranije bilo (u main.js fajlu u web content folderu WAR projekta).
Informacije o usernameu i passwordu za svakog korisnika su u "baza.txt", koji se cuva i ucitava sa desktopa, ali nalijepicu ih i ovdje. Mozete se ulogovati kao admin, ili user (kojih ima vise).
Kao user mozete slati poruku na grupni chat (svima) i pojedinacnom korisniku.

Da bi ObjectMapper (za konverziju iz JSONa i obrnuto) radio, potrebno je u WAR i JAR, u build path, u libraries dodati jackson core i jackson databind 2.1.1 jarove.
Za vfs ucitavanje fajlova, sto je vidjeno u siebogu, potreban je jboss-vfs-3.1.0.Final.jar.

Informacije o korisnicima:
{"id":1,"username":"admin","password":"admin","role":"ADMIN"}
{"id":2,"username":"user","password":"user","role":"USER"}
{"id":3,"username":"proba","password":"proba","role":"USER"}
{"id":4,"username":"user1","password":"user","role":"USER"}
{"id":6,"username":"user2","password":"user","role":"USER"}
{"id":7,"username":"user3","password":"user","role":"USER"}
{"id":8,"username":"user4","password":"user","role":"USER"}
