import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Db {
    private static Connection SRC_DB_CONN;
    private static Connection DEST_DB_CONN;

    static {
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Load() {
        try {
            SRC_DB_CONN = DriverManager.getConnection(
                    Config.getSRC_DB_URL(),
                    Config.getSRC_DB_USER(),
                    Config.getSRC_DB_PASS()
            );

            DEST_DB_CONN = DriverManager.getConnection(
                    Config.getDEST_DB_URL(),
                    Config.getDEST_DB_USER(),
                    Config.getDEST_DB_PASS()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Db connections successful");
    }


    public static Map<String,Object> getSrc_Row_Schema(String query) throws SQLException {
        query = query.replaceAll(";","") + " FETCH FIRST 1 ROW ONLY";

        PreparedStatement pstmt = SRC_DB_CONN.prepareStatement(query);
        boolean status = pstmt.execute();
        ResultSet result = pstmt.getResultSet();

        ResultSetMetaData meta = result.getMetaData();
        int columnCount = meta.getColumnCount();


        Map<String,Object> column_data_map = new HashMap<>();
        while(result.next()){
            for (int i = 1; i <= columnCount; i++) {

                String colName = meta.getColumnName(i);
                Object value = result.getObject(i);

                column_data_map.put(colName,value.getClass());
            }
        }
        return column_data_map;
    }

    public static Map<String,ResultSet> getSrcQueryResultSets(){
        Map<String,ResultSet> res = new HashMap<>();
        Config.getTABLES_AND_QUERIES().forEach((String t,String v) -> {
            try {
                PreparedStatement stmt = SRC_DB_CONN.prepareStatement(v);
                stmt.execute();
                ResultSet result = stmt.getResultSet();
                res.put(t,result);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return res;
    }

    public static Map<String,ArrayList<Map<String,Object>>> getSrcResultMaps(Map<String,ResultSet> resultSetMap){

        Map<String,ArrayList<Map<String,Object>>> mp = new HashMap<>();
        resultSetMap.forEach(
                (table_name,resultSet) -> {

                    try {
                        ResultSetMetaData meta = resultSet.getMetaData();
                        int columnCount = meta.getColumnCount();

                        ArrayList<Map<String,Object>> table_rows = new ArrayList<>();

                        while(!resultSet.next()){
                            Map<String,Object> row = new HashMap<>();
                            for (int i = 1; i <= columnCount; i++) {

                                String colName = meta.getColumnName(i);
                                Object value = resultSet.getObject(i);

                                row.put(colName,value);
                            }
                            table_rows.add(row);
                        }

                        mp.put(table_name,table_rows);
                    } catch (RuntimeException | SQLException e){
                            throw new RuntimeException(e);
                    }
                }
        );

        return mp;
    }

    public static Connection getDEST_DB_CONN() {return DEST_DB_CONN;}
    public static Connection getSRC_DB_CONN() {return SRC_DB_CONN;}
}