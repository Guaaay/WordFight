CREATE DATABASE IF NOT EXISTS wordbender;
USE wordbender;
CREATE TABLE IF NOT EXISTS usuario (
	id VARCHAR(50), 
    victorias INT, 
    max_victorias INT, 
    isPeleando INT, #0 no peleando, 1 peleando
    nombre_monstruo VARCHAR(20),
    daño_pelea DOUBLE,
    turnos_pelea INT,
    PRIMARY KEY(id),
    FOREIGN KEY (nombre_monstruo) REFERENCES monstruo (nombre)
    );
    
UPDATE `wordbender`.`usuario` SET `victorias` = victorias + 1 WHERE (`id` = '867137636');
SELECT monstruo.nombre, 
	 monstruo.tipo, 
	 monstruo.vida_total, 
	 monstruo.turnos,
	 usuario.daño_pelea,
	 usuario.turnos_pelea, 
	 usuario.max_victorias, 
     usuario.victorias FROM monstruo INNER JOIN usuario ON monstruo.nombre=usuario.nombre_monstruo WHERE usuario.id=867137636;
     
UPDATE `wordbender`.`usuario` SET `nombre_monstruo` = "Diva" WHERE (`id` = (?));
SELECT * FROM usuario;
SELECT * FROM monstruo;
SELECT nombre FROM monstruo ORDER BY RAND() LIMIT 1;
SELECT * FROM frases_monstruo;
UPDATE `wordbender`.`usuario` SET `victorias` = '1' WHERE (`id` = '861336740');

CREATE TABLE IF NOT EXISTS monstruo (
	nombre VARCHAR(20), 
	tipo INT,
	vida_total DOUBLE,
	turnos INT,
	PRIMARY KEY(nombre) 
);

CREATE TABLE IF NOT EXISTS frases_monstruo (
	nombre_monstruo VARCHAR(20),
	intro_monstruo VARCHAR(1024),
    hurt VARCHAR(1024),
    nothurt_pos VARCHAR(1024),
    nothurt_neg VARCHAR(1024),
    defeat VARCHAR(1024),
    FOREIGN KEY (nombre_monstruo) REFERENCES monstruo (nombre) ON DELETE CASCADE
);

INSERT INTO monstruo (nombre, tipo, vida_total, turnos)
VALUES ("Diva", 1, 20, 5);
INSERT INTO monstruo (nombre, tipo, vida_total, turnos)
VALUES ("Inseguro", 2, 20, 5);
INSERT INTO monstruo (nombre, tipo, vida_total, turnos)
VALUES ("Troll", 3, 20, 5);
INSERT INTO monstruo (nombre, tipo, vida_total, turnos)
VALUES ("Gigante", 4, 20, 5);

INSERT INTO frases_monstruo (nombre_monstruo, intro_monstruo, hurt, nothurt_neg, nothurt_pos, defeat)
VALUES ("Diva","El monstruo que se halla frente a ti no es otra cosa que una diva. No llegarás a ningún lado con insultos o halagos sin entusiasmo. Sus impulsos depredadores solo disminuirán con los halagos más excesivos y exagerados." , 
    "“Ay cariño, qué cosas más bonitas me dices… Por supuesto todo esto ya lo sabía… Pero escucharlo de la boca de otro nunca sienta mal… ¡MÁS!”
","“Te atreves a insultarme… ¿A MÍ? ¡Te voy a arrancar esa lengua venenosa para que nunca más salgan de tu boca esas sucias palabras!”
","“Mi abuela podría decirme cosas más bonitas que esas… ¡Y está MUERTA! ¡Se nota que lo que me dices no sale de tu corazón!”
","“Para ser un humano sabes cómo ganarte a un modesto monstruito como yo… jiji… Te dejaré pasar por esta vez, pero solo si me prometes más de esas bonitas palabras la próxima vez que nos crucemos…”");

INSERT INTO frases_monstruo (nombre_monstruo, intro_monstruo, hurt, nothurt_neg, nothurt_pos, defeat)
VALUES ("Inseguro","¡Oh no! Te has topado con un monstruo con grandes problemas de autoestima. Las grandes palabras no le llegaran y tus insultos no harán que se quiera menos de lo que lo hace ya. Solo conseguirás derrotarlo si consigues llegar a su gran corazón con palabras tiernas y desinteresadas.",
    "“…Gracias…uwu“ *se sonroja* ",
    "“Ay jo, no me digas eso…snif…snif. Sé un poco más considerado :( “
","“ Pero…pero…pero…¿A qué viene eso? ¿Realmente solo estas diciendo para que me sienta mejor verdad? Eres un falso :( “",
"“ Pero…pero…pero…¿A qué viene eso? ¿Realmente solo estas diciendo para que me sienta mejor verdad? Eres un falso :( “");


INSERT INTO frases_monstruo (nombre_monstruo, intro_monstruo, hurt, nothurt_neg, nothurt_pos, defeat)
VALUES ("Troll","Ante ti se interpone una criatura grasienta, maloliente y sarcástica. Parece que busca una pelea con alguien, y no tiene pinta de que vaya a ser comprensivo ni racional. Lo mejor que puedes hacer es herir su orgullo poco a poco con insultos velados y sarcasmo, sin ser demasiado explícitos.",
    "… ¡Ugh! Tu argumento… ¡No tiene sentido! No me gusta porque… porque… ¡Eres tonto y hueles mal! ",
    "*Una sonrisa insufriblemente engreída se forma en la cara del Troll* ¡Aquí lo tenemos, monstruos y monstruas! El pequeño humano ha sido llevado a los límites de su pobre intelecto, y su débil y penosa voluntad no ha sido suficiente para evitar que caiga por el precipicio de los insultos e improperios. Con esos insultos tan exagerados, ¡Solo consigues darme la razón!",
    "*El Troll pone cara de sabelotodo y una sonrisa exasperante* Con que admites que tengo razón… Por supuesto… Siempre la tengo, al fin y al cabo. ¡Mis habilidades de retórica y argumentación han podido con tu débil mente de humano, y te han reducido a un idiota adulador balbuceante!",
    "*Has dejado al Troll sin argumentos y lo has reducido a una masa entrecortada de insultos y falacias ad hominem* Pues… Pues… ¡Eres terrible! ¡Tu cara es como una boñiga de caballo! ¡Has cometido varios errores de ortografía! ¡He mirado tu perfil de telegram y pareces una persona débil y estúpida! *El Troll se va deshaciendo poco a poco en un charco de una sustancia viscosa y con olor a sudor*"
);
INSERT INTO frases_monstruo (nombre_monstruo, intro_monstruo, hurt, nothurt_pos, nothurt_neg, defeat)
VALUES ("Gigante","En tu camino se ha interpuesto una figura gigantesca que te mira desde arriba con unos aires de superioridad. Este ser parece que lleva mucho tiempo aburrido, y tu llegada le viene perfecta para encontrar un poco de diversión. Los gigantes son seres muy cortitos por lo que cualquier tipo de doble sentido o chiste inteligente no funcionaran contra él. Lo mejor en estos casos es que saques toda tu rabia interior e intentes destrozarlo de las formas más bastas que se te ocurran.",
    "*El Gigante se sobresalta y se echa hacia atrás al oír tus palabras* “ Gigante entender eso, Gigante sentirse dolido “",
"*El Gigante se queda con cara pensativa* “¿Por qué decir esas cosas a Gigante? ¿A caso tu querer salir con Gigante? Tú deber saber que Gigante no sale con chiquitos”",
"*El Gigante parece confundido ante tus palabras* “Gigante no entender a qué te refieres ¿Es algún tipo de broma humana?",
"*Ves como brotan lágrimas de los ojos del Gigante* “Snif..snif… ¿Gigante también tiene sentimiento sabes? Incluso más que humanos, porque Gigante tiene corazón más grande. BUAAAAAAH *El gigante huye de la escena dejando unas grandes pisadas a su paso*");

SELECT * FROM monstruo;
    
