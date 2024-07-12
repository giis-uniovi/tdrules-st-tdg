package market.service.impl;

import org.springframework.context.ApplicationContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
 
/* 
 * New class for test: reset the autoincrement sequences in tables of database
 */
public final class DbTestUtil {
 
    private DbTestUtil() {}
 
    public static void resetAutoIncrementColumns(ApplicationContext applicationContext,
                                                 String... tableNames) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        Connection dbConnection = dataSource.getConnection();
        
        //Create and invoke SQL statements that reset the auto increment columns
        for (String resetSqlArgument: tableNames) {
        	Statement statement = dbConnection.createStatement();
        	String resetSql = "TRUNCATE TABLE "+resetSqlArgument+" RESTART IDENTITY";
            statement.execute(resetSql);
        }
        dbConnection.close();
    }
 }