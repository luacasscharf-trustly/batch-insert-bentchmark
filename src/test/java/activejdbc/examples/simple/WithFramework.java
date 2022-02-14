package activejdbc.examples.simple;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.UUID;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.test.DBSpec;
import org.junit.AfterClass;
import org.junit.Test;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.postgresql.jdbc.PgConnection;

import com.mysema.query.sql.PostgresTemplates;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.dml.SQLInsertClause;

public class WithFramework extends DBSpec {

	private static final Integer INSERT_COUNT = 1;
	private static TimeController timer = new TimeController(INSERT_COUNT, "With Framework Test");

	@AfterClass
	public static void printTest() {
		timer.printTimers();
	}

	@Test
	public void batchInsert() throws Exception {
		Base.openTransaction();
		timer.startTest("Batch insert and reWriteBatchedInserts = false");

		PreparedStatement ps = Base.startBatch("insert into people (id, name) values (nextval('seq'), ?)");
		for (int i = 0; i < INSERT_COUNT; i++) {
			Base.addBatch(ps, UUID.randomUUID().toString());
		}

		Base.executeBatch(ps);
		timer.endTest();
		
		Base.commitTransaction();
	}

	@Test
	public void reWriteBatchedInserts() throws Exception {
		Base.close();

		Properties props = new Properties();
		// props.put("user", "root");
		// props.put("password", "password");
		// props.put("reWriteBatchedInserts", "true");
		props.put("loggerLevel", "TRACE");

		// DB db = Base.open("org.postgresql.Driver",
		// "jdbc:postgresql://localhost:5433/movies_test?reWriteBatchedInserts=true",
		// props);
		DB db = Base.open("org.postgresql.Driver",
				"jdbc:postgresql://localhost:5433/movies_test?reWriteBatchedInserts=true", "root", "password");
		Connection connection = db.connection();
		PgConnection pgConnection = (PgConnection) connection;

		db.openTransaction();
		timer.startTest("Batch insert and reWriteBatchedInserts = true");

		PreparedStatement ps = Base.startBatch("insert into people (id, name) values (nextval('seq'), ?)");
		for (int i = 0; i < INSERT_COUNT; i++) {
			db.addBatch(ps, UUID.randomUUID().toString());
		}

		db.executeBatch(ps);
		timer.endTest();

		db.commitTransaction();
	}

	@Test
	public void manyInsert() throws Exception {
		Base.openTransaction();

		StringBuilder sb = new StringBuilder().append("insert into people (id, name) values");
		List<Object> params = new ArrayList<>();
		StringJoiner sj = new StringJoiner(",\n");

		for (int i = 0; i < INSERT_COUNT; i++) {
			sj.add(" (nextval('seq'), ?)");
			params.addAll(
					Arrays.asList(UUID.randomUUID().toString()));
		}
		sb.append(sj + "\n");

		timer.startTest("Many Insert");
		Base.exec(sb.toString(), params.toArray());
		timer.endTest();

		Base.commitTransaction();
	}

	@Test
	public void insertWithFramework() throws Exception {
		Base.openTransaction();

		timer.startTest("Insert with framework Model.saveIt");
		for (int i = 0; i < INSERT_COUNT; i++) {
			new Person(UUID.randomUUID().toString()).saveIt();
		}
		timer.endTest();

		Base.commitTransaction();
	}

	@Test
	public void simpleInsert() throws Exception {
		Base.openTransaction();

		timer.startTest("Insert with framework Base.exec");
		for (int i = 0; i < INSERT_COUNT; i++) {
			Base.exec("insert into people (id, name) values (nextval('seq'), ?)", UUID.randomUUID());
		}
		timer.endTest();

		Base.commitTransaction();
	}

	@Test
	public void copyManager() throws Exception {
		Base.openTransaction();
		Base.exec("delete from people");
		Base.commitTransaction();

		Base.openTransaction();
		Connection connection = Base.connection();
		CopyManager copyManager = new CopyManager((BaseConnection) connection);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < INSERT_COUNT; i++) {
			sb.append(i + ";" + UUID.randomUUID().toString() + "\r\n");
		}

		timer.startTest("Copy manager with ID hardcoded");
		copyManager.copyIn("COPY public.people FROM STDIN WITH DELIMITER ';'", new StringReader(sb.toString()));
		timer.endTest();

		Base.commitTransaction();
	}

	@Test
	public void copyManagerWithoutId() throws Exception {
		Base.openTransaction();
		Connection connection = Base.connection();
		CopyManager copyManager = new CopyManager((BaseConnection) connection);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < INSERT_COUNT; i++) {
			sb.append(UUID.randomUUID().toString() + "\r\n");
		}

		timer.startTest("Copy manager with ID using generator");
		copyManager.copyIn("COPY public.people(name) FROM STDIN WITH DELIMITER ';'", new StringReader(sb.toString()));
		timer.endTest();

		Base.commitTransaction();
	}

	@Test
	public void usingQueryDsl() throws Exception {
		Base.close();

		DB db = Base.open("org.postgresql.Driver",
				"jdbc:postgresql://localhost:5433/movies_test?reWriteBatchedInserts=true", "root", "password");
		Connection connection = db.connection();
		db.openTransaction();
		PersonQueryDsl personQueryDsl = new PersonQueryDsl("r");
		SQLInsertClause sqlInsert = new SQLInsertClause(connection, new PostgresTemplates(), personQueryDsl);

		for (int i = 0; i < INSERT_COUNT; i++) {
			sqlInsert//
					.set(personQueryDsl.id, SQLExpressions.nextval(Integer.class, "seq"))//
					.set(personQueryDsl.name, UUID.randomUUID().toString());
			sqlInsert.addBatch();
		}

		timer.startTest("QueryDSL batch and reWriteBatchedInserts = true");
		sqlInsert.execute();
		timer.endTest();
		db.commitTransaction();
	}

	@Test
	public void usingQueryDslWithoutReWriteBatchedQueries() throws Exception {
		Base.openTransaction();
		PersonQueryDsl personQueryDsl = new PersonQueryDsl("r");
		SQLInsertClause sqlInsert = new SQLInsertClause(Base.connection(), new PostgresTemplates(), personQueryDsl);

		for (int i = 0; i < INSERT_COUNT; i++) {
			sqlInsert//
					.set(personQueryDsl.id, SQLExpressions.nextval(Integer.class, "seq"))//
					.set(personQueryDsl.name, UUID.randomUUID().toString());
			sqlInsert.addBatch();
		}

		timer.startTest("QueryDSL batch and reWriteBatchedInserts = false");
		sqlInsert.execute();
		timer.endTest();
		Base.commitTransaction();
	}

}
