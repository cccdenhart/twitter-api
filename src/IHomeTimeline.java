import java.sql.Connection;
import java.util.ArrayList;

// repeatedly picks a random user and returns that user’s home timeline
public interface IHomeTimeline {

  // finds a random user
  int findRandUser(Connection c);

  // finds the followers of a given user
  ArrayList<Integer> findFollowers(Connection c, int userID);

  // finds the ten most recent tweets posted by a set of users
  ArrayList<String> findTimeline(Connection c, ArrayList<Integer> followers);
}
