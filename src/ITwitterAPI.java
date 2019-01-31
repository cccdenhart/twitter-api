import java.util.List;

public interface ITwitterAPI {
  void postTweet(Tweet t);
  void addFollower(int user_id, int follower_id);
  List<Tweet> getTimeline(int user_id);
  int findRandUser();
}
