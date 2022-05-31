package auxiliar;

import java.sql.Connection;

public class Tokens {

    public static final String driver = "com.mysql.cj.jdbc.Driver";   
    public static final String url = "jdbc:mysql://localhost:3306/wordbender";    
    public static final String user = "wordbender";   
    public static final String pass = "wordbenderdb";
    public static String urlstring;
    
    public static final String command1 = "/start";
    public static final String command2 = "/stats";
    public static final String command3 = "/battle";
    public static final String command4 = "/huir";
    public static final String command5 = "/ataque";
    public static final String command6 = "/help";
   
    

    public static final String pjNoExiste = "El jugador no existe. Por favor, usa el comando /crearpj para crear nuevo personaje";
    
    public static final String createUser = "INSERT IGNORE INTO usuario (id, victorias, max_victorias, isPeleando) VALUES (?, ? ,? ,?)";
    public static final String consultarStats = "SELECT victorias,max_victorias FROM usuario WHERE id=";
    public static final String isInBattle = "SELECT isPeleando FROM usuario WHERE id=";
    public static final String setBattle = "UPDATE `wordbender`.`usuario` SET `isPeleando` = '1', daño_pelea = '0', turnos_pelea = '0' WHERE (`id` = (?))";
    public static final String noBattle = "UPDATE `wordbender`.`usuario` SET `isPeleando` = '0', daño_pelea = '0', turnos_pelea = '0' WHERE (`id` = (?))";
    public static final String resetVictories = "UPDATE `wordbender`.`usuario` SET `victorias` = '0' WHERE (`id` = (?))";
    public static final String incVictories = "UPDATE `wordbender`.`usuario` SET `victorias` = victorias + 1 WHERE (`id` = (?))";
    public static final String incMaxVictories = "UPDATE `wordbender`.`usuario` SET `max_victorias` = max_victorias + 1 WHERE (`id` = (?))";
    public static final String incTurno = "UPDATE `wordbender`.`usuario` SET `turnos_pelea` = turnos_pelea + 1 WHERE (`id` = (?))";
    public static final String resetTurno = "UPDATE `wordbender`.`usuario` SET `turnos_pelea` = 0 WHERE (`id` = (?))";
    public static final String incDaño = "UPDATE `wordbender`.`usuario` SET `daño_pelea` = daño_pelea + (?) WHERE (`id` = (?))";
    public static final String resetDaño = "UPDATE `wordbender`.`usuario` SET `daño_pelea` = 0 WHERE (`id` = (?))";
    public static final String getMonster = "SELECT nombre FROM monstruo ORDER BY RAND() LIMIT 1";
    public static final String setMonster = "UPDATE `wordbender`.`usuario` SET `nombre_monstruo` = (?) WHERE (`id` = (?))";
    public static final String getFrasesMonstruo = "SELECT intro_monstruo, hurt, nothurt_pos, nothurt_neg, defeat FROM frases_monstruo WHERE nombre_monstruo=";
    public static final String getMonsterStats = "SELECT monstruo.nombre, \r\n"
    		+ "	 monstruo.tipo, \r\n"
    		+ "	 monstruo.vida_total, \r\n"
    		+ "	 monstruo.turnos,\r\n"
    		+ "	 usuario.daño_pelea,\r\n"
    		+ "	 usuario.turnos_pelea, \r\n"
    		+ "	 usuario.max_victorias, \r\n"
    		+ "     usuario.victorias FROM monstruo INNER JOIN usuario ON monstruo.nombre=usuario.nombre_monstruo WHERE usuario.id=";
    
    
    public static final int FraseIntro = 1;
    public static final int FraseHurt = 2;
    public static final int FraseNotHurt_pos = 3;
    public static final int FraseNotHurt_neg = 4;
    public static final int FraseDerrota = 5;

}
