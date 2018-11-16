package com.ultimate.util;

import org.slf4j.Logger;

import java.sql.*;

public class DbUtils{

    private static final Logger logger = Log.getLogger();

    /**
     Get table all column metadata
     */
    public static ResultSetMetaData getTableMetaData(String tableName, Connection connection){

        checkConnectionAlive(connection);
        try{
            PreparedStatement preparedStatement = connection.prepareStatement("select * from " + tableName);
            preparedStatement.executeQuery();
            return preparedStatement.getMetaData();
        } catch(Exception e){
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     Check datbase connection is alive
     */
    public static void checkConnectionAlive(Connection connection){

        try{
            if(connection == null || connection.isClosed()){
                throw new IllegalArgumentException("connection is valid!");
            }
        } catch(SQLException e){
            logger.error(e.getMessage(), e);
        }
    }

    /**
     Close connection
     */
    public static void closeConnection(Connection connection){

        if(connection != null){
            try{
                if(!connection.isClosed()){
                    connection.close();
                    if(logger.isDebugEnabled()){
                        logger.debug("closed connection " + connection);
                    }
                }
            } catch(SQLException e){
                logger.error(e.getMessage(), e);
            }
        }

    }

}
