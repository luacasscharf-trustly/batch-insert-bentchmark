package activejdbc.examples.simple;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

public class PlainJdbcTest {
    static final String DB_URL = "jdbc:postgresql://localhost:5433/movies_test?loggerLevel=TRACE";
    static final String USER = "root";
    static final String PASS = "password";

    private static final Integer INSERT_COUNT = 1_000;
    private static TimeController timer = new TimeController(INSERT_COUNT, "Plain JDBC Test");

    @After
    public void endTest() {
        timer.endTest();
    }

    @AfterClass
    public static void printTest() {
        timer.printTimers();
    }

    @Test
    public void batchInsert() throws Exception {
        timer.startTest("Batch Insert");

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement ps = conn
                        .prepareStatement("insert into people (id, name) values (nextval('seq'), ?)")) {
            for (int i = 0; i < INSERT_COUNT; i++) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Test
    public void manyInsert() throws Exception {
        timer.startTest("Many Insert");

        StringBuilder sb = new StringBuilder().append("insert into people (id, name) values");
        List<Object> params = new ArrayList<>();
        StringJoiner sj = new StringJoiner(",\n");

        for (int i = 0; i < INSERT_COUNT; i++) {
            sj.add(" (nextval('seq'), ?)");
            params.addAll(
                    Arrays.asList(UUID.randomUUID().toString()));
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sb.toString(), params.toArray(new String[1]));
            sb.append(sj + "\n");
        }
    }

}
