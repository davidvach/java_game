
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Class {
    private int id;
    private String type;
    private String description;
    
    public int getId() {return id;}
    public String getType() {return type;}
    public String getDescription() {return description;}
    public void setId(int val) {id = val;}
    public void setType(String val) {type = val;}
    public void setDescription(String val) {description = val;}
    
    public Class insert() throws SQLException {
        String sql = "INSERT INTO character_class (type, description) VALUES (?,?)";
        
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, getType());
            s.setString(2, getDescription());
            
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
     * provides a list of all game character classes in a database
     * @return list of game character classes
     * @throws SQLException 
     */
    
    public static List<Class> getListOfAllClasses() throws SQLException {
        List<Class> res = new ArrayList<>();
        String sql = "SELECT * FROM character_class";
        try(Statement s = DbContext.getConnection().createStatement()) {
            try(ResultSet rs = s.executeQuery(sql)) {
                while (rs.next()) {
                    Class c = new Class();
                    c.setId(rs.getInt("id"));
                    c.setType(rs.getString("type"));
                    c.setDescription(rs.getString("description"));
                    res.add(c);
                }
            }
        }
        return res;
    }
    
    /**
     * prints list of all game character classes in a database
     * @throws SQLException
     * @throws IOException 
     */
    
    public static void printListOfClasses() throws SQLException, IOException {
        List<Class> classes = Class.getListOfAllClasses();
        for (Class c : classes) {
            System.out.println(c);
        }
    }
    
    /**
     * delete a game character class from a database
     * @throws SQLException 
     */
    
    public void delete() throws SQLException {
        String sql = "DELETE FROM character_class WHERE id = ?";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, id);
            s.executeUpdate();
        }
    }
    
    /**
     * provides a string representation of a specific game character class
     * @return string containing appropriate class data
     */
    
    public String toString() {
        String res = "";
        res += getId()+". "+getType()+": "+getDescription();
        return res;
    }
    
    /**
     * prints all abilities of a game character class
     * @param id id of a class
     * @throws SQLException 
     */
    
    public void printAbilities(int id) throws SQLException {
        List<Ability> abs = new ArrayList<>();
        String sql = "SELECT ability_id FROM ability_belongs_class WHERE character_class_id = ?";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, getId());
            
            try (ResultSet r = s.executeQuery()) {
                while (r.next() == true) {
                    String sql2 = "SELECT * FROM abilities WHERE id = ?";
                    try(PreparedStatement s2 = DbContext.getConnection().prepareStatement(sql2)) {
                        s2.setInt(1, r.getInt("ability_id"));
                        
                        try (ResultSet r2 = s2.executeQuery()) {
                            while (r2.next() == true) {
                                Ability a = new Ability();
                                a.setId(r2.getInt("id"));
                                a.setName(r2.getString("name"));
                                a.setDescription(r2.getString("description"));
                                a.setStrengthFactor(r2.getDouble("strength_factor"));
                                a.setAttackId(r2.getInt("attack_id"));
                                a.setHealId(r2.getInt("heal_id"));
                                abs.add(a);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("For this class are current following abilities:");
        for (Ability a : abs) {
            System.out.println(a);
        }
        
    }
    
    /**
     * adds ability to a class
     * @param a ability to add
     * @throws SQLException 
     */
    
    public void addAbility(Ability a) throws SQLException {
        String sql = "INSERT INTO ability_belongs_class VALUES (?,?)";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, getId());
            s.setInt(2, a.getId());
            s.executeUpdate();
        }
    }
    
    /**
     * removes ability from a class
     * @param a ability to remove
     * @throws SQLException 
     */
    
    public void removeAbility(Ability a) throws SQLException {
        String sql = "DELETE FROM ability_belongs_class WHERE character_class_id = ? AND ability_id = ?";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, getId());
            s.setInt(2, a.getId());
            s.executeUpdate();
        }
    }
}
