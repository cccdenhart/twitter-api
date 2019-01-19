import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

public class PostgresPopulator implements IPopulator {

  // contains data read from the CSV file as a (sort of) nested Array
  private List<String[]> followers;
  private List<String[]> tweets;

  // constructor
  public PostgresPopulator(String followersFile, String tweetsFile) {
    this.followers = this.extract(followersFile);
    this.tweets = this.extract(tweetsFile);
  }

  // used CSV read-in suggestions from: https://www.geeksforgeeks.org/reading-csv-file-java-using-opencv/
  @Override
  public List<String[]> extract(String file) {
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

  private void followersInsert(Connection c, int userID, int followsID) {
    try {
      Statement stmt = c.createStatement();
      String sql = "INSERT INTO followers (user_id, follows_id) VALUES (" + userID + ", " + followsID + ");";
      stmt.executeUpdate(sql);
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }

  private void tweetInsert(Connection c, int tweetID, int userID, String ts, String text) {
    try {
      Statement stmt = c.createStatement();
      String sql = "INSERT INTO tweets (tweet_id, user_id, tweet_ts, tweet_text) VALUES (" + tweetID + ", " + userID + ",'" + ts + "','" + text + "');";
      stmt.executeUpdate(sql);
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }

  private void insertMany(Connection c, boolean isTweet) {
    List<String[]> data = this.followers;
    if (isTweet) {
      data = this.tweets;
    }
    for (String[] row : data) {
      if (isTweet) {
        this.tweetInsert(c, Integer.parseInt(row[0]), Integer.parseInt(row[1]), row[2], row[3]);
      } else {
        this.followersInsert(c, Integer.parseInt(row[0]), Integer.parseInt(row[1]));
      }
    }
  }

  public static void main(String args[]) {
    long startTime = System.currentTimeMillis();
    System.out.println("Trying to connect to database...");
    PostgresPopulator pp = new PostgresPopulator("./followers.csv", "./tweets.csv");
    Connection c = null;
    try {
      c = DriverManager
          .getConnection("jdbc:postgresql://localhost:5432/ds4300_hw01",
              "cccdenhart", "1Charles");
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    System.out.println("Opened database successfully");
    System.out.println("Inserting followers data to postgreSQL...");
    pp.insertMany(c, false);
    System.out.println("Inserting tweets data to postgreSQL...");
    pp.insertMany(c, true);
    long stopTime = System.currentTimeMillis();
    float elapsedTime = (stopTime - startTime) / 1000;
    System.out.println("data entry completed in: " + elapsedTime + " seconds");
  }
}
