package com.ultimate.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class HikariDataSourceAdapter{

    private Thread shutdownHook;
    private String url;
    private String username;
    private String password;
    private String driver;

    public HikariDataSourceAdapter(){

    }

    public HikariDataSourceAdapter(String url, String username, String password, String driver){

        this.url = url;
        this.username = username;
        this.password = password;
        this.driver = driver;
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

    private HikariConfig buildConfig(){

        // TODO 需要增加动态参数
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driver);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return config;
    }

}
