
import java.sql.Connection;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vacha
 */
public class DbContext {
    private static Connection connection;
    
    static void setConnection(Connection c) {
        connection = c;
    }
    
    static Connection getConnection() {return connection;}
    
    static void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
        connection = null;
    }
    
    
    
}
