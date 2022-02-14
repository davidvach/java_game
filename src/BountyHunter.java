
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BountyHunter {
    
    /**
     * perfoms bounty hunter transaction
     * @return true if transaction passed, false else
     * @throws SQLException 
     */
    
    public static boolean evaluate() throws SQLException {
        
        Connection c = DbContext.getConnection();
        c.setAutoCommit(false);
        if (c.getTransactionIsolation() == 0) {
        c.setTransactionIsolation(c.TRANSACTION_READ_COMMITTED);   //zaujima ma stav len tesne pred vyhodnotenim (kludne mohol sekundu predtym zabit hraca)
        }
        
        int charId = 0; int kills = 0;
        String sql = "CREATE OR REPLACE FUNCTION getMostKiller()\n"
                + "RETURNS TABLE (game_character_id integer, count bigint)\n"
                + "LANGUAGE sql AS \n"
                + "$$\n"
                + "SELECT game_character_id, count(*) FROM battle_history WHERE result_state = 'WINNER' AND date_battle BETWEEN NOW() - INTERVAL '30 days' AND NOW() GROUP BY game_character_id ORDER BY count(*) DESC LIMIT 1\n"
                + "$$;\n"
                + "";
        
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.executeUpdate();
            try (Statement s2 = c.createStatement()) {
                ResultSet rs = s2.executeQuery("SELECT * FROM getMostKiller();");
                while (rs.next()) {
                    charId = rs.getInt(1);
                    kills = rs.getInt(2);
                }
            } catch (SQLException e) {
                System.out.println("Something wrong happened");
                c.rollback();
                return false;
            }
            
            
        }
        Character charWin = Finder_character.getInstance().findById(charId);
        Player p = Finder_player.getInstance().findById(charWin.getPlayer_id());
        p.testAddMoney(100);
        System.out.println("Bounty Hunter is: "+charWin.getName()+" with ID "+charWin.getId()+" and with "+kills+" kills! He won 100 credits.");
        
        String sql2 = "CREATE OR REPLACE FUNCTION getCredit(num int)\n"
                + "RETURNS double precision LANGUAGE sql AS $$"
                + "SELECT credit_amount FROM player WHERE id = num"
                + "$$;"
                + ""
                + "CREATE OR REPLACE FUNCTION getTop3KillersOf(num int)"
                + "RETURNS TABLE (game_character_id integer, count bigint) LANGUAGE sql AS $$\n"
                + "SELECT game_character_id, count(*) FROM battle_history WHERE result_state = 'WINNER' "
                + "AND (date_battle BETWEEN NOW() - INTERVAL '30 days' AND NOW()) AND opponent_id = num "
                + "GROUP BY game_character_id ORDER BY count(*) DESC LIMIT 3;\n"
                + "$$;";
        
        try (PreparedStatement s = c.prepareStatement(sql2)) {
            s.executeUpdate();
            try (Statement s2 = c.createStatement()) {
                ResultSet rs = s2.executeQuery("SELECT * FROM getTop3KillersOf("+charId+")");
                int prize = 90;
                System.out.println("These 3 characters killed him the most:");
                while (rs.next()) {
                    charId = rs.getInt(1);
                    kills = rs.getInt(2);
                    Character charW = Finder_character.getInstance().findById(charId);
                    Player pT = Finder_player.getInstance().findById(charW.getPlayer_id());
                    System.out.println("   "+charW.getName()+" with ID "+charW.getId()+" and with "+kills+" kills. He won "+prize+" credits.");
                    pT.testAddMoney(prize);
                    prize -= 30;
                }
            }
        } catch (SQLException e) {
            System.out.println("Something wrong happened");
            c.rollback();
            return false;
        }
        c.commit();;
        c.setAutoCommit(true);
        return true;
    }
    
}
