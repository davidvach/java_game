
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
public class Finder_character {
    
    private static final Finder_character INSTANCE = new Finder_character();

    public static Finder_character getInstance() {
        return INSTANCE;
    }
    
    /**
     * finds an character in a database
     * @param id id of character
     * @return returns character as an object
     * @throws SQLException 
     */
    
    public static Character findById(int id) throws SQLException {
        
        String sql = "SELECT * FROM game_character WHERE id = ?";
        
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, id);
            
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) {
                    Character c = new Character();

                    c.setId(r.getInt("id"));
                    c.setPlayer_id(r.getInt("player_id"));
                    c.setName(r.getString("name"));
                    c.setCurrent_xp(r.getInt("current_xp"));
                    c.setCurrent_health(r.getInt("current_health"));
                    c.setGender(r.getString("gender"));
                    c.setEye_color(r.getString("eye_color"));
                    c.setHair_color(r.getString("hair_color"));
                    c.setFace_expression(r.getString("face_expression"));
                    c.setIs_alive(r.getBoolean("is_alive"));
                    c.setBase_attributes_id(r.getInt("base_attributes_id"));
                    c.setCharacter_class_id(r.getInt("character_class_id"));
                    c.setLevel_number(r.getInt("level_number"));
                    
                    if (r.next()) {
                        throw new RuntimeException("!!! There exist more player with this ID !!!");
                    }

                    return c;
                } else {
                    return null;
                }
            }
            
        }
    }
    
}
