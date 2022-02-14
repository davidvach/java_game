
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ChangeAppearance {
    
    private Player player;
    private Character character;
    private Connection c;
    
    public ChangeAppearance(Player p, int id) throws SQLException {
        player = p;
        character = Finder_character.getInstance().findById(id);
        c = DbContext.getConnection();
        c.setAutoCommit(false);
        if (c.getTransactionIsolation() == 0) {
        c.setTransactionIsolation(c.TRANSACTION_READ_COMMITTED); //stacia mi vzdy aktualne data
        }
    }
    
    public boolean checkCredit() throws SQLException {
        if (player.getCredit_amount().compareTo(BigDecimal.valueOf(50.0)) < 0) {
            c.rollback();
            c.setAutoCommit(true);
            return false;
        }
        return true;
    }
    
    public boolean updateGender(String gender) throws SQLException {
        String sql = "UPDATE game_character SET gender = '"+gender+"' WHERE id = "+character.getId();
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.executeUpdate();
        } catch (SQLException e) {
            c.rollback();
            c.setAutoCommit(true);
            return false;
        }
        return true;
    }
    
    public boolean updateEyeColor(String color) throws SQLException {
        String sql = "UPDATE game_character SET eye_color = '"+color+"' WHERE id = "+character.getId();
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.executeUpdate();
        } catch (SQLException e) {
            c.rollback();
            c.setAutoCommit(true);
            return false;
        }
        return true;
    }
    
    public boolean updateHairColor(String color) throws SQLException {
        String sql = "UPDATE game_character SET eye_color = '"+color+"' WHERE id = "+character.getId();
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.executeUpdate();
        } catch (SQLException e) {
            c.rollback();
            c.setAutoCommit(true);
            return false;
        }
        return true;
    }
    
    public boolean updateFaceExpression(String answer) throws SQLException {
        String sql = "UPDATE game_character SET face_expression = '"+answer+"' WHERE id = "+character.getId();
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.executeUpdate();
        } catch (SQLException e) {
            c.rollback();
            c.setAutoCommit(true);
            return false;
        }
        return true;
    }
    
    public void confirmChanges(boolean answer) throws SQLException {
        if (answer == true && checkCredit() == true) {
            c.commit();
            player.pay(BigDecimal.valueOf(50));
            player.feeHistoryAdd(12);
            c.setAutoCommit(true);
            return;
        }
        c.rollback();
        c.setAutoCommit(true);
        return;
    }
    
}
