import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.EPRuntime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class Esp {
    public EPRuntime runtime;

    public void start(Db db) throws SQLException {
        EPCompiler compiler = EPCompilerProvider.getCompiler();
        Configuration configuration = new Configuration();


    }
}