
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Finder_class {
    private static final Finder_class INSTANCE = new Finder_class();

    public static Finder_class getInstance() {
        return INSTANCE;
    }
    
    /**
     * finds an class in a database
     * @param id id of class
     * @return returns class as an object
     * @throws SQLException 
     */
    
    public static Class findById(int id) throws SQLException {
        
        String sql = "SELECT * FROM character_class WHERE id = ?";
        
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, id);
            
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    Class c = new Class();

                    c.setId(rs.getInt("id"));
                    c.setType(rs.getString("type"));
                    c.setDescription(rs.getString("description"));

                    if (rs.next()) {
                        throw new RuntimeException("!!! There exist more classes with this ID !!!");
                    }

                    return c;
                } else {
                    return null;
                }
            }
            
        }
    }
}
