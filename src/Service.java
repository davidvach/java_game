
import java.sql.Date;

public class Service {
    private int id;
    private String type;
    private String description;
    private double price;
    private Date date;
    
    public int getId() {return id;}
    public String getType() {return type;}
    public String getDescription() {return description;}
    public double getPrice() {return price;}
    public Date getDate() {return date;}
    public void setId(int val) {id = val;}
    public void setType(String val) {type = val;}
    public void setDescription(String val) {description = val;}
    public void setPrice(double val) {price = val;}
    public void setDate(Date val) {date = val;}
    
    /**
     * provides a string representation of this service
     * @return string containing appropriate data about this service
     */
    
    public String toString() {
        String ret = "";
        ret += "("+getId() + ") "+getType()+" - "+getDescription() + " PRICE: "+getPrice()+" DATE: "+getDate();
        return ret;
    }
}
