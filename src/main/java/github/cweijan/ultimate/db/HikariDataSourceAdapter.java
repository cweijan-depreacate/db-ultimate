package github.cweijan.ultimate.db;

import github.cweijan.ultimate.db.config.DbConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class HikariDataSourceAdapter{

    private Thread shutdownHook;
    private DbConfig dbConfig;

    public HikariDataSourceAdapter(DbConfig dbConfig){

        this.dbConfig = dbConfig;
    }

    private HikariConfig buildConfig(){

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbConfig.getUrl());
        config.setUsername(dbConfig.getUsername());
        config.setPassword(dbConfig.getPassword());
        config.setDriverClassName(dbConfig.getDriver());
        config.setMinimumIdle(dbConfig.getMinimumIdle());
        config.setMaximumPoolSize(dbConfig.getMaximumPoolSize());

        return config;
    }

    public DataSource refreshDataSource(){

        if(shutdownHook != null){
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        }
        return getDataSource();
    }

    public DataSource getDataSource(){

        HikariDataSource dataSource = new HikariDataSource(buildConfig());
        shutdownHook = new Thread(dataSource::close);
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        return dataSource;
    }

}
