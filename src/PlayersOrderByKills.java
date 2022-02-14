
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayersOrderByKills {
    
    /**
     * perfoms statistics analysing
     * @throws SQLException 
     */
    
    public static void evaluate() throws SQLException {
        String sql = "CREATE OR REPLACE FUNCTION getSumKillsPerPlayer()\n"
                + "RETURNS TABLE (id integer, kills bigint) LANGUAGE sql AS\n"
                + "$$\n"
                + "SELECT p.id, (SELECT count(*) FROM player as p2 JOIN game_character as c ON p2.id = c.player_id "
                + "JOIN battle_history as bh ON bh.game_character_id = c.id WHERE p2.id = p.id AND bh.result_state = 'WINNER' "
                + "AND date_battle BETWEEN NOW()-INTERVAL '7 days' AND NOW()) as kills FROM player as p ORDER BY kills DESC;\n"
                + "$$;\n"
                + ""
                + "CREATE OR REPLACE FUNCTION getPercentil(_id integer)\n"
                + "RETURNS numeric LANGUAGE sql AS\n"
                + "$$\n"
                + "SELECT ((SELECT count(*) FROM getSumKillsPerPlayer() as s WHERE s.kills < ROUND((SELECT kills FROM getSumKillsPerPlayer() as temp "
                + "WHERE temp.id = _id)::numeric,2)) / ROUND((100000)::numeric,2)) * 100;\n"  //SELECT count(*) FROM player
                + "$$;\n"
                + ""
                + "CREATE OR REPLACE FUNCTION getDaysWithKills(_id integer)\n"
                + "RETURNS TABLE (id integer, day double precision) LANGUAGE sql AS\n"
                + "$$\n"
                + "SELECT p.id, date_part('day',b.date_battle) FROM player as p JOIN game_character as c ON p.id = c.player_id "
                + "JOIN battle_history as b ON b.game_character_id = c.id WHERE b.result_state = 'WINNER' and "
                + "date_battle BETWEEN NOW()-INTERVAL '7 days' AND NOW() AND p.id = _id;\n"
                + "$$;\n"
                + ""
                + "CREATE OR REPLACE FUNCTION getMostFrequentDay(_id integer)\n"
                + "RETURNS TABLE (day double precision, count bigint) LANGUAGE sql AS\n"
                + "$$\n"
                + "SELECT day, count(day) FROM getDaysWithKills(_id) GROUP BY day ORDER BY count(day) DESC LIMIT 1;\n"
                + "$$;\n"
                + ""
                + "CREATE OR REPLACE FUNCTION getHoursWithKills(_id integer)\n"
                + "RETURNS TABLE (id integer, hour double precision) LANGUAGE sql AS\n"
                + "$$\n"
                + "SELECT p.id, date_part('hour',b.date_battle) FROM player as p JOIN game_character as c ON p.id = c.player_id \n"
                + "JOIN battle_history as b ON b.game_character_id = c.id WHERE b.result_state = 'WINNER' \n"
                + "and date_battle BETWEEN NOW()-INTERVAL '7 days' AND NOW() AND p.id = _id;\n"
                + "$$;\n"
                + ""
                + "CREATE OR REPLACE FUNCTION getMostFrequentHour(_id integer)\n"
                + "RETURNS TABLE (hour double precision, count bigint) LANGUAGE sql AS\n"
                + "$$\n"
                + "SELECT hour, count(hour) FROM getHoursWithKills(_id) GROUP BY hour ORDER BY count(hour) DESC LIMIT 1;\n"
                + "$$;\n"
                + ""
                + "";
        
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.executeUpdate();
        }
        
        String sql2 = "SELECT s.id, s.kills, ROUND((getPercentil(s.id)),2) as percentil, MOD((SELECT day FROM getMostFrequentDay(s.id))::integer,7)+1 as day, "
                + "(SELECT hour FROM getMostFrequentHour(s.id)) as hour FROM getSumKillsPerPlayer() as s;";
        
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql2)) {
            try (ResultSet rs = s.executeQuery()) {
                double curr = 0; double prev = 0;
                while(rs.next() == true) {
                    curr = rs.getDouble("percentil");
                    if (curr < 90 && prev >= 90) {System.out.println("----------- Player with percentil 90 and less -----------");}
                    System.out.println("Player "+rs.getInt("id")+" | Kills "+rs.getBigDecimal("kills")+" | Percentil "+rs.getDouble("percentil")+" | "
                            + "Most frequent day "+rs.getInt("day")+" | Most frequent hour "+rs.getInt("hour"));
                    prev = curr;
                }
            }
        }
    }
    
}
