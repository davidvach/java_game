
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
public class Finder_item {
    private static final Finder_item INSTANCE = new Finder_item();

    public static Finder_item getInstance() {
        return INSTANCE;
    }
    
    /**
     * finds an item in a database
     * @param id id of item
     * @return returns item as an object
     * @throws SQLException 
     */
    
    public static Item findById(int id) throws SQLException {
        
        String sql = "SELECT * FROM items WHERE id = ?";
        
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, id);
            
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) {
                    Item i = new Item();

                    i.setId(r.getInt("id"));
                    i.setName(r.getString("name"));
                    i.setDescription(r.getString("description"));
                    i.setArmor_id(r.getInt("armor_id"));
                    i.setWeapon_id(r.getInt("weapon_id"));
                    i.setRing_id(r.getInt("ring_id"));
                    i.setPrice(r.getInt("price"));

                    if (r.next()) {
                        throw new RuntimeException("!!! There exist more player with this ID !!!");
                    }

                    return i;
                } else {
                    return null;
                }
            }
            
        }
    }
}
