
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vacha
 */
public class Item {
    
    private int id;
    private String name;
    private String description;
    private int armor_id;
    private int weapon_id;
    private int ring_id;
    private int price;
    
    public void setId(int num) {id = num;}
    public void setName(String ret) {name = ret;}
    public void setDescription(String ret) {description = ret;}
    public void setArmor_id(int num) {armor_id = num;}
    public void setWeapon_id(int num) {weapon_id = num;}
    public void setRing_id(int num) {ring_id = num;}
    public void setPrice(int num) {price = num;}
    public int getId() {return id;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public int getArmor_id() {return armor_id;}
    public int getWeapon_id() {return weapon_id;}
    public int getRing_id() {return ring_id;}
    public int getPrice() {return price;}
    
    /**
     * provides string representation of a race
     * @return string containing appropriate data about this race
     */
    
    public String toString() {
        String res = "";
        res = "ID: "+getId()+" NAME: "+getName()+" DESCRIPTION: "+getDescription()+" ";
        try {
            if (getArmor_id() != 0) {
                res += "TYPE: armor EFFECT: +"+(getValueManipulator()*100)+"% DEFENSE ";
            }
            if (getWeapon_id() != 0) {
                res += "TYPE: weapon EFFECT: +"+(getValueManipulator()*100)+"% STRENGTH ";
            }
            if (getRing_id() != 0) {
                res += "TYPE: ring EFFECT: +"+(getValueManipulator()*100)+"% HEALTH ";
            }
        } catch (SQLException e) {
            System.out.println("!!! Something went wrong !!!");
        }
        res += "(BASE PRICE = "+getPrice()+")";
        return res;
    }
    
    /**
     * provides value affected by this item
     * @return float of a value
     * @throws SQLException 
     */
    
    public double getValueManipulator() throws SQLException {
        String sql = "";
        String itemType = "";
        if (getArmor_id() != 0) {sql = "SELECT defense_manipulator FROM armor WHERE id = "+getArmor_id(); itemType = "armor";}
        else {
            if (getWeapon_id() != 0) {sql = "SELECT strength_manipulator FROM weapon WHERE id = "+getWeapon_id(); itemType = "weapon";}
            else {sql = "SELECT health_manipulator FROM ring WHERE id = "+getRing_id(); itemType = "ring";}
        }
        /*switch (itemType) {
            case "armor":    
                ;break;
            case "weapon":   ;break;
            case "ring":     ;break;
        }*/
        try(Statement s = DbContext.getConnection().createStatement()) {
            try(ResultSet rs = s.executeQuery(sql)) {
                while (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        
        
        return -1;
    }
}
