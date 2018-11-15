package com.ultimate.db.config;

import com.ultimate.db.HikariDataSourceAdapter;
import com.ultimate.util.Log;
import com.ultimate.util.StringUtils;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DbConfig{

    private static Logger logger = Log.getLogger();
    private String url;
    private String username;
    private String password;
    private String driver;
    private boolean createNonexistsTable;
    private DataSource dataSource;

    public Connection getConnection(){

        if(StringUtils.isEmpty(url)){
            throw new IllegalArgumentException("jdbcUrl must be not empty!");
        }

        if(dataSource == null){
            logger.debug("dataSource is null! init hikariDataSource");
            HikariDataSourceAdapter dataSourceAdapter = new HikariDataSourceAdapter(url, username, password, driver);
            dataSource = dataSourceAdapter.getDataSource();
        }

        Connection connection = null;
        try{
            connection = dataSource.getConnection();
        } catch(SQLException e){
            Log.getLogger().error("get jdbc connection fail!", e);
        }

        return connection;
    }

    public DataSource getDataSource(){

        return dataSource;
    }

    public void setDataSource(DataSource dataSource){

        this.dataSource = dataSource;
    }

    public String getDriver(){

        return driver;
    }

    public void setDriver(String driver){

        this.driver = driver;
    }

    public String getUrl(){

        return url;
    }

    public boolean isCreateNonexistsTable(){

        return createNonexistsTable;
    }

    public void setCreateNonexistsTable(boolean createNonexistsTable){

        this.createNonexistsTable = createNonexistsTable;
    }

    public void setUrl(String url){

        this.url = url;
    }

    public String getUsername(){

        return username;
    }

    public void setUsername(String username){

        this.username = username;
    }

    public String getPassword(){

        return password;
    }

    public void setPassword(String password){

        this.password = password;
    }
}
