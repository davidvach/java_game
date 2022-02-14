
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CharacterTransfer {
    
    /**
     * perfoms a character transfer between two players
     * @param buyerId id of a player, who wants to buy a character
     * @param sellerId id of a player, who wants to sell a character
     * @param chId id of a character to buy
     * @return true if transaction passed, false else
     * @throws IOException
     * @throws SQLException 
     */
    
    public static boolean transfer(int buyerId, int sellerId, int chId) throws IOException, SQLException {
        Player b = null; Player s = null; Character ch = null;
        try {
            b = Finder_player.findById(buyerId);
            s = Finder_player.findById(sellerId);
            ch = Finder_character.findById(chId);
        } catch (SQLException e) {
            return false;
        }
        
        try {
            if (b == null || s == null || ch == null) {
                throw new CallException("Did not find a player of character.");
            }
            if ((b.getCredit_amount().compareTo(BigDecimal.valueOf(10.0))) < 0) {
                throw new CallException("Buyer does not have enough credit.");
            }
            if ((s.getCredit_amount().compareTo(BigDecimal.valueOf(7.0))) < 0) {
                throw new CallException("Seller does not have enought credit.");
            }
        } catch (CallException e) {
            System.out.println(e);
            return false;
        }
        
        Connection c = DbContext.getConnection();
        try {
            c.setAutoCommit(false);
            if (c.getTransactionIsolation() == 0) {
            c.setTransactionIsolation(c.TRANSACTION_REPEATABLE_READ);
            }
        } catch (SQLException e) {
            System.out.println("Something wrong happened with connection.");
        }
        
        String sql = "UPDATE game_character SET player_id = "+b.getId()+" WHERE id = "+ch.getId();
        
        try(PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            
            st.executeUpdate();
            //if (Metamorphosis.callConfirmQuestion().equals("n")) {c.rollback(); return false;}
            
            
            try {
                if ((b.getCredit_amount().compareTo(BigDecimal.valueOf(10.0))) < 0) {
                    c.rollback();
                    throw new CallException("Buyer does not have enough credit.");
                }
                if ((s.getCredit_amount().compareTo(BigDecimal.valueOf(7.0))) < 0) {
                    c.rollback();
                    throw new CallException("Seller does not have enought credit.");
                }
                if (b == null || s == null || ch == null) {
                    c.rollback();
                    throw new CallException("Did not find a player of character.");
                }
            } catch (CallException | NullPointerException e) {
                System.out.println(e.toString());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Something wrong happened with prepared statement.");
            return false;
        }
        
        try {
            c.commit();
            b.pay(BigDecimal.valueOf(10));
            s.pay(BigDecimal.valueOf(7));
            b.feeHistoryAdd(11);
            s.feeHistoryAdd(11);
        } catch (SQLException e) {
            System.out.println("Something wrong happened with commit.");
        }
        c.setAutoCommit(true);
        return true;
    }
    
}
