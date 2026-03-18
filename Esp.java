import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.EPDeployException;
import com.espertech.esper.runtime.client.EPDeployment;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPRuntimeProvider;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Esp {
    public static ArrayList<EPDeployment> deployments;
    public static EPRuntime runtime;
    public static EPCompiler compiler;

    public static void start() throws SQLException, EPCompileException {
        compiler = EPCompilerProvider.getCompiler();
        Configuration configuration = new Configuration();

        Map<String,Map<String,Object>> schemas_map = getSchemaMaps();

        schemas_map.forEach((eventname,schema_map) -> {
            configuration.getCommon().addEventType(eventname,schema_map);
        });
        System.out.println("events registered succesfully");

        CompilerArguments argss = new CompilerArguments(configuration);
        ArrayList<String> statements = getSelectStmtsForEvents(schemas_map);
        ArrayList<EPCompiled> cpld_stmts = getCompiledStmts(statements,argss);

        System.out.println("statements compiled succesfully");

        deployments = deploymentsStmts(cpld_stmts,configuration);
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

    public static ArrayList<EPCompiled> getCompiledStmts(ArrayList<String> stmts,CompilerArguments args) throws EPCompileException {
        ArrayList<EPCompiled> compiled_list = new ArrayList<>();
        for (String stmt : stmts){
            compiled_list.add(compiler.compile(stmt,args));
        }

        return compiled_list;
    }

    public static ArrayList<EPDeployment> deploymentsStmts(ArrayList<EPCompiled> copiled_stmts,Configuration config){
        EPRuntime rtime = EPRuntimeProvider.getDefaultRuntime(config);
        ArrayList<EPDeployment> deployments = new ArrayList<>();

        for (EPCompiled c:copiled_stmts){
            try {
                deployments.add(rtime.getDeploymentService().deploy(c));
                System.out.println("deployed_successfully");
            }
            catch (EPDeployException ex) {
                throw new RuntimeException(ex);
            }
        }

        runtime = rtime;
        return deployments;
    }

    public static void sendEvents(Map<String,ArrayList<Map<String,Object>>> tableResults){
        tableResults.forEach(
                (eventName,tableRes) -> {
                    for(Map<String,Object> row : tableRes){
                        runtime.getEventService().sendEventMap(row, eventName);
                    }
                }
        );
    }
}