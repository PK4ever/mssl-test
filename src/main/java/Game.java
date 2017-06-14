import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by paulk4ever on 5/27/17.
 */
@DatabaseTable(tableName = "games")
public class Game {
    Game(){

    }

    @DatabaseField(columnName = "ID", generatedId = true)
    private int id;

//    @DatabaseField(columnName = "DATE", dataType = DataType.DATE_LONG, format = "dd-MM-yyyy")
//    private Date date;
    @DatabaseField(columnName = "DATE", format = "dd-MM-yyyy")
    private String date;

    @DatabaseField(columnName = "TIME")
    private String time;

    @DatabaseField(columnName = "FIELD")
    private String field;

//    @ForeignCollectionField(columnName = "TEAMS", eager = true)
//    public ForeignCollection<Team> teams;
    @DatabaseField(foreign = true, columnName = "TEAM1")
    private Team team1;

    @DatabaseField(foreign = true, columnName = "TEAM2")
    private Team team2;

    @DatabaseField(columnName = "TEAM1SCORE")
    private int team1Score = -1;

    @DatabaseField(columnName = "TEAM2SCORE")
    private int team2Score = -1;

    public int getID(){
        return this.id;
    }
//    public void setDate(Date date){
//        this.date = date;
//    }
//    public Date getDate(){
//        return this.date;
//    }
    public void setDate(String date){
    this.date = date;
}
    public String getDate(){
        return this.date;
    }

    public String getTime(){
        return this.time;
    }

    public void setTime(String time){
        this.time = time;
    }
    public void setTeam1(Team team1){
        this.team1 = team1;
    }
    public Team getTeam1(){
        return this.team1;
    }
    public void setTeam2(Team team2){
        this.team2 = team2;
    }
    public Team getTeam2(){
        return this.team2;
    }

    public void setField(String field){
        this.field = field;
    }
    public String getField(){
        return this.field;
    }
    public void setTeam1Score(int team1Score){
        this.team1Score = team1Score;
    }
    public int getTeam1Score(){
        return team1Score;
    }
    public void setTeam2Score(int team2Score){
        this.team2Score = team2Score;
    }
    public int getTeam2Score(){
        return this.team2Score;
    }
    public String getScore(){
        if (team1Score == -1){
            return "__ " +"   -  "+ " __";

        }else {
            return team1Score+"   -   "+team2Score;
        }
    }
}
