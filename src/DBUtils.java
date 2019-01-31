import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

// abstract class for connecting to a postgres database
public class DBUtils {

  private Connection conn;
  private String username;
  private String password;
  private String database;

  public DBUtils (String username, String password, String database) {
    this.database = database;
    this.username = username;
    this.password = password;
    this.connect();
  }



  private void connect () {
    System.out.println("Trying to connect to database...");
    try {
      this.conn = DriverManager
          .getConnection("jdbc:postgresql://localhost:5432/" + this.database,
              this.username, this.password);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    System.out.println("Opened database successfully");
  }

  public void insert (String sql) {
    try {
      Statement stmt = this.conn.createStatement();
      stmt.executeUpdate(sql);
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }

  public ResultSet query (String sql) {
    ResultSet rs = null;
    try {
      Statement stmt = this.conn.createStatement();
      rs = stmt.executeQuery(sql);
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    return rs;
  }
}
