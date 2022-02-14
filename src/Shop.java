
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vacha
 */
public class Shop {
    List<Item> offer;
    
    /**
     * constructor for a shop, handle all current items in a database
     * @throws SQLException 
     */
    
    public Shop() throws SQLException {
        String sql = "SELECT id FROM items";
        List<Item> res = new ArrayList<Item>();
        try(Statement s = DbContext.getConnection().createStatement()) {
            try(ResultSet rs = s.executeQuery(sql)) {
                while (rs.next()) {
                    Item i = Finder_item.getInstance().findById(rs.getInt(1));
                    res.add(i);
                }
            }
        }
        offer = res;
    }
    
    /**
     * prints a shop structure with all items in it
     * @return 
     */
    
    public String toString() {
        String res = "-------------------------------------------------------------------------------------------"
                + "SHOP---------------------------------------------------------------------------------\n ";
        String[] signs = {"★ARMORS★","★WEAPONS★","★RINGS★"};
        for (int i = 0; i < signs.length; i++) {
            for (int j = 0; j < (58-signs[i].length())/2; j++) {
                res += " ";
            }
            res += signs[i];
            for (int j = 0; j < (58-signs[i].length())/2; j++) {
                res += " ";
            }
            if (i != signs.length) {res += "|";}
        }
        res += "\n ";
        List<Item> armors = new ArrayList<>();
        List<Item> weapons = new ArrayList<>();
        List<Item> rings = new ArrayList<>();
        for (int i = 0; i < offer.size(); i++) {
            if (offer.get(i).getArmor_id() != 0) {
                armors.add(offer.get(i));
            }
            if (offer.get(i).getWeapon_id()!= 0) {
                weapons.add(offer.get(i));
            }
            if (offer.get(i).getRing_id()!= 0) {
                rings.add(offer.get(i));
            }
        }
        String temp = "";  //max is 58 (length)
        try {
        for (int i = 0; i < 5; i++) { //5 is a constant representing number of items of each type
            temp = "("+armors.get(i).getId()+") "+armors.get(i).getName()+" | +"+(armors.get(i).getValueManipulator()*100)+"% DEFENSE (costs "+armors.get(i).getPrice()+")";
            res += temp;
            for (int j = 0; j < 58-temp.length(); j++) {
                res += " ";
            }
            res += "| ";
            temp = "("+weapons.get(i).getId()+") "+weapons.get(i).getName()+" | +"+(weapons.get(i).getValueManipulator()*100)+"% STRENGTH (costs "+weapons.get(i).getPrice()+")";
            res += temp;
            for (int j = 0; j < 58-temp.length(); j++) {
                res += " ";
            }
            res += "| ";
            temp = "("+rings.get(i).getId()+") "+rings.get(i).getName()+" | +"+(rings.get(i).getValueManipulator()*100)+"% HEALTH (costs "+rings.get(i).getPrice()+")";
            res += temp;
            res += "\n ";
        }
        }
        catch (SQLException e) {
            System.out.println("!!! Something went wrong !!!");
        }
        
        
        return res;
    }
    
    /**
     * perfoms shopping process
     * @throws SQLException
     * @throws IOException 
     */
    
    public void shoppingProcess() throws SQLException, IOException {
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Select ID of player you would like to buy an item for: ");
        int playerId = Integer.parseInt(br.readLine());
        Player p;
        p = Finder_player.getInstance().findById(playerId);
        if (p == null) {
            System.out.println("!!! Did not found a player !!!");
            System.out.print("ENTER to continue...");
            br.readLine();
        }
        else {
            System.out.println(this);
            System.out.println("Check out our offer. ENTER to continue shopping...");
            br.readLine();
            List<Character> characters = p.getListOfAllCharacters();
            List<Integer> charIds = new ArrayList<>();
            if (characters.size() == 0) {
                System.out.println("You don't have any character to buy an item for. You can add new character if you wish.");
                for (Character c : characters) {
                    charIds.add(c.getId());
                }
            }
            else {
                System.out.println("Now, select one of your characters you want to buy a new item for:");
                for (int i = 0; i < characters.size(); i++) {
                    System.out.println(characters.get(i));
                }
                Character c;
                System.out.print("Type his ID: ");
                c = Finder_character.getInstance().findById(Integer.parseInt(br.readLine()));
                boolean containId = false;
                for (int i : charIds) {
                    if (c.getId() == i) {
                        containId = true;
                    }
                }
                if (!containId) {
                    System.out.println("!!! You don't own this character !!!");
                    br.readLine();
                    return;
                }
                if (true == false && contains(characters, c.getId()) == false) {
                    System.out.println("!!! You don't own this character !!!");
                    System.out.print("ENTER to continue...");
                    br.readLine();
                }
                else {
                    Item i;
                    System.out.print("Select ID of item: ");
                    i = Finder_item.getInstance().findById(Integer.parseInt(br.readLine()));
                    if (i == null) {
                        System.out.println("!!! Wrong item selected !!!");
                        br.readLine();
                    }
                    else {
                        if (BigDecimal.valueOf(i.getPrice()).compareTo(p.getCredit_amount()) > 0) {
                            System.out.println("We're sorry, but you don't have enough credit for this item. Win some fights to get or buy more credit.");
                            System.out.println("You have "+p.getCredit_amount()+" credit and this item costs "+i.getPrice()+" credit.");
                            System.out.print("ENTER to continue...");
                            br.readLine();
                        }
                        else {
                            System.out.print("Do you want to see a description of this item? (Y/N) ");
                            String in = br.readLine();
                            if (in.equalsIgnoreCase("Y")) {
                                System.out.println(""+i.getName()+" | "+i.getDescription());
                            }
                            System.out.print("Do you want to confirm your purchase of item "+i.getName()+" for "+c.getName()+"? (Y/N) ");
                            String input = br.readLine();
                            if (input.equalsIgnoreCase("N")) {
                                System.out.println("Process has been canceled. You can come back later.");
                                System.out.print("ENTER to continue...");
                                br.readLine();
                            }
                            else {
                                c.addItem(i.getId());
                                p.pay(BigDecimal.valueOf(i.getPrice()));
                                System.out.println("Congratulations! Your character "+c.getName()+" now owns "+i.getName()+". Your remaining credit is "+p.getCredit_amount()+".");
                                System.out.print("ENTER to continue...");
                                br.readLine();
                            }
                        }
                    }
                }
            }
        }
        
    }
    
    private boolean contains(List<Character> array, int id) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getId() == i) {
                return true;
            }
        }
        return false;
    }
    
}
