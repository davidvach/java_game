
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vacha
 */
public class Character extends Object {
    private int id;
    private int player_id;
    private String name;
    private int current_xp;
    private int current_health;
    private String gender;
    private String eye_color;
    private String hair_color;
    private String face_expression;
    private boolean is_alive;
    private int base_attributes_id;
    private int character_class_id;
    private int level_number;
    
    public void setId(int num) {id = num;}
    public void setPlayer_id(int num) {player_id = num;}
    public void setName(String ret) {name = ret;}
    public void setCurrent_xp(int num) {current_xp = num;}
    public void setCurrent_health(int num) {current_health = num;}
    public void setGender(String ret) {gender = ret;}
    public void setEye_color(String ret) {eye_color = ret;}
    public void setHair_color(String ret) {hair_color = ret;}
    public void setFace_expression(String ret) {face_expression = ret;}
    public void setIs_alive(boolean value) {is_alive = value;}
    public void setBase_attributes_id(int num) {base_attributes_id = num;}
    public void setCharacter_class_id(int num) {character_class_id = num;}
    public void setLevel_number(int num) {level_number = num;}
    public int getId() {return id;}
    public int getPlayer_id() {return player_id;}
    public String getName() {return name;}
    public int getCurrent_xp() {return current_xp;}
    public int getCurrent_health() {return current_health;}
    public String getGender() {return gender;}
    public String getEye_color() {return eye_color;}
    public String getHair_color() {return hair_color;}
    public String getFace_expression() {return face_expression;}
    public boolean getIs_alive() {return is_alive;}
    public int getBase_attributes_id() {return base_attributes_id;}
    public int getCharacter_class_id() {return character_class_id;}
    public int getLevel_number() {return level_number;}
    
    @Override
    public String toString() {
        String res = "";
        try {
            String classType = "";
            if (getCharacter_class_id() == 1) {classType = "Lightrange warrior";}
            else {
                if (getCharacter_class_id() == 2) {classType = "Archer";}
                else classType = "Mage";
            }
            res += "ID: "+getId()+" OWNER: "+getOwner().getUsername()+" NAME: "+getName()+" LEVEL: "+getLevel_number()+" "+getCurrent_xp()+"/"
                    + ""+xpNeededForLevelUp()+"XP "+getCurrent_health()+"HP GENDER: "+
                    getGender()+" "+getEye_color()+" eyes "+getHair_color()+" hair "+getFace_expression()+" face CLASS: "+classType+" ALIVE: "+getIs_alive();
        } catch(SQLException | NullPointerException e) {
            //System.out.println("!!! Error occured while searching for owner of character !!!");
        }
        return res;
    }
    
    /**
     * provides owner of a character
     * @return player instance
     * @throws SQLException 
     */
    
    public Player getOwner() throws SQLException {
        return Finder_player.getInstance().findById(getPlayer_id());
    }
    
    /**
     * inserts character into database
     * @return inserted character
     * @throws SQLException 
     */
    
    public Character insert() throws SQLException {
        String sql = "INSERT INTO game_character (player_id, name, current_xp, current_health, gender, eye_color, hair_color, face_expression, " +
                     "is_alive, character_class_id, level_number) VALUES (?,?,?,100,?,?,?,?,?,?,1)";
        
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            s.setInt(1, getPlayer_id());
            s.setString(2, getName());
            s.setInt(3, getCurrent_xp());
            //s.setInt(4, getCurrent_health());
            s.setString(4, getGender());
            s.setString(5, getEye_color());
            s.setString(6, getHair_color());
            s.setString(7, getFace_expression());
            s.setBoolean(8, getIs_alive());
            s.setInt(9, getCharacter_class_id());
            //s.setInt(10, getLevel_number());
            s.executeUpdate();
            try (ResultSet r = s.getGeneratedKeys()) {
                while(r.next()) {
                    setId(r.getInt(1));
                    setPlayer_id(r.getInt(2));
                    setName(r.getString(3));
                    setCurrent_xp(r.getInt(4));
                    setCurrent_health(r.getInt(5));
                    setGender(r.getString(6));
                    setEye_color(r.getString(7));
                    setHair_color(r.getString(8));
                    setFace_expression(r.getString(9));
                    setIs_alive(true);
                    setBase_attributes_id(r.getInt(11));
                    setCharacter_class_id(r.getInt(12));
                    setLevel_number(r.getInt(13));
                }
            }
            
        }
        //este mu treba nastavit atributy a triedu
        String sql2 = "INSERT INTO base_attributes (defense, strength, health) VALUES (?,?,?)";
        
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql2,Statement.RETURN_GENERATED_KEYS)) {
            s.setInt(1, 10*getLevel_number());
            s.setInt(2, 20*getLevel_number());
            s.setInt(3, 100*getLevel_number());
            s.executeUpdate();
            try(ResultSet r = s.getGeneratedKeys()) {
                int id_atts = 0;
                while(r.next()) {
                    id_atts = r.getInt(1);
                }
                String sql3 = "UPDATE game_character SET base_attributes_id = ? WHERE id = ?";
                try(PreparedStatement s2 = DbContext.getConnection().prepareStatement(sql3,Statement.RETURN_GENERATED_KEYS)) {
                    s2.setInt(1, id_atts);
                    s2.setInt(2, getId());
                    s2.executeUpdate();
                    
                }
                setBase_attributes_id(id_atts);
            }
        }
        return this;
    }
    
    /**
     * delete character from a database
     * @throws SQLException 
     */
    
    public void delete() throws SQLException {
        Connection c = DbContext.getConnection();
        c.setAutoCommit(false);
        if (c.getTransactionIsolation() == 0) {
        c.setTransactionIsolation(c.TRANSACTION_READ_COMMITTED);
        }
        String sql = "DELETE FROM game_character WHERE id = ?";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, id);
            s.executeUpdate();
            c.commit();
            c.setAutoCommit(true);
        } catch (SQLException e) {
           c.rollback();
        }
    }
    
    /**
     * provides a list of all items owned by this character
     * @return list of items
     * @throws SQLException 
     */
    
    public List<Item> getItems() throws SQLException {
        List<Item> res = new ArrayList<Item>();
        String sql = "SELECT item_id FROM game_character_owns_item WHERE game_character_id = ?";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, id);
            
            try (ResultSet r = s.executeQuery()) {
                while (r.next() == true) {
                    Item i = Finder_item.getInstance().findById(r.getInt("item_id"));
                    res.add(i);
                }
            }
        }
        return res;
    }

    private void printCapOfItems() {
        System.out.println("The maximal number of items for this character is "+getLevel_number());
    }

    public void makeGetItems() throws SQLException {
        getItems();
        printCapOfItems();
    }
    
    /**
     * adds item for this character
     * @param itemId id of an item to add
     * @throws SQLException 
     */
    
    public void addItem(int itemId) throws SQLException {
        String sql = "INSERT INTO game_character_owns_item (game_character_id, item_id) VALUES (?,?)";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, id);
            s.setInt(2, itemId);
            s.executeUpdate();
        }
    }
    
    /**
     * removes item from a character
     * @param itemId id of an item to delete
     * @throws SQLException 
     */
    
    public void deleteItem(int itemId) throws SQLException {
        String sql = "DELETE FROM game_character_owns_item WHERE game_character_id = ? and item_id = ?";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, id);
            s.setInt(2, itemId);
            s.executeUpdate();
        }
    }
    
    /**
     * perfoms leveling up of a character
     * @return true if condition of having enough XP passed, false else
     * @throws SQLException 
     */
    
    public boolean levelUp() throws SQLException {
        boolean satisfiedConds = false;
        int newLevel = -1;
        String sql = "SELECT current_xp, level_number FROM game_character WHERE id = ?";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, getId());
            try(ResultSet r = s.executeQuery()) {
                while (r.next() == true) {
                    String sql2 = "SELECT * FROM level WHERE number = ?";
                    try(PreparedStatement s2 = DbContext.getConnection().prepareStatement(sql2)) {
                        s2.setInt(1, r.getInt("level_number"));
                        try(ResultSet r2 = s2.executeQuery()) {
                            while (r2.next() == true) {
                                if (r2.getInt("xp_next") <= r.getInt("current_xp")) {
                                    satisfiedConds = true;
                                }
                            }
                        }
                    }
                    newLevel = r.getInt("level_number")+1;
                }
            }
        }
        if (!satisfiedConds) {
            return false;
        }
        //navysenie levelu (este nie su riesene zivoty)
        String sql3 = "UPDATE game_character SET level_number = ?, current_xp = 0, current_health = ? WHERE id = ?";
        try(PreparedStatement s3 = DbContext.getConnection().prepareStatement(sql3)) {
            s3.setInt(1, newLevel);
            s3.setInt(2, newLevel*100);
            s3.setInt(3, getId());
            s3.executeUpdate();
        }
        return true;
    }
    
    /**
     * set character's isAlive to false
     * @throws SQLException 
     */
    
    public void setIsDead() throws SQLException {
        String sql = "UPDATE game_character SET is_alive = false WHERE id = ?;";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, getId());
            s.executeUpdate();
        }
    }
    
    /**
     * adds experience points to character
     * @param value number of XPs to add
     * @throws SQLException 
     */
    
    public void addXP(int value) throws SQLException {
        String sql = "UPDATE game_character SET current_xp = "+getCurrent_xp()+"+? WHERE id = ?;";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, value);
            s.setInt(2, getId());
            s.executeUpdate();
        }
        levelUp();
    }
    
    /**
     * provides a number of XPs this character needs to level up
     * @return number of left XPs
     * @throws SQLException 
     */
    
    public int xpNeededForLevelUp() throws SQLException {
        String sql = "SELECT xp_next FROM level WHERE number = "+getLevel_number();
        int result = -1;
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.executeQuery();
            try (ResultSet rs = s.executeQuery()) {
                while(rs.next() == true) {
                    result = rs.getInt("xp_next");
                }
            }
        }
        return result;
    }
    
    /**
     * writes passed battle into fee history table
     * @param looser character who lose the battle
     * @throws SQLException 
     */
    
    public void writeBattleHistory(Character looser) throws SQLException {
        String sql = "INSERT INTO battle_history (game_character_id, opponent_id, date_battle, result_state) VALUES"
                + "(?,?,NOW(),'WINNER')";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, getId());
            s.setInt(2, looser.getId());
            s.executeUpdate();
        }
        String sql2 = "INSERT INTO battle_history (game_character_id, opponent_id, date_battle, result_state) VALUES"
                + "(?,?,NOW(),'LOOSER')";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql2)) {
            s.setInt(1, looser.getId());
            s.setInt(2, getId());
            s.executeUpdate();
        }
    }
}
