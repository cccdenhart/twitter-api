import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

// represents a tweet
public class Tweet implements Comparable {

  private int tweet_id;
  private int user_id;
  private String tweet_ts;
  private String tweet_text;

  public Tweet(int tweet_id, int user_id, String tweet_ts, String tweet_text) {
    this.tweet_id = tweet_id;
    this.user_id = user_id;
    this.tweet_ts = tweet_ts;
    this.tweet_text = tweet_text;
  }

  public Tweet(int user_id, String tweet_ts, String tweet_text) {
    this.user_id = user_id;
    this.tweet_ts = tweet_ts;
    this.tweet_text = tweet_text;
  }

  @Override
  public String toString() {
    return "At " + this.tweet_ts + " user " + this.user_id + " posted:\n" + this.tweet_text;
  }

  public int getTweet_id() {
    return tweet_id;
  }

  public int getUser_id() {
    return user_id;
  }

  public String getTweet_ts() {
    return tweet_ts;
  }

  public String getTweet_text() {
    return tweet_text;
  }

  public Timestamp convertTimestamp() {
    Timestamp ts = Timestamp.valueOf(this.tweet_ts);
    return ts;
  }

  @Override
  public int compareTo(Object o) {
    return this.convertTimestamp().compareTo(((Tweet) o).convertTimestamp());
  }
}
