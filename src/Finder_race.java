
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Finder_race {
    private static final Finder_race INSTANCE = new Finder_race();

    public static Finder_race getInstance() {
        return INSTANCE;
    }
    
    /**
     * finds an race in a database
     * @param id id of race
     * @return returns race as an object
     * @throws SQLException 
     */
    
    public static Race findById(int id) throws SQLException {
        
        String sql = "SELECT * FROM race WHERE id = ?";
        
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, id);
            
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    Race r = new Race();

                    r.setId(rs.getInt("id"));
                    r.setType(rs.getString("type"));
                    r.setDescription(rs.getString("description"));

                    if (rs.next()) {
                        throw new RuntimeException("!!! There exist more races with this ID !!!");
                    }

                    return r;
                } else {
                    return null;
                }
            }
            
        }
    }
}
