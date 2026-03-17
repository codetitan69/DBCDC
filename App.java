import java.sql.Connection;
import java.sql.SQLException;

public class App {
    public static void main(String[] args){
        Config.Load();
        Db.Load();
        try {
            Db.getSrc_First_Row(Config.getTABLES_AND_QUERIES().get("DB2ADMIN.TEST_EVENTS"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}