import java.util.List;

public class Scoreboard {
    private List<Player> playerList;
    private int scoreLimit;

    public Scoreboard(GeneralGame game, List<Player> players) {
        playerList = players;
    }

    {}
    {}

    public void setDefaultScoreLimit(int limit) {
        scoreLimit = limit;
    }

    public int getScoreLimit() {
        return this.scoreLimit;
    }
}
