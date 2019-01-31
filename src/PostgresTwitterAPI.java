import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PostgresTwitterAPI implements ITwitterAPI {

  private DBUtils db;

  public PostgresTwitterAPI(String username, String password, String database) {
    this.db = new DBUtils(username, password, database);
  }

  @Override
  public void postTweet(Tweet t) {
    String sql =
        "INSERT INTO tweets (tweet_id, user_id, tweet_ts, tweet_text) VALUES (" + t.getTweet_id()
            + ", " + t.getUser_id() + ",'" + t.getTweet_ts() + "','" + t.getTweet_text() + "');";
    this.db.insert(sql);
  }

  @Override
  public void addFollower(int user_id, int follower_id) {
    String sql =
        "INSERT INTO followers (user_id, follows_id) VALUES (" + user_id + ", " + follower_id
            + ");";
    this.db.insert(sql);
  }

  @Override
  public List<Tweet> getTimeline(int user_id) {
    // find tweets and timestamps for each follower
    List<Integer> followers = this.getFollowers(user_id);
    ArrayList<Integer> tweets = new ArrayList<>();
    ArrayList<Timestamp> ts = new ArrayList<>();
    try {
      for (Integer follower : followers) {
        ResultSet rs = this.db
            .query("select tweet_ts, tweet_text from tweets where user_id = " + follower + ";");
        while (rs.next()) {
          tweets.add(rs.getInt("tweet_id"));
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

  private List<Integer> getFollowers(int user_id) {
    ResultSet rs = this.db
        .query("select distinct follows_id from followers where user_id = " + user_id + ";");
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

  // gets the tweets at the positions given by the indexes array
  private ArrayList<Tweet> getRecentTweets(ArrayList<Integer> indexes, ArrayList<Integer> tweets) {
    ArrayList<Integer> tweet_ids = new ArrayList<>();
    for (int index : indexes) {
      tweet_ids.add(tweets.get(index));
    }
    ArrayList<Tweet> recentTweets = this.getTweets(tweet_ids);
    return recentTweets;
  }

  private ArrayList<Tweet> getTweets(ArrayList<Integer> tweet_ids) {
    ArrayList<Tweet> tweets = new ArrayList<>();
    for (int tweet_id : tweet_ids) {
      ResultSet rs = this.db
          .query("select tweet_ts from tweets where tweet_id = " + tweet_id + ";");
      try {
        while (rs.next()) {
          int user_id = rs.getInt("user_id");
          String ts = rs.getString("tweet_ts");
          String text = rs.getString("tweet_text");
          Tweet t = new Tweet(tweet_id, user_id, ts, text);
          tweets.add(t);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return tweets;
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

  // gets a random user out of all users in the database
  @Override
  public int findRandUser() {
    ResultSet rs = this.db.query("select distinct user_id from tweets;");
    ArrayList<Integer> users = new ArrayList<>();
    try {
      while (rs.next()) {
        users.add(rs.getInt("user_id"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    int user_id = users.get(new Random().nextInt(users.size()));
    return user_id;
  }
}
