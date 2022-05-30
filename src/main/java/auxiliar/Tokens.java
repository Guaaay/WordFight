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
    public static final String command32 = "/jabali";
    public static final String command42 = "/atacarjugador";
    public static final String command5 = "/defenderse";
    public static final String command6 = "/consultarstats";
    

    public static final String pjNoExiste = "El jugador no existe. Por favor, usa el comando /crearpj para crear nuevo personaje";
    
    public static final String createUser = "INSERT IGNORE INTO usuario (id, victorias, max_victorias, isPeleando) VALUES (?, ? ,? ,?)";
    public static final String consultarStats = "SELECT victorias,max_victorias FROM usuario WHERE id=";
    public static final String isInBattle = "SELECT isPeleando FROM usuario WHERE id=";
    public static final String setBattle = "UPDATE `wordbender`.`usuario` SET `isPeleando` = '1' WHERE (`id` = (?))";
    public static final String noBattle = "UPDATE `wordbender`.`usuario` SET `isPeleando` = '0' WHERE (`id` = (?))";
    public static final String resetVictories = "UPDATE `wordbender`.`usuario` SET `victorias` = '0' WHERE (`id` = (?))";
    public static final String getMonster = "SELECT nombre FROM monstruo ORDER BY RAND() LIMIT 1";
    public static final String setMonster = "UPDATE `wordbender`.`usuario` SET `nombre_monstruo` = (?) WHERE (`id` = (?))";
    public static final String getFrasesMonstruo = "SELECT intro_monstruo, hurt, nothurt_pos, nothurt_neg, defeat FROM frases_monstruo WHERE nombre_monstruo=";
    public static final String defense = "UPDATE characterplayer SET isdefensing=(?), defense=defense+5 WHERE id=(?)";
    public static final String isDefense = "SELECT isdefensing FROM characterplayer WHERE id=";
    public static final String quitDefense = "UPDATE characterplayer SET isdefensing=false, defense=defense-5 WHERE id=(?)";
    public static final String experience = "SELECT nextlevel FROM characterplayer WHERE id=";
    public static final String updateXP = "UPDATE characterplayer SET nextlevel=nextlevel+1 WHERE id=(?)";
    public static final String nextLvl = "UPDATE characterplayer SET level=level+1,attack=attack+1,defense=defense+1,nextlevel=(?) WHERE id=(?)";
    public static final String attackerdefenser = "SELECT attack,defense FROM characterplayer WHERE id=";
    
    public static final int FraseIntro = 1;
    public static final int FraseHurt = 2;
    public static final int FraseNotHurt_pos = 3;
    public static final int FraseNotHurt_neg = 4;
    public static final int FraseDerrota = 5;

}
