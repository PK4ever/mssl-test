/**
 * Created by paulk4ever on 5/8/17.
 */

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import spark.ModelAndView;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        port(getHerokuAssignedPort());

        UserAuth userAuth = new UserAuth();
        staticFileLocation("/public");
        String layout = "templates/layout.vtl";
        String page = "templates/menubar.vtl";

        String mysqlUrl = System.getenv("SCALINGO_MYSQL_URL");
//        String databaseUrl = "jdbc:"+mysqlUrl;
//        connectionSource = new JdbcConnectionSource("jdbc:"+mysqlUrl)
//        String databaseUrl = "jdbc:mysql://localhost:3306/mssl?characterEncoding=UTF-8&useSSL=false";
//        String databaseUrl = "jdbc:mysql://manliussumm_8345:JpbYSaGBWdLsmwJUTyHp@manliussumm-8345.mysql.dbs.appsdeck.eu:31318/manliussumm_8345";
//        String databaseUrl = "jdbc:mysql://"+getHerokuAssignedPort()+"/mssl?characterEncoding=UTF-8&useSSL=false";
//        String databaseUrl = "jdbc:mysql://localhost/mssl?characterEncoding=UTF-8&useSSL=false";
//        String databaseUrl = "jdbc:postgresql://localhost:5432/mssl?characterEncoding=UTF-8&useSSL=false";

//        String databaseUrl = "jdbc:mysql://manliussumm_8345";
        String databaseUrl = "jdbc:mysql://manliussumm_8345:31318/manliussumm_8345";



        ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);
//        ((JdbcConnectionSource) connectionSource).setUsername("postgres");
        ((JdbcConnectionSource) connectionSource).setUsername("root");
        ((JdbcConnectionSource) connectionSource).setPassword("mdbh6548");
        Dao<UserModal, String> userModalStringDao = DaoManager.createDao(connectionSource, UserModal.class);

        UserModal userModal = new UserModal();
        userModal.setUserName("admin");
        userModal.setPassword("mssl");
//        userModalStringDao.createIfNotExists(userModal);

        userAuth.createIfNotExists(userModal, userModalStringDao);

        TableUtils.createTableIfNotExists(connectionSource, Team.class);
        TableUtils.createTableIfNotExists(connectionSource, Player.class);
        TableUtils.createTableIfNotExists(connectionSource, Game.class);

        StandingsCalculator standingsCalculator = new StandingsCalculator();

        get("/", (request, response) -> {
            Dao<Team, String> teamDao = DaoManager.createDao(connectionSource, Team.class);
            Dao<Game, String> gameDao = DaoManager.createDao(connectionSource, Game.class);

            List<Team> teams = teamDao.queryForAll();

            List<Game> games = gameDao.queryForAll();

            ArrayList<Game> upcomingGames = new ArrayList<Game>();


            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            Date target;
            Date today = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(today);

            c.add(Calendar.DATE, 7);
            Date oneWeekLater = c.getTime();

            for (Game g :games) {
                try {
                    target = df.parse(g.getDate());
//                    String gameDate = df.format(target);
                    if (target.after(today) && target.before(oneWeekLater)){
                        upcomingGames.add(g);
                    }
                    String targetString = df.format(target);
                    String todayString = df.format(today);
                    if (targetString.equals(todayString)){
                        upcomingGames.add(g);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            Team t1;
            for (Team t:teams){
                t1 = teamDao.queryForId(t.getName());
                t1.clearAll();
                teamDao.update(t1);
            }
            standingsCalculator.calculate(teamDao, games, teams);


            Map<String, Object> model = new HashMap<String, Object>();
            model.put("upcomingGames", upcomingGames);
            model.put("games", games);
            model.put("Teams", teams);
            model.put("template", "public/home.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        get("/admin", (request, response) -> {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("template", "templates/admin.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());


        before("/login", (request, response) -> {

            if (userAuth.validateUser(request, userModalStringDao)) {
                request.session().attribute("connected", request.queryParams("password"));
                request.session(true);
//                response.redirect("/admin_forms");
                response.redirect("/admin_Home");
            } else {
                response.redirect("/signup");
            }
        });
        get("/admin_Home", (request, response) -> {
            if (userAuth.isLoggedIn(request)) {
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("page", "templates/admin_Home.vtl");
                return new ModelAndView(model, page);
            }else {
                response.redirect("/admin");
                return null;
            }
        }, new VelocityTemplateEngine());


        get("/admin_forms", (request, response) -> {
            if (request.session().attribute("connected") != null) {

                Dao<Team, String> teamDao = DaoManager.createDao(connectionSource, Team.class);
                List<Team> teams = teamDao.queryForAll();
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("Teams", teams);
                model.put("page", "templates/teams.vtl");
                return new ModelAndView(model, page);
            } else {
                response.redirect("/admin");
                return null;
            }

        }, new VelocityTemplateEngine());

        get("/addTeam", (request, response) -> {
            if (userAuth.isLoggedIn(request)) {
                Dao<Team, String> teamDao = DaoManager.createDao(connectionSource, Team.class);
                if (request.queryParams("teamName") != null) {
                    Team team = new Team();
                    team.setName(request.queryParams("teamName"));
                    teamDao.createIfNotExists(team);
                }
                List<Team> teams = teamDao.queryForAll();
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("Teams", teams);
//            model.put("template", "templates/teams.vtl" );
                model.put("page", "templates/teams.vtl");
                return new ModelAndView(model, page);
            } else {
                response.redirect("/admin");
                return null;
            }
        }, new VelocityTemplateEngine());

        get("/teamPage", (request, response) -> {
            if (userAuth.isLoggedIn(request)) {
                String teamName = request.queryParams("teamName").replace("%20", " ");
                Dao<Team, String> teamDao = DaoManager.createDao(connectionSource, Team.class);
                Team team = teamDao.queryForId(teamName);
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("team", team);
//                model.put("template", "templates/teamPage.vtl" );
                model.put("page", "templates/teamPage.vtl");
                return new ModelAndView(model, page);
            } else {
                response.redirect("/admin");
                return null;
            }
        }, new VelocityTemplateEngine());

        get("/addPlayer", (request, response) -> {
            if (userAuth.isLoggedIn(request)) {
                String teamName = request.queryParams("teamName").replace("%20", " ");
                Dao<Team, String> teamDao = DaoManager.createDao(connectionSource, Team.class);
                Team team = teamDao.queryForId(teamName);
                Dao<Player, String> playerDao = DaoManager.createDao(connectionSource, Player.class);
                if (request.queryParams("firstName") != null) {
                    Player player = new Player(team);
                    player.setFirstName(request.queryParams("firstName"));
                    player.setLastName(request.queryParams("lastName"));
                    player.setFullName();
                    playerDao.createIfNotExists(player);
                }
                Team team1 = teamDao.queryForId(teamName);
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("team", team1);
                model.put("page", "templates/teamPage.vtl");
                return new ModelAndView(model, page);
            } else {
                response.redirect("/admin");
                return null;
            }
        }, new VelocityTemplateEngine());


        get("/signup", (request, response) -> {
            return "You are not an admin";
        });

        before("/logout", (request, response) -> {
            request.session().invalidate();
            response.redirect("/admin");
        });

        delete("remove/:team", (request, response) -> {

            String teamName = request.params(":team").replace("%20", " ");
            Dao<Team, String> teamDao = DaoManager.createDao(connectionSource, Team.class);
            Team team1 = teamDao.queryForId(teamName);
            if (!team1.getPlayers().isEmpty()) {
                team1.getPlayers().clear();
            }

            String id = teamName;
            DeleteBuilder<Team, String> deleteBuilder = teamDao.deleteBuilder();
            deleteBuilder.where().eq("NAME", id);

            if (deleteBuilder.delete() > 0) {
                return "Tem with ID: " + request.params(":team") + " deleted.";
            } else {
                return "Team with ID: " + request.params(":team") + "Not found.";
            }
        });

        delete("removePlayer/:player", (request, response) -> {
            String playerName = request.params(":player").replace("%20", " ");
            Dao<Player, String> playerDao = DaoManager.createDao(connectionSource, Player.class);

            String id = playerName;
            DeleteBuilder<Player, String> deleteBuilder = playerDao.deleteBuilder();
            deleteBuilder.where().eq("fullName", id);

            if (deleteBuilder.delete() > 0) {
                return "Player with ID: " + request.params(":player") + " deleted.";
            } else {
                return "Player with ID: " + request.params(":player") + "Not found.";
            }
        });

        get("/edit_schedule", (request, response) -> {
            Dao<Team,String> teamDao = DaoManager.createDao(connectionSource,Team.class);
            Dao<Game,String> gameDao = DaoManager.createDao(connectionSource,Game.class);
            List<Team> teams = teamDao.queryForAll();
            List<Game> games = gameDao.queryForAll();

            Map<String, Object> model = new HashMap<String, Object>();
            model.put("games", games);
            model.put("teams", teams);
            model.put("page", "templates/edit_schedule.vtl");
            return new ModelAndView(model, page);
        }, new VelocityTemplateEngine());

        get("/updateSchedule/:date/:time/:team1/:team2/:field", (request, response) -> {

            String date = request.params(":date").replace(" ","/");
            String time = request.params(":time");
            String team1 = request.params(":team1");
            String team2 = request.params(":team2");
            String field = request.params(":field");


            Dao<Game,String> gameDao = DaoManager.createDao(connectionSource,Game.class);
            Game game = new Game();
//
            game.setDate(date);
            game.setTime(time);
            Dao<Team, String> teamDao = DaoManager.createDao(connectionSource, Team.class);
            Team teamObject1 = teamDao.queryForId(team1);
            game.setTeam1(teamObject1);
            Team teamObject2 = teamDao.queryForId(team2);
            game.setTeam2(teamObject2);
            game.setField(field);
            gameDao.createIfNotExists(game);

            List<Team> teams = teamDao.queryForAll();
            List<Game> games = gameDao.queryForAll();

            Map<String, Object> model = new HashMap<String, Object>();
            model.put("games", games);
            model.put("teams", teams);
            model.put("page", "templates/edit_schedule.vtl");
            return new ModelAndView(model, page);
        }, new VelocityTemplateEngine());


        delete("removeGame/:id", (request, response) -> {
            int gameID = Integer.valueOf(request.params(":id"));
            Dao<Game, String> gameDao = DaoManager.createDao(connectionSource, Game.class);

            DeleteBuilder<Game, String> deleteBuilder = gameDao.deleteBuilder();
            deleteBuilder.where().eq("id", gameID);

            if (deleteBuilder.delete() > 0) {
                return " deleted!!";
            } else {
                return "Not found";
            }
        });
        get("/addScores", (request, response) -> {
            Dao<Game, String> gameDao = DaoManager.createDao(connectionSource, Game.class);
            List<Game> games = gameDao.queryForAll();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("games", games);
            model.put("page", "templates/addScores.vtl" );
            return new ModelAndView(model, page);
        }, new VelocityTemplateEngine());

        put("/putScores/:id/:team1Score/:team2Score", (request, response) -> {

                String gameID = request.params(":id");
                int team1Score = Integer.valueOf(request.params(":team1Score"));
                int team2Score = Integer.valueOf(request.params(":team2Score"));

                Dao<Game, String> gameDao = DaoManager.createDao(connectionSource, Game.class);
                Game game = gameDao.queryForId(gameID);
                game.setTeam1Score(team1Score);
                game.setTeam2Score(team2Score);
                //add score to team for standings
                gameDao.update(game);
                List<Game> games = gameDao.queryForAll();


                Map<String, Object> model = new HashMap<String, Object>();
                model.put("games", games);
                model.put("page", "templates/addScores.vtl");
            return new ModelAndView(model, page);

        }, new VelocityTemplateEngine());


//        get("/", (request, response) -> {
//            Map<String, Object> model = new HashMap<String, Object>();
//            model.put("template", "templates/search.vtl" );
//            return new ModelAndView(model, layout);
//        }, new VelocityTemplateEngine());

//        get("/eventList", (request, response) -> {
//            String keyWord = request.queryParams("event");
//            List<Event> events = eventFulController.search(keyWord);
//            Map<String, Object> model = new HashMap<String, Object>();
//            model.put("EventsList", events);
//            model.put("template", "templates/eventView.vtl" );
//            return new ModelAndView(model, layout);
//        }, new VelocityTemplateEngine());
//
//
//        post("/liked", (request, response) -> {
//
//            String title = request.queryParams("title");
//            String venue = request.queryParams("venue");
//            String seid = request.queryParams("seid");
//            EventModal eventModal = new EventModal();
//            if (eventModalsDao.queryForId(seid) == null) {
//                eventModal.setTitle(title);
//                eventModal.setVenue(venue);
//                eventModal.setSeID(seid);
//                eventModalsDao.create(eventModal);
//            }
//            List<EventModal> modals = eventModalsDao.queryForAll();
//            Map<String, Object> model = new HashMap<String, Object>();
//            model.put("modals", modals);
//
//            model.put("template", "templates/liked.vtl" );
//            return new ModelAndView(model, layout);
//        }, new VelocityTemplateEngine());
//
//
//        get("/remove", (request, response) -> {
//            String seid = request.queryParams("seid");
//
//            DeleteBuilder<EventModal, String> deleteBuilder = eventModalsDao.deleteBuilder();
//            deleteBuilder.where().eq(EventModal.SEID_FIELD, seid);
//            int count = deleteBuilder.delete();
//
//            List<EventModal> modals = eventModalsDao.queryForAll();
//            Map<String, Object> model = new HashMap<String, Object>();
//
//            model.put("modals", modals);
//            model.put("template", "templates/liked.vtl" );
//            return new ModelAndView(model, layout);
//        }, new VelocityTemplateEngine());
//
//        //RESTFUL
//        //Delete
//        delete("remove/:seid", (request, response) -> {
//            String seid = request.params(":seid");
//            DeleteBuilder<EventModal, String> deleteBuilder = eventModalsDao.deleteBuilder();
//            deleteBuilder.where().eq(EventModal.SEID_FIELD, seid);
//
//            if (deleteBuilder.delete() > 0) {
//                return "Event with SEID: " + request.params(":seid") + " deleted.";
//            } else {
//                return "Event with SEID: " + request.params(":seid") + "Not found.";
//            }
//        });
//
//        //POST
//        post("/addAll/:keyword", (request, response) -> {
//            String keyword = request.params(":keyword");
//            EventModal eventModal = new EventModal();
//            for (Event e: eventFulController.search(keyword)){
//                eventModal.setTitle(e.getTitle());
//                eventModal.setVenue(e.getVenueAddress());
//                eventModal.setSeID(e.getSeid());
//                eventModalsDao.create(eventModal);
//            }
//
//            List<EventModal> modals = eventModalsDao.queryForAll();
//            Map<String, Object> model = new HashMap<String, Object>();
//            model.put("modals", modals);
//
//            model.put("template", "templates/liked.vtl" );
//            return "All Events matching the search word "+keyword+ "have been added";
//        });


    }
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

}