import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

/**
 * Created by paulk4ever on 5/31/17.
 */
public class StandingsCalculator {

    Dao<Team,String> teamDao;

    public StandingsCalculator(){

    }

    public void calculate(Dao<Team,String> teamDao, List<Game>games, List<Team> teams) throws SQLException {
        this.teamDao = teamDao;

        Team team1 = null;
        Team team2 = null;
        for (Game game: games){
            team1 = teamDao.queryForId(game.getTeam1().getName());
            team2 = teamDao.queryForId(game.getTeam2().getName());

            handleNumbers(team1, game.getTeam1Score(), team2, game.getTeam2Score(), teams);
        }
    }
    public void handleNumbers(Team team1, int score1, Team team2, int score2, List<Team> teams) throws SQLException {
        team1.setPlayed(team1.getPlayed()+1);
        team2.setPlayed(team2.getPlayed()+1);
//        Calculate wins, draws or lose
        calculateWIns(team1,score1,team2,score2);
//        GOALS FOR
        team1.setGoalsFor(team1.getGoalsFor()+score1);
        team2.setGoalsFor(team2.getGoalsFor()+score2);
//        GOALS AGAINST
        team1.setGoalsAgainst(team1.getGoalsAgainst()+score2);
        team2.setGoalsAgainst(team2.getGoalsAgainst()+score1);

//        GOAL DIFFERENTIAL
        calculateGoalDifferential(team1,team2);

//        POINTS
        calculatePoints(team1,team2);

//        POSITION
        teams.sort(new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {
                if ((o2.getPoints() - o1.getPoints()) == 0){
                    return o2.getGoalDifferential() - o1.getGoalDifferential();
                }else{
                    return o2.getPoints() - o1.getPoints();
                }
            }
        });
        calculatePositions(teams);
        teamDao.update(team1);
        teamDao.update(team2);
    }

    public void calculateWIns(Team team1, int score1, Team team2, int score2){
        if (score1 > score2){
            team1.setWins(team1.getWins()+1);
            team2.setLoses(team2.getLoses()+1);
        }else if (score1 == score2){
            team1.setDraws(team1.getDraws()+1);
            team2.setDraws(team2.getDraws()+1);
        }else{
            team1.setLoses(team1.getLoses()+1);
            team2.setWins(team2.getWins()+1);
        }
    }
    public void calculateGoalDifferential(Team team1, Team team2){
        team1.setGoalDifferential(team1.getGoalsFor()- team1.getGoalsAgainst());
        team2.setGoalDifferential(team2.getGoalsFor()- team2.getGoalsAgainst());
    }
    public void calculatePoints(Team team1,Team team2){
        team1.setPoints((team1.getWins()*3)+(team1.getDraws()*1));
        team2.setPoints((team2.getWins()*3)+(team2.getDraws()*1));

    }
    public void calculatePositions(List<Team> teams){

        for (Team t:teams){
            t.setPosition(teams.indexOf(t)+1);
        }

    }

}
