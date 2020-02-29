## Kodutöö 1
Anonümiseeriv võrk Javas.

Emil Fenenko & Karl Tamberg

### Endpoints
* GET /start-download

Endpoint testimiseks. Selle kaudu antakse serverile märku, et tema peaks alustama faili allalaadimist ja sinna 
antakse kaasa URL, mis alla laadida. 

* GET /download

Endpoint, kuhu saadetakse allalaadimise käsk koos faili id'ga ja URL'iga. Randomiga otsustab server, kas 
laeb faili alla (ja saadab saadud faili tagasi samale aadressile, kust fail tuli) või saadab allalaadimise 
requesti edasi oma naabritele.

* POST /file

Endpoint, kuhu saadetakse fail koos vastusega. Kui see server allalaadimise päringut alustas, siis logitakse 
fail base64 kujul maha. Kui see server sai allalaadimise requesti läbi /download endpointi, siis saadab ta
saadud faili tagasi sellele aadressile, kust tuli talle /download request.

### Naabrite leidmine
Iga 60 sekundi tagant küsitakse kataloogiserverist naabreid ja naabrid kirjutatakse endale ka lokaalsesse faili.