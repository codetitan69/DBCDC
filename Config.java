import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Config {
    private static String SRC_DB_URL;
    private static String SRC_DB_USER;
    private static String SRC_DB_PASS;

    private static String DEST_DB_URL;
    private static String DEST_DB_USER;
    private static String DEST_DB_PASS;

    private static Map<String,String> TABLES_AND_QUERIES;

    public static void Load() {
        Dotenv dotenv = Dotenv.load();

        SRC_DB_URL = dotenv.get("SRC_DB_URL");
        SRC_DB_USER = dotenv.get("SRC_DB_USER");
        SRC_DB_PASS = dotenv.get("SRC_DB_PASS");

        DEST_DB_URL = dotenv.get("DEST_DB_URL");
        DEST_DB_USER = dotenv.get("DEST_DB_USER");
        DEST_DB_PASS = dotenv.get("DEST_DB_PASS");

        String Table_and_queries = dotenv.get("QUERY");

        ArrayList<String> query = Arrays.stream(Table_and_queries .split(","))
                .map((String s) -> {return s.replaceAll("[()]","");})
                .map(String::trim)
                .filter((String s) -> {return !s.isEmpty();})
                .collect(Collectors.toCollection(ArrayList::new));

        TABLES_AND_QUERIES = new HashMap<>();

        for (String q : query) {
            TABLES_AND_QUERIES.put(q.split(">")[0].trim(),q.split(">")[1].trim());
        }

        System.out.println("Config Loaded");
    }

    public static String getSRC_DB_URL() {return SRC_DB_URL;}
    public static String getSRC_DB_USER() {return SRC_DB_USER;}
    public static String getSRC_DB_PASS() {return SRC_DB_PASS;}
    public static String getDEST_DB_URL() {return DEST_DB_URL;}
    public static String getDEST_DB_USER() {return DEST_DB_USER;}
    public static String getDEST_DB_PASS() {return DEST_DB_PASS;}
    public static Map<String,String> getTABLES_AND_QUERIES() {return TABLES_AND_QUERIES;}
}