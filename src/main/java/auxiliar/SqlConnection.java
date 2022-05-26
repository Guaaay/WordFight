package auxiliar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
