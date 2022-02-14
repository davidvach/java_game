/**
 *
 * @author vacha
 */
public class MenuPrinter {
    
    /**
     * help method for printing a menu
     */
    
    public void print() {
        StringBuffer sb = new StringBuffer();
        int consoleSize = 176;
        String banner = "MENU";
        for (int i = 0; i < consoleSize-banner.length(); i++) {
            if (sb.length()+banner.length() == consoleSize/2) {sb.append(banner);}
            sb.append("-");
            if (i == consoleSize-banner.length()-1) {sb.append("\n");}
        }
        String[] player = {"1 - Add player","2 - Remove player","3 - Update player","4 - Get data about player","5 - Print list of all characters of player"};
        String[] character = {"6 - Add character to player","7 - Remove character from player","8 - List all items of character","30 - FEE HISTORY: See player's history","18 - SHOP: Buy an item"};
        String[] domainOperations = {"10 - Metamorphosis","11 - Character transfer", "12 - Change appearance", "13 - Bounty Hunter", "14 - Fight simulation"};
        String[] race = {"20 - Get list of races", "21 - Create race", "22 - Add/delete class to race", "23 - Delete race"};
        String[] class_ = {"25 - Get list of classes","26 - Get base information about a class","27 - Create class","28 - Update class","29 - Delete class"};
        String[] stats = {"40 - Top characters (statistics)", "41 - Players order by kills (statistics)"};
        String[] tests = {"100 - Test Level up","101 - Test Add money"};
        //1st row:
        int lines = giveLongestArray(player, character, domainOperations).length;
        for (int i = 0; i < lines; i++) {
            String temp = " |";
            if (i < player.length) {temp += player[i];}
            sb.append(temp);
            for (int j = 0; j < consoleSize/3-temp.length(); j++) {
                sb.append(" ");
            }
             temp = ""; sb.append("|");
            if (i < character.length) {temp = character[i];}
            sb.append(temp);
            for (int j = 0; j < consoleSize/3-temp.length(); j++) {
                sb.append(" ");
            }
            temp = ""; sb.append("|");
            if (i < domainOperations.length) {temp = domainOperations[i];}
            sb.append(temp);
            for (int j = 0; j < consoleSize/3-temp.length(); j++) {
                sb.append(" ");
            }
            sb.append("|"); sb.append("\n");
        }
        for (int i = 0; i < consoleSize; i++) {
            if (i == 0 || i == consoleSize - 1) {sb.append(" ");}
            else { sb.append("-");}
            if (i == consoleSize-1) {sb.append("\n");}
        }
        //2nd row:
        lines = 5; //giveLongestArray manually
        for (int i = 0; i < lines; i++) {
            String temp = " |";
            if (i < race.length) {temp += race[i];}
            sb.append(temp);
            for (int j = 0; j < (consoleSize-1)/4-temp.length(); j++) {
                sb.append(" ");
            }
             temp = ""; sb.append("|");
            if (i < class_.length) {temp = class_[i];}
            sb.append(temp);
            for (int j = 0; j < (consoleSize-1)/4-temp.length(); j++) {
                sb.append(" ");
            }
            temp = ""; sb.append("|");
            if (i < stats.length) {temp = stats[i];}
            sb.append(temp);
            for (int j = 0; j < (consoleSize-1)/4-temp.length(); j++) {
                sb.append(" ");
            }
            temp = ""; sb.append("|");
            if (i < tests.length) {temp = tests[i];}
            sb.append(temp);
            for (int j = 0; j < (consoleSize-1)/4-temp.length(); j++) {
                sb.append(" ");
            }
            sb.append("|"); sb.append("\n");
        }
        for (int i = 0; i < consoleSize; i++) {
            sb.append("-");
            if (i == consoleSize-1) {sb.append("\n");}
        }
        System.out.println(sb);
    }
    
    /**
     * help method to find a longest array
     * @param a1 first array
     * @param a2 second array
     * @param a3 third array
     * @return 
     */
    
    public static String[] giveLongestArray(String[] a1, String[] a2, String[] a3) {
        if (a1.length > a2.length) {
            if (a1.length > a3.length) {
                return a1;
            }
            return a3;
        }
        if (a2.length > a3.length) {
            return a2;
        }
        return a3;
    }
    
}
