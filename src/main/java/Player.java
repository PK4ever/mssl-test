import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * Created by paulk4ever on 5/23/17.
 */
@DatabaseTable(tableName = "manliussumm_8345.players")

public class Player {

    Player(Team team){
        this.team = team;
    }
    Player(){

    }

    @DatabaseField(columnName = "fullName",unique = true, id = true)
    private String fullName;

//    @DatabaseField(columnName = "id", generatedId = true)
//    private int id;

    @DatabaseField(columnName = "firstName")
    private String firstName;

    @DatabaseField(columnName = "lastName")
    private String lastName;

    @DatabaseField(columnName = "team", foreign = true, foreignAutoRefresh = true)
    private Team team;

    public void setFullName(){
        this.fullName = firstName+ " "+ this.lastName;

    }
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    public String getFirstName(){
        return this.firstName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
    }
    public String getLastName(){
        return this.lastName;
    }
    public Team getTeam(){
        return this.team;
    }
    public String getFullName(){
      return this.firstName+ " "+ this.lastName;
    }
}
