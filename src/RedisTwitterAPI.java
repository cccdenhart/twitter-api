import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import redis.clients.jedis.Jedis;

public class RedisTwitterAPI implements ITwitterAPI {

  static Jedis jedis = new Jedis("localhost");

  @Override
  public void postTweet(Tweet t) {
    Map<String, String> tweet_map = new HashMap<>();
    String key = "tweet:" + t.getTweet_id() + ":user:" + t.getUser_id();
    tweet_map.put("ts", t.getTweet_ts());
    tweet_map.put("text", t.getTweet_text());
    jedis.hmset(key, tweet_map);
  }

  @Override
  public void addFollower(int user_id, int follower_id) {
    String key = "user:" + user_id;
    jedis.lpush(key, Integer.toString(follower_id));
  }

  @Override
  public List<Tweet> getTimeline(int user_id) {
    List<Integer> followers = this.getFollowers(user_id);
    List<Tweet> allTweets = new ArrayList<>();
    for (Integer follower : followers) {
      List<Tweet> followerTweets = this.getTweets(follower);
      allTweets.addAll(followerTweets);
    }
    List<Tweet> minTweets = this.getMinTweets(allTweets);
    return minTweets;
  }

  private List<Integer> getFollowers(int user_id) {
    List<String> lrange = jedis.lrange(Integer.toString(user_id), 0, -1);
    ArrayList<Integer> followers = new ArrayList<>();
    for (String f : lrange) {
      followers.add(Integer.parseInt(f));
    }
    return followers;
  }

  private List<Tweet> getTweets(int user_id) {
    List<String> ts = jedis.hmget("tweet:*:user:" + user_id, "ts");
    List<String> text = jedis.hmget("tweet:*:user:" + user_id, "text");
    ArrayList<Tweet> tweets = new ArrayList<>();
    for (int i = 0; i < ts.size(); i++) {
      String time = ts.get(i);
      String tweet_text = text.get(i);
      Tweet t = new Tweet(user_id, time, tweet_text);
      tweets.add(t);
    }
    return tweets;
  }

  private List<Tweet> getMinTweets(List<Tweet> tweets) {
    Collections.sort(tweets);
    return tweets.subList(0, 10);

  }

  @Override
  public int findRandUser() {
    Set<String> users = jedis.keys("user:*");
    ArrayList<Integer> user_ids = new ArrayList<>();
    for (String u : users) {
      String key = u.substring(u.indexOf(":"));
      user_ids.add(Integer.parseInt(key));
    }
    int user_id = user_ids.get(new Random().nextInt(users.size()));
    return user_id;
  }
}
