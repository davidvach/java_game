/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vacha
 */

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestMenuConsole extends Menu_Console{

    @Test
    public void TestFakeFinder() throws  Exception {
        Menu_Console mc = new Menu_Console();
        List list = mc.getListOfAllCharacters(4);

        assertEquals(list.size(), 0);
    }

    @Test
    public void TestScoreboard() {
        FakeGame game = new FakeGame();
        Scoreboard sc = new Scoreboard(game, null);

        sc.setDefaultScoreLimit(10);

        assertEquals(10, sc.getScoreLimit());
    }
}
