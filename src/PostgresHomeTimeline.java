import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PostgresHomeTimeline extends APostgresConnector implements IHomeTimeline {

  private final int NUM_TIMELINES = 100;

  public static void main(String[] args) {
    // start timer
    long startTime = System.currentTimeMillis();

    // get database information from program arguments
    String username = args[0];
    String password = args[1];
    String database = args[2];

    // connect to database
    PostgresHomeTimeline pht = new PostgresHomeTimeline();
    Connection c = pht.connect(username, password, database);
    System.out.println("Connected to database");
    System.out.println();

    // print out all timelines
    for (int i = 0; i < pht.NUM_TIMELINES; i++) {
      int user = pht.findRandUser(c);
      System.out.println("Home timeline for user: " + user);
      ArrayList<String> timeline = pht.findTimeline(c, pht.findFollowers(c, user));
      for (int j = 0; j < timeline.size(); j++) {
        String tweet = timeline.get(j);
        System.out.println((j + 1) + ". " + tweet);
      }
      System.out.println();
    }

    // end timer and report runtime
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    System.out.println("data entry completed in: " + elapsedTime + " milliseconds");
  }

  @Override
  public int findRandUser(Connection c) {
    ResultSet rs = this.query(c, "select distinct user_id from tweets;");
    ArrayList<Integer> users = new ArrayList<>();
    try {
      while (rs.next()) {
        users.add(rs.getInt("user_id"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    int userID = users.get(new Random().nextInt(users.size()));
    return userID;
  }

  @Override
  public ArrayList<Integer> findFollowers(Connection c, int userID) {
    ResultSet rs = this.query(c, "select distinct follows_id from followers where user_id = " + userID + ";");
    ArrayList<Integer> followers = new ArrayList<>();
    try {
      while (rs.next()) {
        followers.add(rs.getInt("follows_id"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return followers;
  }

  @Override
  public ArrayList<String> findTimeline(Connection c, ArrayList<Integer> followers) {
    // find tweets and timestamps for each follower
    ArrayList<String> tweets = new ArrayList<>();
    ArrayList<Timestamp> ts = new ArrayList<>();
    try {
      for (Integer follower : followers) {
        ResultSet rs = this
            .query(c, "select tweet_ts, tweet_text from tweets where user_id = " + follower + ";");
        while (rs.next()) {
          tweets.add(rs.getString("tweet_text"));
          ts.add(rs.getTimestamp("tweet_ts"));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    // find the 10 min indexes in ts
    ArrayList<Integer> minIndexes = this.minTimestamps(10, ts);
    // return the tweets at those indexes
    return this.getRecentTweets(minIndexes, tweets);
  }

  // gets the n most recent timestamps given
  private ArrayList<Integer> minTimestamps(int n, ArrayList<Timestamp> ts) {
    int i = 0;
    ArrayList<Long> times = new ArrayList<>();
    for (Timestamp t : ts) {
      times.add(t.getTime());
    }
    ArrayList<Integer> indexes = new ArrayList<>();
    while (i < n) {
      int minIndexes = ts.indexOf(Collections.max(ts));
      ts.remove(minIndexes);
      indexes.add(minIndexes);
      i++;
    }
    return indexes;
  }

  // gets the tweets at the positions given by the indexes array
  private ArrayList<String> getRecentTweets(ArrayList<Integer> indexes, ArrayList<String> tweets) {
    ArrayList<String> recentTweets = new ArrayList<>();
    for (int index : indexes) {
      recentTweets.add(tweets.get(index));
    }
    return recentTweets;
  }
}