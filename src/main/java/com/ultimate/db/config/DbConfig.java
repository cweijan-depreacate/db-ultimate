package com.ultimate.db.config;

import com.ultimate.db.HikariDataSourceAdapter;
import com.ultimate.util.Log;
import com.ultimate.util.StringUtils;
import org.slf4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@ConfigurationProperties(prefix = "ultimate.jdbc")
public class DbConfig{

    private static final Logger logger = Log.getLogger();

    private DataSource dataSource;
    private boolean createNonexistsTable;

    private String url;
    private String username;
    private String password;
    private String driver;
    private Integer maximumPoolSize;
    private Integer minimumIdle;

    public Connection openConnection(){

        return openConnection(true);
    }

    public Connection openConnection(boolean autoCommit){

        if(StringUtils.isEmpty(url)){
            throw new IllegalArgumentException("JDBC_URL must be not empty!");
        }

        if(dataSource == null){
            if(logger.isDebugEnabled()){
                logger.debug("dataSource is null! init hikariDataSource");
            }
            HikariDataSourceAdapter dataSourceAdapter = new HikariDataSourceAdapter(this);
            dataSource = dataSourceAdapter.getDataSource();
        }

        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            connection.setAutoCommit(autoCommit);
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

        if(driver==null)return DefaultProperties.DEFAULT_DRIVER;

        return driver;
    }

    public Integer getMaximumPoolSize(){

        if(maximumPoolSize==null)return DefaultProperties.MAXIUM_POOL_SIZE;

        return maximumPoolSize;
    }

    public void setMaximumPoolSize(Integer maximumPoolSize){

        this.maximumPoolSize = maximumPoolSize;
    }

    public Integer getMinimumIdle(){

        if(minimumIdle==null)return DefaultProperties.MINIUM_IDEL_SIZE;

        return minimumIdle;
    }

    public void setMinimumIdle(Integer minimumIdle){

        this.minimumIdle = minimumIdle;
    }

    public void setDriver(String driver){

        this.driver = driver;
    }

    public String getUrl(){

        if(url==null)return DefaultProperties.JDBC_URL;

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

        if(username==null)return DefaultProperties.USERNAME;

        return username;
    }

    public void setUsername(String username){

        this.username = username;
    }

    public String getPassword(){

        if(password==null)return DefaultProperties.PASSWORD;

        return password;
    }

    public void setPassword(String password){

        this.password = password;
    }
}
