package github.cweijan.ultimate.transaction;

import java.sql.Connection;
import java.sql.SQLException;

public interface Transaction{
    /**
     * Retrieve inner database connection
     * @return DataBase connection
     */
    Connection getConnection() throws SQLException;

    /**
     * Commit inner database connection.
     */
    void commit() throws SQLException;

    /**
     * Rollback inner database connection.
     */
    void rollback() throws SQLException;

    /**
     * Close inner database connection.
     */
    void close() throws SQLException;

}
