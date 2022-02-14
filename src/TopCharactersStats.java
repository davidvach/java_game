
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TopCharactersStats {
    
    /**
     * perfoms top characters stats operation
     * @throws SQLException 
     */
    
    public static void evaluate() throws SQLException {
        String sql0 = "CREATE OR REPLACE FUNCTION getNthWeekKillerSince(since TIMESTAMP)\n"
                + "RETURNS TABLE (kills bigint, id integer, since TIMESTAMP, until TIMESTAMP) LANGUAGE sql AS\n"
                + "$$\n"
                + "SELECT res.vyhry, res.idecko, since, since + INTERVAL '7 days' FROM (SELECT game_character_id as idecko, count(game_character_id) as vyhry "
                + "FROM battle_history WHERE result_state = 'WINNER' and date_battle BETWEEN since AND since + INTERVAL '7 days' "
                + "GROUP BY game_character_id ORDER BY vyhry DESC LIMIT 1) as res;\n"
                + "$$;\n"
                + ""
                + "CREATE OR REPLACE FUNCTION getLoosersById(_id integer, since TIMESTAMP, until TIMESTAMP)\n"
                + "RETURNS TABLE (id integer) LANGUAGE sql AS\n"
                + "$$\n"
                + "SELECT opponent_id FROM battle_history as b WHERE b.game_character_id = _id AND date_battle BETWEEN since AND until AND result_state = 'WINNER';\n"
                + "$$;\n"
                + ""
                + "CREATE OR REPLACE FUNCTION getKilledMalesAndFemalesBy(_id integer, since TIMESTAMP, until TIMESTAMP)\n"
                + "RETURNS TABLE (males bigint, females bigint) LANGUAGE sql AS\n"
                + "$$\n"
                + "SELECT (SELECT count(*) FROM getLoosersById(_id, since, until) as c "
                + "JOIN game_character as gc ON c.id = gc.id WHERE gender = 'MALE') as males, "
                + "(SELECT count(*) FROM getLoosersById(_id, since, until) as c JOIN game_character as gc ON c.id = gc.id WHERE gender = 'FEMALE') as females;\n"
                + "$$;\n"
                + ""
                + "CREATE OR REPLACE FUNCTION getNthMonthKillerSince(since TIMESTAMP)\n"
                + "RETURNS TABLE (kills bigint, id integer, since TIMESTAMP, until TIMESTAMP) LANGUAGE sql AS\n"
                + "$$\n"
                + "SELECT getNthWeekKillerSince(since) UNION SELECT getNthWeekKillerSince(since+INTERVAL '7 days') "
                + "UNION SELECT getNthWeekKillerSince(since+INTERVAL '14 days') UNION SELECT getNthWeekKillerSince(since+INTERVAL '21 days');\n"
                + "$$;\n"
                + ""
                + "CREATE OR REPLACE FUNCTION getLast3Months()\n"
                + "RETURNS TABLE (kills bigint, id integer, since TIMESTAMP, until TIMESTAMP)\n"
                + "LANGUAGE sql AS\n"
                + "$$\n"
                + "SELECT getNthMonthKillerSince((NOW()-INTERVAL '28 days')::TIMESTAMP) UNION "
                + "SELECT getNthMonthKillerSince((NOW()-INTERVAL '56 days')::TIMESTAMP) UNION "
                + "SELECT getNthMonthKillerSince((NOW()-INTERVAL '84 days')::TIMESTAMP);\n"
                + "$$;\n"
                + ""
                + "";
        
        try (PreparedStatement s0 = DbContext.getConnection().prepareStatement(sql0)) {
            s0.executeUpdate();
        }
        
        
        String sql = "SELECT m.id, m.kills, (SELECT males FROM getKilledMalesAndFemalesBy(m.id, m.since, m.until)), "
                + "(SELECT females FROM getKilledMalesAndFemalesBy(m.id, m.since, m.until)), "
                + "m.since::DATE, m.until::DATE FROM getLast3Months() as m ORDER BY m.since DESC;";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            try (ResultSet rs = s.executeQuery()) {
                int week = 1; int month = 1; String newMonth = "Results for month number "+month+":";
                while(rs.next() == true) {
                    if (newMonth.equals("")) {System.out.print(newMonth); newMonth = "";}
                    else {System.out.println(newMonth); newMonth = "";}
                    System.out.println("    Week "+week+":");
                    System.out.println("        Winner is ID "+rs.getInt("id")+" with "+rs.getBigDecimal("kills")+" kills, "
                            +rs.getBigDecimal("males")+" males and "+rs.getBigDecimal("females")+" females.");
                    week++;
                    if (week == 5) {
                        month++;
                        week = 1;
                        newMonth = "Results for month number "+month+":";
                    }
                }
            }
        }
    }
    
    /*
    takze mam funkciu getLast3Months, ta vrati 12 riadkov, prve 4 su posledny mesiac, druhe 4 predp. atd., kazdy riadok je jeden
    tyzden a ja sa viem pre kazdy riadok spytat KTO je killer a jeho since until, toto je vstup do funkcie getKilledMalesAndFemalesBy,
    ktora vrati pocet nim zabitych males and females za dane casove obdobie
    do funkcie getLast3Months vstupovat pomocou order by SELECT * FROM getLast3Months() ORDER BY since DESC;
    */
    
    
}
