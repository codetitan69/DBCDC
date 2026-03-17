import java.sql.Connection;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException {
        Config.Load();
        Db.Load();
        Esp.start();
    }
}