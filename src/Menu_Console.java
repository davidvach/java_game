
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.sql.Statement;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vacha
 */
public class Menu_Console {
    
    private boolean exit;
    
    public void run() throws IOException, SQLException, Exception { //metoda prevzata zo vzoroveho projektu
        exit = false;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        while (exit == false) {
            System.out.println();
            MenuPrinter mp = new MenuPrinter();
            mp.print();
            System.out.println();

            String line = br.readLine();
            if (line == null) {
                return;
            }

            System.out.println();

            command(line);
        }
        
    }
    
    public void exit() {
        exit = true;
    }
    
    public void command(String line) throws IOException, SQLException, Exception { //metoda prevzata zo vzoroveho projektu
        try {
            switch (line) {
                case "1":   addPlayer(); break;
                case "2":   removePlayer(); break;
                case "3":   updatePlayer(); break;
                case "4":   getDataAboutPlayer(); break;
                case "5":   printListOfAllCharacters(); break;
                case "6":   addCharacterToPlayer(); break;
                case "7":   removeCharacterFromPlayer(); break;
                case "8":   printListOfAllItemsOfCharacter(); break;
                case "10":  metamorphosis(); break;
                case "11":  characterTransfer(); break;
                case "12":  changeAppearance(); break;
                case "13":  bountyHunter(); break;
                case "14":  fightAndHeal(); break;
                case "18":  visitShop(); break;
                case "40":  printTopCharacters(); break;
                case "41":  playersOrderByKills(); break;
                case "20":  printListOfAllRaces(); break;
                case "21":  addRace(); break;
                case "22":  classToRaceManipulate(); break;
                case "23":  removeRace(); break;
                case "25":  printListOfClasses(); break;
                case "26":  getDataAboutClass(); break;
                case "27":  addClass(); break;
                case "28":  updateClass(); break;
                case "29":  removeClass(); break;
                case "30":  printFeeHistory(); break;
                case "100": testLevelUp(); break;
                case "101": testAddMoney(); break;
                default:    System.out.println("Unknown option"); break;
            }
        } catch(SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * adds player into database
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void addPlayer() throws IOException, SQLException, Exception {
        
        Player p = new Player();
        p.setCredit_amount(BigDecimal.valueOf(50));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Select your name: ");
        p.setUsername(br.readLine());
        System.out.println("");
        System.out.print("Select your password: ");
        p.setPassword(br.readLine());
        System.out.println("");
        System.out.print("Put your email: ");
        try {
            p.setEmail(br.readLine());
            System.out.println("");
            p.insert();
            waitForResponse(br, "You've succesfully added new player with ID "+p.getId()+" to this game.\nENTER to continue...");
            run();
        } catch(SQLException e) {
            p.delete();
            waitForResponse(br, "!!! Email does not have correct form !!!");
            run();
        }
    }
    
    /**
     * remove player from a database
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void removePlayer() throws IOException, SQLException, Exception {
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Select player's ID: ");
        int playerId = Integer.parseInt(br.readLine());
        
        Player p;
        p = Finder_player.getInstance().findById(playerId);
        
        try {
            if (p == null) {
                throw new CallException("!!! Did not find a player !!!");
            }
            else {
                p.delete();
                waitForResponse(br, "You've succesfully deleted player "+p.getUsername()+" from this game.\nENTER to continue...");
                run();
            }
        } catch (CallException e) {
            System.out.println(e.toString());
        }
    }
    
    /**
     * prints data about a player
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void getDataAboutPlayer() throws IOException, SQLException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Select ID of player you want to get info about: ");
        int playerId = Integer.parseInt(br.readLine());
        Player p;
        p = Finder_player.getInstance().findById(playerId);
        if (p == null) {
            try {
                throw new CallException("!!! Did not find a player !!!");
            } catch (CallException e) {
                System.out.println(e.toString());
            }
        }
        else {
            //tu bude vypis udajov o konkretnom player, jeho historii transakcii, postav (a ci su nazive a ich levelov)
            //potom pridat este podobnu metodu na vypis vsetkych hracov, s okresanymi udajmi (len pocet postav, z toho x zivych napr.)
            System.out.println("-------------------------------------");
            System.out.println(p);
            //pridat vypis dalsich udajov po spraveni tried fee_history a pod.
            waitForResponse(br, "ENTER to continue...");
            run();
        }
        
    }
    
    /**
     * prints list of all characters of a specific player
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void printListOfAllCharacters() throws IOException, SQLException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Select ID of player you want to get a list of characters of: ");
        int playerId = Integer.parseInt(br.readLine());
        if (Finder_player.getInstance().findById(playerId) == null) {
            waitForResponse(br, "!!! Did not find a player !!!");
            run();
        }
        System.out.println("");
        System.out.println("LIST OF CHARACTERS OF PLAYER WITH ID "+playerId);
        if (getListOfAllCharacters(playerId).size() == 0) {
            waitForResponse(br, "This player does not own any character right now. ENTER to continue...");
            run();
        }
        else { 
            for (int i = 0; i < getListOfAllCharacters(playerId).size(); i++) {
                System.out.println("CHARACTER "+(i+1)+": "+getListOfAllCharacters(playerId).get(i));
            }
            waitForResponse(br, "ENTER to continue...");
            run();
        }
    }
    
    /**
     * returns list of all characters of a specific player
     * @param ownerId
     * @return list of all characters of a specific player
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
     
    public List<Character> getListOfAllCharacters(int ownerId) throws IOException, SQLException, Exception {
        Player p;
        p = Finder_player.getInstance().findById(ownerId);
        augmentPlayer(p);
        if (p == null) {
            return new ArrayList<Character>();
        }
        return p.getListOfAllCharacters();
    }

    private void augmentPlayer(Player p) throws SQLException{
        if (p.getId() > 10) {
            p.setEmail("kunzo6@gmail.com");
        }
        if (p.getUsername().startsWith("C")) {
            p.setPassword("Password123");
        }
    }
    
    /**
     * updates information about a player
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void updatePlayer() throws IOException, SQLException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Select player's ID: ");
        int playerId = Integer.parseInt(br.readLine());
        
        Player p;
        p = Finder_player.getInstance().findById(playerId);
        if (p == null) {
            try {
                throw new CallException("!!! Did not find a player !!!");
            } catch (CallException e) {
                System.out.println(e.toString());
                return;
            }
        }
        System.out.print("Select new password or 'N' if current remains: ");
        String output = br.readLine();
        if (output.equalsIgnoreCase("N") == false) {
            p.setPassword(output);
        }
        System.out.println("");
        System.out.print("Select new email or 'N' if current remains: ");
        output = br.readLine();
        if (output.equalsIgnoreCase("N") == false) {
            p.setEmail(output);
        }
        System.out.println("");
        try {
        p.update();
        waitForResponse(br, "You've succesfully updated player "+p.getUsername()+" in this game.\nENTER to continue...");
        run();
        } catch (SQLException e) {
            System.out.println("!!! Wrong data !!!");
            waitForResponse(br, "ENTER to continue...");
            run();
        }

    }


    /**
     * help method to handle response from user
     * @param br reader
     * @param ret string to print into console
     * @throws IOException
     * @throws Exception 
     */
    
    public void waitForResponse(BufferedReader br, String ret) throws IOException, Exception {
        System.out.print(ret);
        br.readLine();
    }
    
    /**
     * handle user input for metamorphose
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void metamorphosis() throws IOException, SQLException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Select ID of a player you would like to take an action with (cost 25 credits): ");
        int playerId = Integer.parseInt(br.readLine());
        Player p = Finder_player.getInstance().findById(playerId);
        if (p == null) {
            try {
                throw new CallException("!!! Did not find a player !!!");
            }catch (CallException e) {
                System.out.println(e);
                run();
            }
        }
        List<Character> characters = p.getListOfAllCharacters();
        System.out.println("You own "+characters.size()+" characters. See their information below:");
        int[] ids = new int[characters.size()];
        for (int i = 0; i < characters.size(); i++) {
            System.out.println(characters.get(i));
            ids[i] = characters.get(i).getId();
        }
        if (characters.size() < 2) {
            try {
                throw new CallException("We cannot continue. You do not own enough characters for metamorphose. You can create new character. ENTER to continue...");
            } catch (CallException e) {
                System.out.println(e);
                run();
            }
        }
        System.out.println("Now, select ID's of 2 of your characters listed above you want to merge.");
        int ch1 = Integer.parseInt(br.readLine());
        int ch2 = Integer.parseInt(br.readLine());
        boolean containId1 = false, containId2 = false;
        for (int i : ids) {
            if (i == ch1) {
                containId1 = true;
            }
            if (i == ch2) {
                containId2 = true;
            }
        }
        if (containId1 == false || containId2 == false) {
            waitForResponse(br, "!!! You don't own this character !!!");
            run();
        }
        boolean result = Metamorphosis.metamorphose(ch1,ch2,playerId);
        if (result) {waitForResponse(br, "Congratulations! New character appeared in your arena."+"\nENTER to continue...");
                    run();
        } else {
            waitForResponse(br, "ENTER to continue...");
            run();
        }
    }
    
    public static boolean contains(int[] array, int num) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == num) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * adds character to a specific player
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void addCharacterToPlayer() throws IOException, SQLException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Character c = new Character();
        System.out.print("Select ID of player you want to add a character to: ");
        int playerId = Integer.parseInt(br.readLine());
        Player p;
        p = Finder_player.getInstance().findById(playerId);
        if (p == null) {
            try {
                throw new CallException("!!! Did not find a player !!!");
            } catch (CallException e) {
                System.out.println(e);
                return;
            }
        }
        c.setPlayer_id(playerId);
        System.out.print("Select name of your character: ");
        c.setName(br.readLine());
        System.out.print("Select his gender (1=MALE/2=FEMALE): ");
        int gender = Integer.parseInt(br.readLine());
        if (gender != 1 && gender != 2) {
            try {
                throw new CallException("!!! Wrong input !!!");
            } catch (CallException e) {
                System.out.println(e);
                return;
            }
        }
        if (gender == 1) {c.setGender("MALE");}
        else {c.setGender("FEMALE");}
        System.out.print("Select a color of his eyes (1=blue/2=black/3=green/4=grey): ");
        int eyes = Integer.parseInt(br.readLine());
        if (eyes != 1 && eyes != 2 && eyes != 3 && eyes != 4) {
            try {
                throw new CallException("!!! Wrong input !!!");
            } catch (CallException e) {
                System.out.println(e);
                return;
            }
        }
        switch (eyes) {
            case 1: c.setEye_color("blue"); break;
            case 2: c.setEye_color("black"); break;
            case 3: c.setEye_color("green"); break;
            case 4: c.setEye_color("grey"); break;
        }
        System.out.print("Select a color of his hair (1=black/2=brown/3=red/4=blue/5=silver): ");
        int hair = Integer.parseInt(br.readLine());
        if (hair != 1 && hair != 2 && hair != 3 && hair != 4 && hair != 5) {
            try {
                throw new CallException("!!! Wrong input !!!");
            } catch (CallException e) {
                System.out.println(e);
                return;
            }
        }
        switch (hair) {
            case 1: c.setHair_color("black"); break;
            case 2: c.setHair_color("brown"); break;
            case 3: c.setHair_color("red"); break;
            case 4: c.setHair_color("blue"); break;
            case 5: c.setHair_color("silver"); break;
        }
        System.out.print("Select an expression of his face (1=angry/2=smile/3=neutral): ");
        int face = Integer.parseInt(br.readLine());
        if (face != 1 && face != 2 && face != 3) {
            try {
                throw new CallException("!!! Wrong input !!!");
            } catch (CallException e) {
                System.out.println(e);
                return;
            }
        }
        switch (face) {
            case 1: c.setFace_expression("angry"); break;
            case 2: c.setFace_expression("smile"); break;
            case 3: c.setFace_expression("neutral"); break;
        }
        for (Class cl : Class.getListOfAllClasses()) {
            System.out.println(cl);
        }
        System.out.print("Select ID of a class this character will belong to: ");
        int classId = Integer.parseInt(br.readLine());
        if (Finder_class.getInstance().findById(classId) == null) {
            waitForResponse(br, "!!! Did not find a class !!!");
            run();
        }
        c.setCharacter_class_id(classId);
        c.setIs_alive(true);
        c.insert();
        waitForResponse(br, "Congratulations! Your new character has been created. Read his information below:\n"+c+"\nENTER to continue...");
        run();
    }
    
    /**
     * removes character from a specific player
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void removeCharacterFromPlayer() throws IOException, SQLException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Select player's ID: ");
        int playerId = Integer.parseInt(br.readLine());
        
        Player p;
        p = Finder_player.getInstance().findById(playerId);
        
        if (p == null) {
            try {
                throw new CallException("!!! Did not find a player !!!");
            } catch (CallException e) {
                System.out.println(e);
                return;
            }
        }
        else {
            List<Character> characters = getListOfAllCharacters(p.getId());
            List<Integer> charIds = new ArrayList<>();
            for (Character c : characters) {
                charIds.add(c.getId());
            }
            if (characters.size() == 0) {
                waitForResponse(br, "This player does not own any character.\nENTER to continue...");
                run();
            }
            System.out.println("Your characters are:");
            for (int i = 0; i < characters.size(); i++) {
                System.out.println("   "+characters.get(i));
            }
            System.out.print("Select his ID: ");
            int characterId = Integer.parseInt(br.readLine());
            boolean contains = false;
            for (int i : charIds) {
                if (i == characterId) {
                    contains = true;
                }
            }
            if (!contains) {
                waitForResponse(br, "!!! You must select character which belong to this player !!!");
                run();
            }
            Character c = Finder_character.getInstance().findById(characterId);
            if (c == null) {waitForResponse(br, "!!! Wrong selected character !!!");run();}
            c.delete();
            waitForResponse(br, "You've succesfully deleted character "+c.getName()+" from this game.\nENTER to continue...");
            run();
        }
    }
    
    /**
     * prints list of all items of a character
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void printListOfAllItemsOfCharacter() throws IOException, SQLException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Select ID of player: ");
        int playerId = Integer.parseInt(br.readLine());
        Player p;
        p = Finder_player.getInstance().findById(playerId);
        if (p == null) {
            try {
                throw new CallException("!!! Did not find a player !!!");
            } catch (CallException e) {
                System.out.println(e);
                return;
            }
        }
        List<Character> characters = getListOfAllCharacters(p.getId());
        if (characters.size() == 0) {
            waitForResponse(br, "This player does not own any character.\nENTER to continue...");
            run();
        }
        System.out.println("These are your characters and their items:");
        for (int i = 0; i < characters.size(); i++) {
            System.out.println("CHARACTER: "+characters.get(i));
            if (characters.get(i).getItems().size() == 0) {
                System.out.println("   This character does not own any item.");
            }
            else {
                for (int j = 0; j < characters.get(i).getItems().size(); j++) {
                    System.out.println("   ITEM:"+characters.get(i).getItems().get(j));
                }
            }
        }
        waitForResponse(br, "ENTER to continue...");
        run();
    }
    
    /**
     * help method for a shop process launching
     * @throws SQLException
     * @throws IOException 
     */
    
    public void visitShop() throws SQLException, IOException {
        Shop sh = new Shop();
        sh.shoppingProcess();
    }
    
    /**
     * statistics
     * @throws SQLException 
     */
    
    public void printTopCharacters() throws SQLException {
        TopCharactersStats.evaluate();
    }
    
    /**
     * prints list of all races in the database
     * @throws SQLException
     * @throws IOException
     * @throws Exception 
     */
    
    public void printListOfAllRaces() throws SQLException, IOException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<Race> races = Race.getListOfAllRaces();
        System.out.println("Current races in the game:");
        for (Race r : races) {
            System.out.println(r.getId()+". "+r.getType()+": "+r.getDescription());
        }
        waitForResponse(br, "ENTER to continue...");
        run();
    }
    
    /**
     * adds race into database
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void addRace() throws IOException, SQLException, Exception {
        
        Race r = new Race();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Select the type of new race: ");
        r.setType(br.readLine());
        System.out.println("");
        System.out.print("Select description of new race: ");
        r.setDescription(br.readLine());
        System.out.println("");
        r.insert();
        waitForResponse(br, "You have succesfully created new race with ID "+r.getId()+".\nENTER to continue...");
        run();
    }
    
    /**
     * removes race from a database
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void removeRace() throws IOException, SQLException, Exception {
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<Race> races = Race.getListOfAllRaces();
        if (races.size() == 0) {
            waitForResponse(br, "There is not a race in the game.");
            run();
        }
        for (Race r : races) {
            System.out.println(r.getId()+". "+r.getType()+": "+r.getDescription());
        }
        System.out.print("Select ID of the race: ");
        int raceId = Integer.parseInt(br.readLine());
        
        Race r;
        r = Finder_race.getInstance().findById(raceId);
        
        if (r == null) {
            try {
                throw new CallException("!!! Did not find a race !!!");
            } catch (CallException e) {
                System.out.println(e);
                return;
            }
        }
        else {
            r.delete();
            waitForResponse(br, "You've succesfully deleted race "+r.getType()+" from this game.\nENTER to continue...");
            run();
        }
    }
    
    /**
     * adds class into database
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void addClass() throws IOException, SQLException, Exception {
        Class c = new Class();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Select the type of new class: ");
        c.setType(br.readLine());
        System.out.println("");
        System.out.print("Select description of new class: ");
        c.setDescription(br.readLine());
        System.out.println("");
        c.insert();
        waitForResponse(br, "You have succesfully created new class with ID "+c.getId()+".\nENTER to continue...");
        run();
    }
    
    /**
     * removes class from a database
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void removeClass() throws IOException, SQLException, Exception {
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<Class> classes = Class.getListOfAllClasses();
        if (classes.size() == 0) {
            waitForResponse(br, "There is not a class in the game.");
            run();
        }
        for (Class c : classes) {
            System.out.println(c.getId()+". "+c.getType()+": "+c.getDescription());
        }
        System.out.print("Select ID of the class: ");
        int classId = Integer.parseInt(br.readLine());
        
        Class c;
        c = Finder_class.getInstance().findById(classId);
        
        if (c == null) {
            try {
                throw new CallException("!!! Did not find a class !!!");
            } catch (CallException e) {
                System.out.println(e);
                return;
            }
        }
        else {
            c.delete();
            waitForResponse(br, "You've succesfully deleted class "+c.getType()+" from this game.\nENTER to continue...");
            run();
        }
    }
    
    /**
     * prints list of all game character classes in database
     * @throws SQLException
     * @throws IOException
     * @throws Exception 
     */
    
    public void printListOfClasses() throws SQLException, IOException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Class.printListOfClasses();
        waitForResponse(br, "ENTER to continue...");
        run();
    }
    
    /**
     * prints data about a specific class
     * @throws SQLException
     * @throws IOException
     * @throws Exception 
     */
    
    public void getDataAboutClass() throws SQLException, IOException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<Class> classes = Class.getListOfAllClasses();
        for (Class c : classes) {
            System.out.println(c);
        }
        System.out.print("Select ID of class you want to get info about: ");
        int classId = Integer.parseInt(br.readLine());
        Class c;
        c = Finder_class.getInstance().findById(classId);
        if (c == null) {
            try {
                throw new CallException("!!! Did not find a class !!!");
            } catch (CallException e) {
                System.out.println(e);
                return;
            }
        }
        System.out.println("-----------------INFORMATION-------------------");
        System.out.println(c);
        c.printAbilities(classId);
        waitForResponse(br, "ENTER to continue...");
        run();
    }
    
    /**
     * updates a game character class in a database
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void updateClass() throws IOException, SQLException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Select ID of a class: ");
        int classId = Integer.parseInt(br.readLine());
        
        Class c;
        c = Finder_class.getInstance().findById(classId);
        if (c == null) {
            try {
                throw new CallException("!!! Did not find a class !!!");
            } catch (CallException e) {
                System.out.println(e);
                return;
            }
        }
        System.out.println("These are abilities for current class:");
        c.printAbilities(classId);
        System.out.println("");
        System.out.print("Select ID of ability you want to remove OR type 'N' if you want to add a new one. ");
        String output = br.readLine();
        if (output.equalsIgnoreCase("N") == true) {
            //pridavanie novej ability triede
            Ability.printAllAbilities();
            System.out.print("Select ID of ability you want to add. ");
            int abId = Integer.parseInt(br.readLine());
            Ability a = Finder_ability.getInstance().findById(abId);
            c.addAbility(a);
            waitForResponse(br, "You have succesfully added this ability to the class "+c.getType()+".");
            run();
        }
        else {
            //vymazanie ability z triedy
            int abId = Integer.parseInt(output);
            Ability a = Finder_ability.getInstance().findById(abId);
            c.removeAbility(a);
            waitForResponse(br, "You have succesfully removed this ability from the class "+c.getType()+".");
            run();
        }
    }
    
    /**
     * adding or removing class to/from a race
     * @throws SQLException
     * @throws IOException
     * @throws Exception 
     */
    
    public void classToRaceManipulate() throws SQLException, IOException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<Race> races = Race.getListOfAllRaces();
        List<Class> classes = Class.getListOfAllClasses();
        System.out.println("These are all races:");
        for (Race r : races) {
            System.out.println("   "+r);
        }
        System.out.println("These are all classes:");
        for (Class c : classes) {
            System.out.println("   "+c);
        }
        System.out.print("Select ID of race you want to manipulate with. ");
        int raceId = Integer.parseInt(br.readLine());
        Race r = Finder_race.getInstance().findById(raceId);
        if (r == null) {
            waitForResponse(br, "!!! Did not find a race !!!");
            run();
        }
        System.out.print("Select ID of class you want to add/remove. ");
        int classId = Integer.parseInt(br.readLine());
        Class c = Finder_class.getInstance().findById(classId);
        if (c == null) {
            waitForResponse(br, "!!! Did not find a class !!!");
            run();
        }
        System.out.print("Select 'A' if you wish to add a class to race, or 'R' if you wish to remove class from race. ");
        String output = br.readLine();
        if (output.equalsIgnoreCase("a") == true) {
            try {
                r.addClass(c);
                waitForResponse(br, "You have succesfully added this class to race "+r.getType()+".");
                run();
            } catch (SQLException e) {
                waitForResponse(br, "This pair of race and class already exists.");
                run();
            }
        }
        else {
            r.removeClass(c);
            waitForResponse(br, "You have succesfully removed this class from race "+r.getType()+".");
            run();
        }
    }
    
    /**
     * prints fee history of a specific player
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void printFeeHistory() throws IOException, SQLException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Select ID of a player: ");
        int playerId = Integer.parseInt(br.readLine());
        Player p = Finder_player.getInstance().findById(playerId);
        if (p == null) {
            waitForResponse(br, "!!! Did not find a player !!!");
            run();
        }
        p.printFeeHistory();
        waitForResponse(br, "ENTER to continue...");
        run();
    }
    
    /**
     * test only - level up for a player
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void testLevelUp() throws IOException, SQLException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Select ID of a player: ");
        int playerId = Integer.parseInt(br.readLine());
        Player p = Finder_player.getInstance().findById(playerId);
        for (Character c : p.getListOfAllCharacters()) {
            System.out.println(c);
        }
        System.out.print("Select character ID. ");
        int charId = Integer.parseInt(br.readLine());
        Character c = Finder_character.getInstance().findById(charId);
        if(c.levelUp()) {
            System.out.println("Increased");
        }
        else {
            System.out.println("Didn't increase.");
        }
    }
    
    /**
     * test only - adds money for a specific player
     * @throws IOException
     * @throws SQLException
     * @throws Exception 
     */
    
    public void testAddMoney() throws IOException, SQLException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Select ID of a player: ");
        int playerId = Integer.parseInt(br.readLine());
        Player p = Finder_player.getInstance().findById(playerId);
        System.out.print("Select value (CREDIT): ");
        int value = Integer.parseInt(br.readLine());
        p.testAddMoney(value);
        System.out.println("Money added.");
    }
    
    /**
     * handle user input for a domain operation character transfer
     * @throws SQLException
     * @throws Exception 
     */
    
    public void characterTransfer() throws SQLException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Select ID of a buyer (costs 10 credits): ");
        int buyerId = 0; int sellerId = 0;
        try {
            buyerId = Integer.parseInt(br.readLine());
            System.out.print("Select ID of a seller (costs 7 credits): ");
            sellerId = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            System.out.println("Did not find a player");
            return;
        }
        if (Finder_player.getInstance().findById(buyerId) == null || Finder_player.getInstance().findById(sellerId) == null) {
            waitForResponse(br, "!!! Some of these player do not exists !!!");
            run();
        }
        List<Character> characters = getListOfAllCharacters(sellerId);
        List<Integer> charIds = new ArrayList<>();
        for (Character c : characters) {
            charIds.add(c.getId());
        }
        System.out.println("Seller owns "+characters.size()+" characters. See their information below:");
        for (int i = 0; i < characters.size(); i++) {
            System.out.println(characters.get(i));
        }
        if (characters.size() < 1) {
            waitForResponse(br, "Seller does not own any character. ENTER to continue...");
            run();
        }
        System.out.print("Now, select ID of 1 of seller's characters listed above you want to buy. ");
        int ch1 = Integer.parseInt(br.readLine());
        boolean containId = false;
        for (int i : charIds) {
            if (i == ch1) {
                containId = true;
            }
        }
        if (!containId) {
            waitForResponse(br, "!!! You must select some of these characters !!!");
            run();
        }
        boolean result = CharacterTransfer.transfer(buyerId, sellerId, ch1);
        if (result) {waitForResponse(br, "Congratulations! Character has been succesfully transfered."+"\nENTER to continue...");
                    run();
        } else {
            waitForResponse(br, "ENTER to continue...");
            run();
        }
    }
    
    /**
     * handle user input for a changing an appearance of a character
     * @throws IOException
     * @throws Exception 
     */
    
    public void changeAppearance() throws IOException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Select ID of a player (costs 50 credit): ");
        Player p = Finder_player.getInstance().findById(Integer.parseInt(br.readLine()));
        if (p == null) {
            waitForResponse(br, "!!! Did not find a player !!!");
            run();
        }
        List<Character> chars = p.getListOfAllCharacters();
        List<Integer> charIds = new ArrayList<>();
        for (Character c : chars) {
            charIds.add(c.getId());
        }
        for (Character c : chars) {
            System.out.println(c);
        }
        System.out.print("Select ID of a character: ");
        int charId = Integer.parseInt(br.readLine());
        boolean containId = false;
        for (int i : charIds) {
            if (i == charId) {
                containId = true;
            }
        }
        if (!containId) {
            waitForResponse(br, "!!! You must select some of these characters !!!");
            run();
        }
        try {
            ChangeAppearance change = new ChangeAppearance(p, charId);
            if (change.checkCredit() == false) {waitForResponse(br, "Not enough credit");run();}
            System.out.print("Select new gender (MALE/FEMALE): ");
            String answer = br.readLine();
            if (change.updateGender(answer) == false) {waitForResponse(br, "Wrong data");run();}
            System.out.print("Select new eye color (blue/black/green/grey): ");
            answer = br.readLine();
            if (change.updateEyeColor(answer) == false) {waitForResponse(br, "Wrong data"); run();}
            System.out.print("Select new hair color (black/brown/red/blue/silver): ");
            answer = br.readLine();
            if (change.updateHairColor(answer) == false) {waitForResponse(br, "Wrong data");run();}
            System.out.print("Select new face expression (angry/smile/neutral): ");
            answer = br.readLine();
            if (change.updateFaceExpression(answer) == false) {waitForResponse(br, "Wrong data");run();}
            System.out.print("Do you want to confirm changes? (y/n)");
            answer = br.readLine();
            if (answer.equals("y")) {change.confirmChanges(true);waitForResponse(br, "Success!");run();}
            else {change.confirmChanges(false);waitForResponse(br, "Was not changed"); run();}
        } catch (SQLException e) {
            waitForResponse(br, "Something wrong happened during appearance change");
            run();
        }
    }
    
    /**
     * launch bounty hunter transaction
     * @throws SQLException
     * @throws Exception 
     */
    
    public void bountyHunter() throws SQLException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        if (BountyHunter.evaluate() == true) {
            waitForResponse(br, "These are the results! Prizes has been added.");
            run();
        }
    }
    
    /**
     * launch a fight simulation between 2 selected characters
     * @throws SQLException
     * @throws Exception 
     */
    
    public void fightAndHeal() throws SQLException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int player1 = 0; int player2 = 0;
        try {
            System.out.print("Select ID of 1st player: ");
            player1 = Integer.parseInt(br.readLine());
            System.out.print("Select ID of 2nd player: ");
            player2 = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            System.out.println("Something went wrong");
            return;
        }
        if (Finder_player.getInstance().findById(player1) == null || Finder_player.getInstance().findById(player2) == null) {
            waitForResponse(br, "!!! Did not find a player !!!");
            run();
        }
        int alive = 0;
        for (Character c : getListOfAllCharacters(player1)) {
            if (c.getIs_alive() == true) {
                System.out.println("    "+c);
                alive++;
            }
        }
        if (alive == 0) {
            waitForResponse(br, "Player 1 does not have any alive character. ENTER to continue...");
            run();
        }
        int char1 = 0; int char2 = 0;
        System.out.print("Select ID of character of 1st player: ");
        char1 = Integer.parseInt(br.readLine());
        List<Integer> char1Ids = new ArrayList<>();
        for (Character c : getListOfAllCharacters(player1)) {
            char1Ids.add(c.getId());
        }
        boolean contain1Id = false;
        for (int i : char1Ids) {
            if (i == char1) {
                contain1Id = true;
            }
        }
        if (!contain1Id) {
            waitForResponse(br, "!!! You must select some of those characters !!!");
            run();
        }
        alive = 0;
        for (Character c : getListOfAllCharacters(player2)) {
            if (c.getIs_alive() == true) {
                System.out.println("    "+c);
                alive++;
            }
        }
        if (alive == 0) {
            waitForResponse(br, "Player 2 does not have any alive character. ENTER to continue...");
            run();
        }
        System.out.print("Select ID of character of 2nd player: ");
        char2 = Integer.parseInt(br.readLine());
        List<Integer> char2Ids = new ArrayList<>();
        for (Character c : getListOfAllCharacters(player2)) {
            char2Ids.add(c.getId());
        }
        boolean contain2Id = false;
        for (int i : char2Ids) {
            if (i == char2) {
                contain2Id = true;
            }
        }
        if (!contain2Id) {
            waitForResponse(br, "You must select some of those characters !!!");
            run();
        }
        try {
            FightAndHeal fight = new FightAndHeal(player1, player2, char1, char2);
            fight.simulateFight();
        } catch (SQLException e) {
            System.out.println(e);
        }
        waitForResponse(br, "ENTER to continue...");
        run();
    }
    
    /**
     * statistics - ordering players by kills
     * @throws SQLException 
     */
    
    public void playersOrderByKills() throws SQLException {
        PlayersOrderByKills.evaluate();
    }
    
}
