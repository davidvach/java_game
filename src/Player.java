
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vacha
 */
public class Player {
    
    private int id;
    private String username;
    private String password;
    private String email;
    private BigDecimal credit_amount;
    
    public void setUsername(String ret) {
        username = ret;
    }
    
    public void setPassword(String ret) {
        password = ret;
    }
    
    public void setEmail(String ret) {
        email = ret;
    }
    
    public void setId(int i) {
        id = i;
    }
    
    public void setCredit_amount(BigDecimal num) {
        credit_amount = num;
    }
    
    public String getUsername() {return username;}

    public String getPassword() {return password;}

    public String getEmail() {return email;}

    public int getId() {return id;}

    public BigDecimal getCredit_amount() {return credit_amount;}
    
    public String toString() {
        String ret = "";
        ret += "ID: "+getId()+" NAME: "+getUsername()+" EMAIL: "+getEmail()+" CREDIT AMOUNT: "+getCredit_amount();
        return ret;
    }
    
    /**
     * inserts player to database
     * @return inserted player
     * @throws SQLException
     */
    public Player insert() throws SQLException {
        String sql = "INSERT INTO player (username, password, email, credit_amount) VALUES (?,?,?,?)";
        
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, getUsername());
            s.setString(2, getPassword());
            s.setString(3, getEmail());
            s.setBigDecimal(4, getCredit_amount());
            
            s.executeUpdate();
            
            try (ResultSet r = s.getGeneratedKeys()) {
                while(r.next()) {
                    setId(r.getInt(1));
                }
            }
        }
        return this;
    }
    
    /**
     * delete player from database
     * @throws SQLException
     */
    public void delete() throws SQLException {
        Connection c = DbContext.getConnection();
        c.setAutoCommit(false);
        if (c.getTransactionIsolation() == 0) {
        c.setTransactionIsolation(c.TRANSACTION_READ_COMMITTED);
        }
        String sql = "DELETE FROM player WHERE id = ?";
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
     * update player in database
     * @throws SQLException
     */
    public void update() throws SQLException {
        String sql = "UPDATE player SET password = ?, email = ? WHERE id = ?";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setString(1, getPassword());
            s.setString(2, getEmail());
            s.setInt(3, getId());
            s.executeUpdate();
        } 
    }
    
    /**
     * perfom a transaction of a player
     * @param cost how much money does he pay
     * @throws SQLException
     */
    public void pay(BigDecimal cost) throws SQLException {
        setCredit_amount(getCredit_amount().subtract(cost));
        String sql = "UPDATE player SET credit_amount = ? WHERE id = ?";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setBigDecimal(1, getCredit_amount());
            s.setInt(2, getId());
            s.executeUpdate();
        }
    }
    
    /**
     * 
     * @return list of characters
     * @throws IOException
     * @throws SQLException
     */
    public List<Character> getListOfAllCharacters() throws IOException, SQLException {
        List<Character> res = new ArrayList<Character>();
        String sql = "SELECT * FROM game_character WHERE player_id = ?";
        
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, getId());
            
            try (ResultSet r = s.executeQuery()) {
                while (r.next() == true) {
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
                    
                    res.add(c);
                }
            }
        }
        return res;
    }
    
    /**
     * prints fee history of a player
     * @throws SQLException
     */
    public void printFeeHistory() throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM fee_history WHERE player_id = ?";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, getId());
            
            try(ResultSet r = s.executeQuery()) {
                while (r.next() == true) {
                    String sql2 = "SELECT * FROM services WHERE id = ?";
                    try(PreparedStatement s2 = DbContext.getConnection().prepareStatement(sql2)) {
                        s2.setInt(1, r.getInt("service_id"));
                        try(ResultSet r2 = s2.executeQuery()) {
                            while (r2.next() == true) {
                                Service ser = new Service();
                                ser.setId(r2.getInt("id"));
                                ser.setType(r2.getString("type"));
                                ser.setDescription(r2.getString("description"));
                                ser.setPrice(r2.getDouble("price"));
                                ser.setDate(r.getDate("date"));
                                services.add(ser);
                            }
                        }
                    }
                }
            }
        }
        for (Service ser : services) {
            System.out.println(ser);
        }
    }
    
    /**
     * test method - add money
     * @param val how much money to add
     * @throws SQLException
     */
    public void testAddMoney(int val) throws SQLException {
        String sql = "UPDATE player SET credit_amount = ? WHERE id = ?";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setBigDecimal(1, BigDecimal.valueOf(val).add(getCredit_amount()));
            s.setInt(2, getId());
            s.executeUpdate();
        }
    }
    
    /**
     * add money to player
     * @param val how much money to add
     * @throws SQLException
     */
    public void addMoney(double val) throws SQLException {
        String sql = "UPDATE player SET credit_amount = "+getCredit_amount()+"+ ? WHERE id = ?;";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setBigDecimal(1, BigDecimal.valueOf(val));
            s.setInt(2, getId());
            s.executeUpdate();
        }
    }
    /**
     * adds a transaction into fee history of a player
     * @param service_id id of a specific service
     * @throws SQLException 
     */
    
    public void feeHistoryAdd(int service_id) throws SQLException {
        String sql = "INSERT INTO fee_history (player_id, date, service_id) VALUES ("+getId()+",NOW(),+"+service_id+");";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.executeUpdate();
        }
    }
    
}
