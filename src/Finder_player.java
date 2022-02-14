
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
public class Finder_player {
    
    private static final Finder_player INSTANCE = new Finder_player();

    public static Finder_player getInstance() {
        return INSTANCE;
    }
    
    /**
     * finds an player in a database
     * @param id id of player
     * @return returns player as an object
     * @throws SQLException 
     */
    
    public static Player findById(int id) throws SQLException {
        
        String sql = "SELECT * FROM player WHERE id = ?";
        
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, id);
            
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) {
                    Player p = new Player();

                    p.setId(r.getInt("id"));
                    p.setUsername(r.getString("username"));
                    p.setPassword(r.getString("password"));
                    p.setEmail(r.getString("email"));
                    p.setCredit_amount(r.getBigDecimal("credit_amount"));

                    if (r.next()) {
                        throw new RuntimeException("!!! There exist more player with this ID !!!");
                    }

                    return p;
                } else {
                    try {
                        throw new CallException("!!! Did not find a player !!!");
                    } catch (CallException e) {
                        //System.out.println(e);
                        return null;
                    }
                }
            }
            
        }
    }
    
}
