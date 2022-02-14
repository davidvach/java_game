
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Metamorphosis {
    
    /**
     * perfoms metamorphosis of two characters
     * @param c1_id id of first character
     * @param c2_id id of second character
     * @param p_id id of a player, who owns those 2 characters
     * @return true if transaction passed, false else
     * @throws SQLException
     * @throws Exception 
     */
    
    public static boolean metamorphose(int c1_id, int c2_id, int p_id) throws SQLException, Exception {
        Player p = Finder_player.getInstance().findById(p_id);
        Character c1 = Finder_character.getInstance().findById(c1_id);
        Character c2 = Finder_character.getInstance().findById(c2_id);
        
        try {
            if (p == null || c1 == null || c2 == null) {
                throw new CallException("Player or character doesn't exist");
            }

            if (c1.getIs_alive() == false || c2.getIs_alive() == false) {
                throw new CallException("Some of characters is already dead");
            }
        } catch (CallException e) {
            System.out.println(e.getMessage());
        }
        
        Connection c = DbContext.getConnection();
        c.setAutoCommit(false);
        if (c.getTransactionIsolation() == 0) {
        c.setTransactionIsolation(c.TRANSACTION_REPEATABLE_READ);
        }
        
        
        String sql = "";
        try {
        
        sql = "CREATE OR REPLACE FUNCTION getCharacter(num int)\n"
                + "RETURNS TABLE (id integer, player_id integer, name varchar, current_xp integer, current_health integer, gender varchar, "
                + "eye_color varchar, hair_color varchar, face_expression varchar, is_alive boolean, base_attributes_id integer, "
                + "character_class_id integer, level_number integer)\n"
                + "LANGUAGE sql AS\n"
                + "$$\n"
                + "SELECT * FROM game_character WHERE id = num\n"
                + "$$;\n"
                + "INSERT INTO game_character (player_id, name, current_xp, current_health, gender, eye_color, hair_color, face_expression, "
                + "is_alive, base_attributes_id, character_class_id, level_number)\n"
                + "SELECT\n"
                + ""+c1.getPlayer_id()+" as player_id,\n"
                + "'"+c1.getName()+"'::varchar as name,\n"
                + "0 as current_xp,\n"
                + "100 as current_health,\n"
                + "'"+c1.getGender()+"' as gender,\n"
                + "'"+c2.getEye_color()+"' as eye_color,\n"
                + "'"+c2.getHair_color()+"' as hair_color,\n"
                + "'"+c1.getFace_expression()+"' as face_expression,\n"
                + "true as is_alive,\n"
                + ""+c1.getBase_attributes_id()+" as base_attributes_id,\n"
                + ""+c1.getCharacter_class_id()+" as character_class_id,\n"
                + "CASE random() < 0.5 WHEN true THEN least(((SELECT level_number FROM getCharacter("+c1.getId()+"))+(SELECT level_number FROM getCharacter("+c2.getId()+"))/2),10) WHEN false THEN greatest(((SELECT level_number FROM getCharacter("+c1.getId()+"))+(SELECT level_number FROM getCharacter("+c2.getId()+"))/2),1) end as level_number\n"
                + "FROM generate_series(1,1) as seq(ida);\n"
                + ""
                + "DELETE FROM game_character WHERE id = "+c1.getId()+";\n"
                + "DELETE FROM game_character WHERE id = "+c2.getId()+";\n"
                + "UPDATE game_character_owns_item SET game_character_id = (SELECT max(id) FROM game_character) WHERE game_character_id = "+c1.getId()+" or game_character_id = "+c2.getId()+";";
        
        } catch (NullPointerException e) {
            System.out.println("Did not find a character/player");
            return false;
        }
        
        try(PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            
            s.executeUpdate();
            //if (callConfirmQuestion().equals("n")) {c.rollback(); return false;}
            
            try {
                /*c1.delete();
                c2.delete();*/
                if (p.getCredit_amount().compareTo(BigDecimal.valueOf(25.0)) < 0) {
                    c.rollback();
                    throw new CallException("Not enough credit");
                }
                else {
                    if (p == null || c1 == null || c2 == null) {
                        c.rollback();
                        throw new CallException("");
                    }
                    else {
                        c.commit();
                        p.pay(BigDecimal.valueOf(25));
                        p.feeHistoryAdd(7);
                    }
                }
            } catch (CallException | NullPointerException e) {
                System.out.println(e.toString());
                return false;
            }
        }
        c.setAutoCommit(true);
        return true;
    }
    
    /*public static String callConfirmQuestion() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Do you want to confirm your choice? (y/n)");
        String answer = br.readLine();
        return answer;
    }*/
    
}
