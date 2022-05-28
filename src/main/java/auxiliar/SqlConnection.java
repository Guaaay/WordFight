package auxiliar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class SqlConnection { 

	/**
	 * Método para conectar a una BBDD MySQL
	 */
    public static Connection getConnection() {
    	Connection conn = null;
        try {
            Class.forName(Tokens.driver);
            try {
            	conn = DriverManager.getConnection(Tokens.url, Tokens.user, Tokens.pass);
            } catch (SQLException e) {
            	System.out.println("Error al conectar."); 
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Driver no encontrado."); 
        }
        
        return conn;
        
    }
}
