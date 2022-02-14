
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.postgresql.ds.PGSimpleDataSource;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vacha
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, IOException, Exception {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName("db.dai.fmph.uniba.sk");
        ds.setPortNumber(5432);
        ds.setDatabaseName("playground");
        ds.setUser("vachalek7@uniba.sk");
        ds.setPassword("vachalekdavid");
        
        try(Connection c = ds.getConnection()) {
            DbContext.setConnection(c);

            Menu_Console menu = new Menu_Console();
            menu.run();
        } finally {
            DbContext.closeConnection();
        }
    }
}

