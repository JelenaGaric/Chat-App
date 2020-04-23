# Chat-App
Projekat iz predmeta Agentske tehnologije

Odradjene su funkcionalnosti za rest servis.
Informacije o usernameu i passwordu za svakog korisnika su u "baza.txt", ali nalijepicu ih i ovdje. Mozete se ulogovati kao admin, ili user (kojih ima vise).
Kao admin dobijate (zasad) samo informacije o ulogovanim i registrovanim korisnicima.
Kao user mozete slati poruku na grupni chat (svima) i pojedinacnom korisniku.
Kada se ulogujete kao user, mozda je potreban refresh stranice da ocita sve ulogovane korisnike sa strane, ili je potrebno da se ulogujete iz drugog browsera.
Omogucena je komunikacija putem web socketa.

Da bi ObjectMapper (za konverziju iz JSONa i obrnuto) radio, potrebno je u WAR i JAR, u build path, u libraries dodati jackson core i jackson databind 2.1.1 jarove.

Informacije o korisnicima:
{"id":1,"username":"admin","password":"admin","role":"ADMIN"}
{"id":2,"username":"user","password":"user","role":"USER"}
{"id":3,"username":"proba","password":"proba","role":"USER"}
{"id":4,"username":"user1","password":"user","role":"USER"}
{"id":6,"username":"user2","password":"user","role":"USER"}
{"id":7,"username":"user3","password":"user","role":"USER"}
{"id":8,"username":"user4","password":"user","role":"USER"}
