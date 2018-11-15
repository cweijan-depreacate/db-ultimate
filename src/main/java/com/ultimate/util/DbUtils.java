package com.ultimate.util;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class DbUtils{

    private static final Logger logger = Log.getLogger();

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
