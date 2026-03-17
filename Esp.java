import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.EPRuntime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Esp {
    public static EPRuntime runtime;

    public static void start() throws SQLException {
        EPCompiler compiler = EPCompilerProvider.getCompiler();
        Configuration configuration = new Configuration();

        Map<String,Map<String,Object>> schemas_map = getSchemaMaps();

        schemas_map.forEach((eventname,schema_map) -> {
            configuration.getCommon().addEventType(eventname,schema_map);
        });

        CompilerArguments argss = new CompilerArguments(configuration);

        ArrayList<String> statements = getSelectStmtsForEvents(schemas_map);

        EPCompiled epCompiled;
        epCompiled = compiler.compile("@name('my-statement') select id,name,ts from TestEvent", argss);


    }

    public static Map<String,Map<String,Object>> getSchemaMaps(){
        Map<String,String> query_map = Config.getTABLES_AND_QUERIES();

        Map<String,Map<String,Object>> schemas_map = new HashMap<>();

        query_map.forEach((key,value) ->{
            try {
                schemas_map.put(key,Db.getSrc_Row_Schema(value));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return schemas_map;
    }

    public static ArrayList<String> getSelectStmtsForEvents(Map<String,Map<String,Object>> schema_map){
        ArrayList<String> stmts = new ArrayList<>();
        schema_map.forEach((eventname,schema) -> {
            String stmt = "@name";
            stmt = stmt + "('" + eventname + "-stmt" + "')" + "select * from " + eventname;
            stmts.add(stmt);
        });

        return stmts;
    }
}