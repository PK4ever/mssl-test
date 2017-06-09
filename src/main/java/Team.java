import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by paulk4ever on 5/19/17.
 */
@DatabaseTable(tableName = "teams")
public class Team {

    Team(){}

    @DatabaseField(columnName = "NAME", unique = true, id = true, canBeNull = false)
    public String name;

    @DatabaseField(columnName = "PLAYED")
    public int played = 0;

    @DatabaseField(columnName = "POSITION")
    public int position = 0;

    @DatabaseField(columnName = "WINS")
    public int wins = 0;

    @DatabaseField(columnName = "DRAWS")
    public int draws = 0;

    @DatabaseField(columnName = "LOSES")
    public int loses = 0;

    @DatabaseField(columnName = "GOALSFOR")
    public int goalsFor = 0;

    @DatabaseField(columnName = "GOALSAGAINST")
    public int goalsAgainst = 0;

    @DatabaseField(columnName = "GOALDIFFERENTIAL")
    public int goalDifferential = 0;

    @DatabaseField(columnName = "POINTS")
    public int points = 0;

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
    public void setGoals(int goals){
        this.played = goals;
    }
    public int getGoals(){
        return this.played;
    }

    @ForeignCollectionField(columnName = "players", eager = true)
    public ForeignCollection<Player> players;

    public ForeignCollection<Player> getPlayers() {
        return players;
    }

    @ForeignCollectionField(columnName = "games", eager = true)
    public ForeignCollection<Game> games;

    public ForeignCollection<Game> getGames() {
        return games;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public int getGoalDifferential() {
        return goalDifferential;
    }

    public void setGoalDifferential(int goalDifferential) {
        this.goalDifferential = goalDifferential;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
    public void clearAll(){
        setDraws(0);
        setGoalDifferential(0);
        setGoalsAgainst(0);
        setWins(0);
        setLoses(0);
        setPoints(0);
        setPosition(0);
        setGoalsFor(0);
        setPlayed(0);
    }
}
