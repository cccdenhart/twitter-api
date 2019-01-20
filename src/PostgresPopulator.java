import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileReader;
import java.sql.Connection;
import java.util.List;

public class PostgresPopulator extends APostgresConnector implements IPopulator {

  public static void main(String[] args) {
    // start timer
    long startTime = System.currentTimeMillis();

    // get database information from program arguments
    String username = args[0];
    String password = args[1];
    String database = args[2];

    // connect to database and populate it
    PostgresPopulator pp = new PostgresPopulator();
    Connection c = pp.connect(username, password, database);
    System.out.println("Inserting followers data to postgreSQL...");
    pp.insertMany(c, false, pp.extract("./followers.csv"));
    System.out.println("Inserting tweets data to postgreSQL...");
    pp.insertMany(c, true, pp.extract("./tweets.csv"));

    // end timer and report runtime
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    System.out.println("data entry completed in: " + elapsedTime + " milliseconds");
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

  @Override
  public void insertMany(Connection c, boolean isTweet, List<String[]> data) {
    if (isTweet) {
      for (String[] row : data) {
        String sql = "INSERT INTO tweets (tweet_id, user_id, tweet_ts, tweet_text) VALUES (" + row[0] + ", " + row[1] + ",'" + row[2] + "','" + row[3] + "');";
        this.insert(c, sql);
      }
    } else {
      for (String[] row : data) {
        String sql = "INSERT INTO followers (user_id, follows_id) VALUES (" + row[0] + ", " + row[1] + ");";
        this.insert(c, sql);
      }
    }
  }
}
