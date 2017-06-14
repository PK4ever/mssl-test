import com.j256.ormlite.dao.Dao;
import spark.Request;

import java.sql.SQLException;

/**
 * Created by paulk4ever on 5/21/17.
 */
public class UserAuth {

    public void createIfNotExists(UserModal modal, Dao userDao) throws SQLException {
        userDao.createIfNotExists(modal);
    }
    public boolean validateUser(Request request, Dao userDao) throws SQLException {
        UserModal userModal1 = new UserModal();
        userModal1.setUserName(request.queryParams("username"));
        userModal1.setPassword(request.queryParams("password"));

        if (!userDao.queryForMatching(userModal1).isEmpty()) {
            return true;
        }else{
            return false;
        }
    }
    public boolean isLoggedIn(Request request){
        if (request.session().attribute("connected") != null) {
            return true;
        }else{
            return false;
        }
    }
}

