package github.cweijan.ultimate.test.base;

import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.component.TableInfo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import github.cweijan.ultimate.util.Log;
import org.junit.Test;

import java.sql.SQLException;

public class DataBaseTest extends BaseTest{

    @Test
    public void testCreateTable(){
        dbInitialer.createTable(TableInfo.INSTANCE.getComponent(Admin.class));
    }

    @Test
    public void testCheckTableExists(){

        boolean exists = dbInitialer.tableExists(TableInfo.INSTANCE.getComponent(Admin.class).getTableName());
        Log.info(String.valueOf(exists));

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
