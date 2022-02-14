
import static java.lang.Math.round;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FightAndHeal {
    
    private Player player1;
    private Player player2;
    private Character character1;
    private double char1Strength;
    private double char2Strength;
    private double char1Defense;
    private double char2Defense;
    private double char1Current_health;
    private double char1Max_health;
    private double char2Current_health;
    private double char2Max_health;
    private Character character2;
    private final int MOVE_SLEEP = 2000;
    private final double ABL_CHANCE = 0.3;
    
    private ArrayList<Ability> char1abs;
    private ArrayList<Ability> char2abs;
    
    public FightAndHeal(int p1, int p2, int c1, int c2) throws SQLException {
        player1 = Finder_player.getInstance().findById(p1);
        player2 = Finder_player.getInstance().findById(p2);
        character1 = Finder_character.getInstance().findById(c1);
        character2 = Finder_character.getInstance().findById(c2);
        char1abs = new ArrayList<>();
        char2abs = new ArrayList<>();
    }
    
    public void simulateFight() throws CallException, InterruptedException, SQLException {
        if (character1.getIs_alive() == false || character2.getIs_alive() == false) {
            throw new CallException("Some of characters is already dead. Cannot start fight.");
        }
        
        Connection c = DbContext.getConnection();
        c.setAutoCommit(false);
        if (c.getTransactionIsolation() == 0) {
        c.setTransactionIsolation(c.TRANSACTION_SERIALIZABLE);
        }
        
        try {
            loadFunctions();
        } catch (SQLException e) {
            c.rollback();
            throw new CallException("An error occured while loading functions.");
        }
        try {
            setAttributes();
        } catch (SQLException e) {
            c.rollback();
            throw new CallException("An error occured while setting attributes.");
        }
        try {
            loadAbilities();
        } catch (SQLException e) {
            c.rollback();
            throw new CallException("An error occured while loading abilities.");
        }
        
        System.out.println("");
        System.out.println("The fight has started:");
        System.out.println("=========="+character1.getName()+" vs. "+character2.getName()+"==========");
        
        int lastFight = 2; // kto utocil posledny
        
        while (character1.getIs_alive() == true && character2.getIs_alive() == true) {
            
            Random rnd = new Random();
            double strength_change = 0;
            double health_change = 0;
            
            
            
            if (lastFight == 2) {
                //vypocet schopnosti
                double c1ThrowsAbility = rnd.nextDouble();
                Ability c1ABL = null;
                if (c1ThrowsAbility < ABL_CHANCE) {int ab = rnd.nextInt(char1abs.size()); c1ABL = char1abs.get(ab);}

                if (c1ABL != null) {
                    if (c1ABL.getType().equals("ATTACK")) {
                        strength_change = c1ABL.getStrengthFactor();
                    } else {
                        health_change = c1ABL.getStrengthFactor();
                    }
                }
                char2Current_health -= (round(char1Strength+char1Strength*strength_change)-Math.min(char2Defense,round(char1Strength+char1Strength*strength_change)));
                char2Current_health = Math.max(char2Current_health, 0);
                char1Current_health = Math.min(round(char1Current_health+char1Current_health*health_change), char1Max_health);
                String res = char1Current_health+"/"+char1Max_health+" HP       "
                        + ">>>>>>>>>>>"+(char1Strength+char1Strength*strength_change)+"☛     "+char2Current_health+"/"+char2Max_health+" HP";
                if (strength_change != 0) {res += "   ✮ "+character1.getName()+" used "+c1ABL.getName()+" (ATTACK)";}
                if (health_change != 0) {res += "   ✮ "+character1.getName()+" used "+c1ABL.getName()+" (HEALTH)";}
                System.out.println(res);
                lastFight = 1;
            } else {
                //vypocet schopnosti
                double c2ThrowsAbility = rnd.nextDouble();
                Ability c2ABL = null;
                if (c2ThrowsAbility < ABL_CHANCE) {int ab = rnd.nextInt(char2abs.size()); c2ABL = char2abs.get(ab);}
                
                if (c2ABL != null) {
                    if (c2ABL.getType().equals("ATTACK")) {
                        strength_change = c2ABL.getStrengthFactor();
                    } else {
                        health_change = c2ABL.getStrengthFactor();
                    }
                }
                char1Current_health -= (round(char2Strength+char2Strength*strength_change)-Math.min(char1Defense,round(char2Strength+char2Strength*strength_change)));
                char1Current_health = Math.max(char1Current_health, 0);
                char2Current_health = Math.min(round(char2Current_health+char2Current_health*health_change), char2Max_health);
                String res = char1Current_health+"/"+char1Max_health+" HP       "
                        +"☚"+(char2Strength+char2Strength*strength_change)+"<<<<<<<<<<<      "+char2Current_health+"/"+char2Max_health+" HP";
                if (strength_change != 0) {res += "   ✮ "+character2.getName()+" used "+c2ABL.getName()+" (ATTACK)";}
                if (health_change != 0) {res += "   ✮ "+character2.getName()+" used "+c2ABL.getName()+" (HEALTH)";}
                System.out.println(res);
                lastFight = 2;
                
            }
            
            
            if (char1Current_health <= 0) {
                character1.setIs_alive(false);
            }
            if (char2Current_health <= 0) {
                character2.setIs_alive(false);
            }
            Thread.sleep(MOVE_SLEEP);
        }
        try {
            updateAndFinish();
        } catch (SQLException e) {
            c.rollback();
            throw new CallException("Something went wrong with update and finish ---< here will come rollback");
        }
        try {
            announceWinner();
        } catch (SQLException e) {
            c.rollback();
            throw new CallException("Something went wrong with winner announce ROLLBACK LATER");
        }
        c.commit();
        c.setAutoCommit(true);
    }
    
    public void updateAndFinish() throws SQLException {
        /*
        nastavit alive
        pridat xp, porazeny level*25
        zapisat suboj do battle history
        */
        
        if (character1.getIs_alive() == false) {
            character1.setIsDead();
            character2.addXP(character1.getLevel_number()*25);
            player2.addMoney(character1.getLevel_number()*10);
            character2.writeBattleHistory(character1);
        }
        else {
            character2.setIsDead();
            character1.addXP(character2.getLevel_number()*25);
            player1.addMoney(character2.getLevel_number()*10);
            character1.writeBattleHistory(character2);
        }
    }
    
    public void announceWinner() throws SQLException {
        if (character1.getIs_alive() == false) {
            System.out.println("WINNER IS: "+character2.getName()+". He got "+(character1.getLevel_number()*25)+"XP and "
                    +(character1.getLevel_number()*10)+" credit.");
        }
        else {
            System.out.println("WINNER IS: "+character1.getName()+". He got "+(character2.getLevel_number()*25)+"XP and "
                    +(character2.getLevel_number()*10)+" credit.");
        }
    }
    
    public void loadFunctions() throws SQLException {
        String sql1 = "CREATE OR REPLACE FUNCTION getStrength(num int)\n"
                    + "RETURNS numeric LANGUAGE sql AS\n"
                    + "$$\n"
                    + "SELECT strength FROM base_attributes as b JOIN game_character as c ON c.base_attributes_id = b.id WHERE c.id = num;\n"
                    + "$$;";
        String sql2 = "CREATE OR REPLACE FUNCTION getTotalStrength(num int)\n"
                    + "RETURNS numeric LANGUAGE sql AS\n"
                    + "$$\n"
                    + "SELECT sum(w.strength_manipulator) FROM game_character_owns_item as ci JOIN items as i ON ci.item_id = i.id "
                    + "JOIN weapon as w ON w.id = i.weapon_id WHERE ci.game_character_id = num;\n"
                    + "$$;";
        String sql3 = "CREATE OR REPLACE FUNCTION getStrengthPerRound(num int)\n"
                    + "RETURNS numeric AS $$\n"
                    + "DECLARE result numeric;\n"
                    + "BEGIN\n"
                    + "CASE WHEN (SELECT * FROM getTotalStrength(num)) is null THEN result = "
                    + "(SELECT getStrength(num)); ELSE result = (SELECT getStrength(num)+getTotalStrength(num)*getStrength(num)); END CASE;\n"
                    + "RETURN result;\n"
                    + "END; $$"
                    + "LANGUAGE plpgsql;";
        String sql4 = "CREATE OR REPLACE FUNCTION getDefense(num int)\n"
                    + "RETURNS numeric LANGUAGE sql AS\n"
                    + "$$\n"
                    + "SELECT defense FROM base_attributes as b JOIN game_character as c ON c.base_attributes_id = b.id WHERE c.id = num;\n"
                    + "$$;";
        String sql5 = "CREATE OR REPLACE FUNCTION getTotalDefense(num int)\n"
                    + "RETURNS numeric LANGUAGE sql AS\n"
                    + "$$\n"
                    + "SELECT sum(a.defense_manipulator) FROM game_character_owns_item as ci JOIN items as i ON ci.item_id = i.id "
                    + "JOIN armor as a ON a.id = i.armor_id WHERE ci.game_character_id = num;\n"
                    + "$$;";
        String sql6 = "CREATE OR REPLACE FUNCTION getDefensePerRound(num int)\n"
                    + "RETURNS numeric AS $$\n"
                    + "DECLARE result numeric;\n"
                    + "BEGIN\n"
                    + "CASE WHEN (SELECT * FROM getTotalDefense(num)) is null THEN result = "
                    + "(SELECT getDefense(num)); ELSE result = (SELECT getDefense(num)+getTotalDefense(num)*getDefense(num)); END CASE;\n"
                    + "RETURN result;\n"
                    + "END; $$"
                    + "LANGUAGE plpgsql;";
        String sql7 = "CREATE OR REPLACE FUNCTION getHealth(num int)\n"
                    + "RETURNS numeric LANGUAGE sql AS\n"
                    + "$$\n"
                    + "SELECT health FROM base_attributes as b JOIN game_character as c ON c.base_attributes_id = b.id WHERE c.id = num;\n"
                    + "$$;";
        String sql8 = "CREATE OR REPLACE FUNCTION getTotalHealth(num int)\n"
                    + "RETURNS numeric LANGUAGE sql AS\n"
                    + "$$\n"
                    + "SELECT sum(r.health_manipulator) FROM game_character_owns_item as ci JOIN items as i ON ci.item_id = i.id "
                    + "JOIN ring as r ON r.id = i.ring_id WHERE ci.game_character_id = num;\n"
                    + "$$;";
        String sql9 = "CREATE OR REPLACE FUNCTION getMaxHealth(num int)\n"
                    + "RETURNS numeric AS $$\n"
                    + "DECLARE result numeric;\n"
                    + "BEGIN\n"
                    + "CASE WHEN (SELECT * FROM getTotalHealth(num)) is null THEN result = "
                    + "(SELECT current_health FROM game_character WHERE id = num); ELSE result = (SELECT current_health FROM game_character WHERE id = num)+getTotalHealth(num)*(SELECT current_health FROM \n"
                    + "game_character WHERE id = num); END CASE;\n"
                    + "RETURN result;\n"
                    + "END; $$"
                    + "LANGUAGE plpgsql;";
        
        String sql10 = "CREATE OR REPLACE FUNCTION getAbilities(num int)\n"
                + "RETURNS TABLE (id integer, name varchar, description varchar, strength_factor numeric, attack_id integer, heal_id integer) LANGUAGE sql AS\n"
                + "$$\n"
                + "SELECT a.id, a.name, a.description, a.strength_factor, a.attack_id, a.heal_id FROM game_character as c JOIN "
                + "character_class as class ON c.character_class_id = class.id JOIN ability_belongs_class as abc ON abc.character_class_id = "
                + "class.id JOIN abilities as a ON abc.ability_id = a.id WHERE c.id = num;\n"
                + "$$;";
            
            //zo vsetkych funkcii budem pri boji pouzivat len getStrengthPerRound, getDefensePerRound a getMaxHealth (aktualne zdravie so vsetkym)
            
            String[] sqls = {sql1,sql2,sql3,sql4,sql5,sql6,sql7,sql8,sql9,sql10};
            for (String str : sqls) {
                try (PreparedStatement s = DbContext.getConnection().prepareStatement(str)) {
                    s.executeUpdate();
                }
            }
    }
    
    public void setAttributes() throws SQLException {
        String sql1 = "SELECT * FROM getStrengthPerRound(?);";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql1)) {
            s.setInt(1, character1.getId());
            try (ResultSet rs = s.executeQuery()) {
                while(rs.next() == true) {
                    char1Strength = rs.getDouble("getstrengthperround");
                }
            }
        }
        String sql2 = "SELECT * FROM getDefensePerRound(?);";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql2)) {
            s.setInt(1, character1.getId());
            try (ResultSet rs = s.executeQuery()) {
                while(rs.next() == true) {
                    char1Defense = rs.getDouble("getdefenseperround");
                }
            }
        }
        String sql3 = "SELECT * FROM getMaxHealth(?);";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql3)) {
            s.setInt(1, character1.getId());
            try (ResultSet rs = s.executeQuery()) {
                while(rs.next() == true) {
                    char1Current_health = rs.getDouble("getmaxhealth");
                    char1Max_health = rs.getDouble("getmaxhealth");
                }
            }
        }
        //pre character 2:
        String sql4 = "SELECT * FROM getStrengthPerRound(?);";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql4)) {
            s.setInt(1, character2.getId());
            try (ResultSet rs = s.executeQuery()) {
                while(rs.next() == true) {
                    char2Strength = rs.getDouble("getstrengthperround");
                }
            }
        }
        String sql5 = "SELECT * FROM getDefensePerRound(?);";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql5)) {
            s.setInt(1, character2.getId());
            try (ResultSet rs = s.executeQuery()) {
                while(rs.next() == true) {
                    char2Defense = rs.getDouble("getdefenseperround");
                }
            }
        }
        String sql6 = "SELECT * FROM getMaxHealth(?);";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql6)) {
            s.setInt(1, character2.getId());
            try (ResultSet rs = s.executeQuery()) {
                while(rs.next() == true) {
                    char2Current_health = rs.getDouble("getmaxhealth");
                    char2Max_health = rs.getDouble("getmaxhealth");
                }
            }
        }
    }
    
    public void loadAbilities() throws SQLException {
        //character1:
        String sql = "SELECT * FROM getAbilities(?);";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql)) {
            s.setInt(1, character1.getId());
            try (ResultSet rs = s.executeQuery()) {
                while(rs.next() == true) {
                    Ability a = new Ability();
                    a.setId(rs.getInt("id"));
                    a.setName(rs.getString("name"));
                    a.setDescription(rs.getString("description"));
                    a.setStrengthFactor(rs.getDouble("strength_factor"));
                    a.setAttackId(rs.getInt("attack_id"));
                    a.setHealId(rs.getInt("heal_id"));
                    
                    char1abs.add(a);
                }
            }
        }
        //character2:
        String sql2 = "SELECT * FROM getAbilities(?);";
        try (PreparedStatement s = DbContext.getConnection().prepareStatement(sql2)) {
            s.setInt(1, character2.getId());
            try (ResultSet rs = s.executeQuery()) {
                while(rs.next() == true) {
                    Ability a = new Ability();
                    a.setId(rs.getInt("id"));
                    a.setName(rs.getString("name"));
                    a.setDescription(rs.getString("description"));
                    a.setStrengthFactor(rs.getDouble("strength_factor"));
                    a.setAttackId(rs.getInt("attack_id"));
                    a.setHealId(rs.getInt("heal_id"));
                    
                    char2abs.add(a);
                }
            }
        }
    }
    
}
