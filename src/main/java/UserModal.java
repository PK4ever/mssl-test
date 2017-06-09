import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by paulk4ever on 5/19/17.
 */
@DatabaseTable(tableName = "adminUser")
public class UserModal {

    public static final String USERNAME_FIELD = "userName";
    public static final String PASSWORD_FIELD = "password";

    UserModal(){

    }


    @DatabaseField(columnName = USERNAME_FIELD, unique = true, id = true, canBeNull = false)
    private String userName;

    @DatabaseField(columnName = PASSWORD_FIELD, canBeNull = false)
    private String password;


    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getUserName(){
        return this.userName;
    }

    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return this.password;
    }


}
