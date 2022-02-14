
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Race {
    private int id;
    private String type;
    private String description;
    
    public int getId() {return id;}
    public String getType() {return type;}
    public String getDescription() {return description;}
    public void setId(int val) {id = val;}
    public void setType(String val) {type = val;}
    public void setDescription(String val) {description = val;}
    
    public String toString() {
        String ret = "";
        ret += getId() + ". "+getType()+" - "+getDescription();
        return ret;
    }
    
    /**
     * inserts a race into database
     * @return inserted race
     * @throws SQLException 
     */
    
    public Race insert() throws SQLException {
        String sql = "INSERT INTO race (type, description) VALUES (?,?)";
        
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
     * delete a race from a database
     * @throws SQLException 
     */
    
    public void delete() throws SQLException {
        String sql = "DELETE FROM race WHERE id = ?";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, id);
            s.executeUpdate();
        }
    }
    
    /**
     * provides a list of all races in a database
     * @return list of races
     * @throws SQLException 
     */
    
    public static List<Race> getListOfAllRaces() throws SQLException {
        List<Race> races = new ArrayList<>();
        String sql = "SELECT * FROM race";
        try(Statement s = DbContext.getConnection().createStatement()) {
            try(ResultSet rs = s.executeQuery(sql)) {
                while (rs.next()) {
                    Race r = new Race();
                    r.setId(rs.getInt("id"));
                    r.setType(rs.getString("type"));
                    r.setDescription(rs.getString("description"));
                    races.add(r);
                }
            }
        }
        return races;
    }
    
    /**
     * adds class into race
     * @param c instance of a class
     * @throws SQLException 
     */
    
    public void addClass(Class c) throws SQLException {
        String sql = "INSERT INTO class_belongs_race VALUES (?,?)";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, c.getId());
            s.setInt(2, getId());
            s.executeUpdate();
        }
    }
    
    /**
     * removes a clas from a race
     * @param c instance of a class
     * @throws SQLException 
     */
    
    public void removeClass(Class c) throws SQLException {
        String sql = "DELETE FROM class_belongs_race WHERE character_class_id = ? AND race_id = ?";
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, c.getId());
            s.setInt(2, getId());
            s.executeUpdate();
        }
    }
}
