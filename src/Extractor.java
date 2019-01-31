import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Extractor {

  // used CSV read-in suggestions from: https://www.geeksforgeeks.org/reading-csv-file-java-using-opencv/
  private List<String[]> extract(String file) {
    List<String[]> readIn = null;
    try {
      // instantiate file reader object
      FileReader fr = new FileReader(file);

      // instantiate csv reader object
      CSVReader csvReader = new CSVReaderBuilder(fr).build();
      readIn = csvReader.readAll();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return readIn;
  }

  // converts the list of String[] to a list of Tweets
  public List<Tweet> getTweets(String file) {
    List<String[]> data = this.extract(file);
    ArrayList<Tweet> tweets = new ArrayList<>();
    for (String[] row : data) {
      Tweet t = new Tweet(Integer.parseInt(row[0]), Integer.parseInt(row[1]), row[2], row[3]);
      tweets.add(t);
    }
    return tweets;
  }

  // converts the list of String[] to a HashMap of followers
  public HashMap<Integer, ArrayList<Integer>> getFollowers(String file) {
    List<String[]> data = this.extract(file);
    HashMap<Integer, ArrayList<Integer>> followers = new HashMap<>();
    for (String[] row : data) {
      int user_id = Integer.parseInt(row[0]);
      int follower_id = Integer.parseInt(row[1]);
      if (followers.containsKey(user_id)) {
        followers.get(user_id).add(follower_id);
      } else {
        followers.put(user_id, new ArrayList<>());
        followers.get(user_id).add(follower_id);
      }
    }
    return followers;
  }
}
