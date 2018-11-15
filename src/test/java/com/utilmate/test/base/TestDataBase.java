package com.utilmate.test.base;

import com.ultimate.bean.Admin;
import com.ultimate.component.TableInfo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;

import java.sql.SQLException;

public class TestDataBase extends BaseTest{

    @Test
    public void testCreateTable(){
        dbInitialer.createTable(TableInfo.getComponent(Admin.class));
    }

    @Test
    public void testCheckTableExists(){

        boolean exists = dbInitialer.tableExists(TableInfo.getComponent(Admin.class).getTableName());
        logger.info(String.valueOf(exists));

    }


    @Test
    public void testCreateDatasource() throws SQLException{

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/listen");
        config.setUsername("root");
        config.setPassword("665420");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource dataSource = new HikariDataSource(config);
        System.out.println(dataSource.getConnection());
        dataSource.close();

    }

}
