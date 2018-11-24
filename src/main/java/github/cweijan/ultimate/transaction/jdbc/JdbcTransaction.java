package github.cweijan.ultimate.transaction.jdbc;

import github.cweijan.ultimate.transaction.Transaction;
import github.cweijan.ultimate.util.Log;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JdbcTransaction implements Transaction{

    private static Logger log = Log.INSTANCE.getLogger();
    protected Connection connection;
    protected DataSource dataSource;
    protected boolean autoCommit;

    public JdbcTransaction(Connection connection){

        this.connection = connection;
    }

    public JdbcTransaction(DataSource ds, boolean desiredAutoCommit){

        dataSource = ds;
        autoCommit = desiredAutoCommit;
    }

    @Override
    public Connection getConnection() throws SQLException{

        if(connection == null){
            openConnection();
        }
        return connection;
    }

    @Override
    public void commit() throws SQLException{

        if(connection != null && !connection.getAutoCommit()){
            if(log.isDebugEnabled()){
                log.debug("Committing JDBC Connection [" + connection + "]");
            }
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException{

        if(connection != null && !connection.getAutoCommit()){
            if(log.isDebugEnabled()){
                log.debug("Rolling back JDBC Connection [" + connection + "]");
            }
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException{

        if(connection != null){
            resetAutoCommit();
            if(log.isDebugEnabled()){
                log.debug("Closing JDBC Connection [" + connection + "]");
            }
            connection.close();
        }
    }

    protected void setDesiredAutoCommit(boolean desiredAutoCommit){

        try{
            if(connection.getAutoCommit() != desiredAutoCommit){
                if(log.isDebugEnabled()){
                    log.debug("Setting autocommit to " + desiredAutoCommit + " on JDBC Connection [" + connection + "]");
                }
                connection.setAutoCommit(desiredAutoCommit);
            }
        } catch(SQLException e){
            // Only a very poorly implemented driver would fail here,
            // and there's not much we can do about that.
            throw new RuntimeException("Error configuring AutoCommit.  " + "Your driver may not support getAutoCommit() or setAutoCommit(). " + "Requested setting: " + desiredAutoCommit + ".  Cause: " + e, e);
        }
    }

    protected void resetAutoCommit(){

        try{
            if(!connection.getAutoCommit()){
                // MyBatis does not call commit/rollback on a connection if just selects were performed.
                // Some databases start transactions with select statements
                // and they mandate a commit/rollback before closing the connection.
                // A workaround is setting the autocommit to true before closing the connection.
                // Sybase throws an exception here.
                if(log.isDebugEnabled()){
                    log.debug("Resetting autocommit to true on JDBC Connection [" + connection + "]");
                }
                connection.setAutoCommit(true);
            }
        } catch(SQLException e){
            if(log.isDebugEnabled()){
                log.debug("Error resetting autocommit to true " + "before closing the connection.  Cause: " + e);
            }
        }
    }

    protected void openConnection() throws SQLException{

        if(log.isDebugEnabled()){
            log.debug("Opening JDBC Connection");
        }
        connection = dataSource.getConnection();
        setDesiredAutoCommit(autoCommit);
    }

}
