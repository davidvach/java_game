
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Ability {
    private int id;
    private String name;
    private String description;
    private double strengthFactor;
    private int attackId;
    private int healId;
    
    public int getId() {return id;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public double getStrengthFactor() {return strengthFactor;}
    public int getAttackId() {return attackId;}
    public int gethealId() {return healId;}
    public void setId(int val) {id = val;}
    public void setName(String val) {name = val;}
    public void setDescription(String val) {description = val;}
    public void setStrengthFactor(double val) {strengthFactor = val;}
    public void setAttackId(int val) {attackId = val;}
    public void setHealId(int val) {healId = val;}
    
    public String toString() {
        String ret = "";
        ret += "ID: " + getId() + " " + getName() + " - " + getDescription() + " STRENGTH FACTOR: "+getStrengthFactor()+" ";
        if (attackId != 0) {
            ret += "ATTACK";
        }
        if (healId != 0) {
            ret += "HEAL";
        }
        return ret;
    }
    
    /**
     * provides a list of all abilities in a database
     * @return list of abilities
     * @throws SQLException 
     */
    
    public static List<Ability> getListOfAllAbilities() throws SQLException {
        List<Ability> res = new ArrayList<>();
        String sql = "SELECT * FROM abilities";
        try (Statement s = DbContext.getConnection().createStatement()) {
            try(ResultSet rs = s.executeQuery(sql)) {
                while (rs.next()) {
                    Ability a = new Ability();
                    a.setId(rs.getInt("id"));
                    a.setName(rs.getString("name"));
                    a.setDescription(rs.getString("description"));
                    a.setStrengthFactor(rs.getDouble("strength_factor"));
                    a.setAttackId(rs.getInt("attack_id"));
                    a.setHealId(rs.getInt("heal_id"));
                    res.add(a);
                }
            }
        }
        return res;
    }
    
    /**
     * prints all abilities in a database
     * @throws SQLException 
     */
    
    public static void printAllAbilities() throws SQLException {
        List<Ability> abs = getListOfAllAbilities();
        for (Ability a : abs) {
            System.out.println(a);
        }
    }
    
    /**
     * provides a type of this class (fight / heal)
     * @return type of a class
     */
    
    public String getType() {
        if (getAttackId() == 0) {
            return "HEAL";
        }
        return "ATTACK";
    }
}
