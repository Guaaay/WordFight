package main.java.auxiliar;

import java.sql.Connection;

public class Tokens {

    public static final String driver = "com.mysql.cj.jdbc.Driver";   
    public static final String url = "jdbc:mysql://localhost:3306/mas";    
    public static final String user = "agent";   
    public static final String pass = "mientras";
    public static String urlstring;
    
    public static final String command1 = "/ayuda";
    public static final String command2 = "/crearpj";
    public static final String command3 = "/atacarnpc";
    public static final String command31 = "/lobo";
    public static final String command32 = "/jabali";
    public static final String command4 = "/atacarjugador";
    public static final String command5 = "/defenderse";
    public static final String command6 = "/consultarstats";

    public static final String pjNoExiste = "El jugador no existe. Por favor, usa el comando /crearpj para crear nuevo personaje";
    
    public static final String createPJ = "INSERT IGNORE INTO characterplayer (id) VALUES (?)";
    public static final String attacknpc = "INSERT IGNORE INTO characterplayer (id) VALUES (?)";
    public static final String defense = "UPDATE characterplayer SET isdefensing=(?), defense=defense+5 WHERE id=(?)";
    public static final String isDefense = "SELECT isdefensing FROM characterplayer WHERE id=";
    public static final String quitDefense = "UPDATE characterplayer SET isdefensing=false, defense=defense-5 WHERE id=(?)";
    public static final String experience = "SELECT nextlevel FROM characterplayer WHERE id=";
    public static final String updateXP = "UPDATE characterplayer SET nextlevel=nextlevel+1 WHERE id=(?)";
    public static final String nextLvl = "UPDATE characterplayer SET level=level+1,attack=attack+1,defense=defense+1,nextlevel=(?) WHERE id=(?)";
    public static final String attackerdefenser = "SELECT attack,defense FROM characterplayer WHERE id=";

}
