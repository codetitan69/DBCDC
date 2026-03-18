import com.espertech.esper.compiler.client.EPCompileException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import static java.lang.Thread.sleep;

public class App {
    public static void main(String[] args) throws SQLException, EPCompileException {
        Config.Load();
        Db.Load();
        Esp.start();

        while (true){
            Map<String, ResultSet> resultsetmap = Db.getSrcQueryResultSets();
            resultsetmap.forEach((Str,v) -> {System.out.println(Str + v.toString());});
            Map<String, ArrayList<Map<String,Object>>> qrResults = Db.getSrcResultMaps(resultsetmap);

            System.out.println("results from table extracted");
            Esp.sendEvents(qrResults);

            System.out.println("events sent to runtime");

            try {
                sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}