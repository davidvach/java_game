
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Finder_ability {
    private static final Finder_ability INSTANCE = new Finder_ability();

    public static Finder_ability getInstance() {
        return INSTANCE;
    }
    
    /**
     * finds an ability in a database
     * @param id id of ability
     * @return returns ability as an object
     * @throws SQLException 
     */
    
    public static Ability findById(int id) throws SQLException {
        
        String sql = "SELECT * FROM abilities WHERE id = ?";
        
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, id);
            
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    Ability a = new Ability();

                    a.setId(rs.getInt("id"));
                    a.setName(rs.getString("name"));
                    a.setDescription(rs.getString("description"));
                    a.setStrengthFactor(rs.getDouble("strength_factor"));
                    a.setAttackId(rs.getInt("attack_id"));
                    a.setHealId(rs.getInt("heal_id"));

                    if (rs.next()) {
                        throw new RuntimeException("!!! There exist more abilities with this ID !!!");
                    }

                    return a;
                } else {
                    return null;
                }
            }
            
        }
    }
}
