import java.util.ArrayList;
import java.util.List;
import redis.clients.jedis.Jedis;

public class RedisBroadcastTwitterAPI extends RedisTwitterAPI {

  private static Jedis jedis = new Jedis("localhost");

  @Override
  public void postTweet(Tweet t) {
    super.postTweet(t);
    List<String> followers = jedis.lrange("user:" + t.getUser_id(), 0, -1);
    for (String follower : followers) {
      jedis.lpush("timeline:" + follower, "tweet:" + t.getTweet_id() + ":user:" + t.getUser_id());
    }
  }

  // when broadcasting, keep track of who is following a particular user instead of who a particular user is following
  @Override
  public void addFollower(int user_id, int follower_id) {
    String key = "user:" + follower_id;
    jedis.lpush(key, Integer.toString(user_id));
  }

  @Override
  public List<Tweet> getTimeline(int user_id) {
    List<String> keys = jedis.lrange("timeline:" + user_id, 0, 10);
    List<Tweet> timeline = new ArrayList<>();
    for (String key : keys) {
      String tokens[] = key.split(":");
      Integer user = Integer.parseInt(tokens[tokens.length - 1]);
      String ts = jedis.hget(key, "ts");
      String text = jedis.hget(key, "text");
      Tweet t = new Tweet(user, ts, text);
      timeline.add(t);
    }
    return timeline;
  }

}
