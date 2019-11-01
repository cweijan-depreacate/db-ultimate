package github.cweijan.ultimate.test.postgres.base;

import github.cweijan.ultimate.core.DbUltimate;
import github.cweijan.ultimate.core.component.info.ComponentInfo;
import github.cweijan.ultimate.core.lucene.LuceneQuery;
import github.cweijan.ultimate.db.config.DbConfig;
import github.cweijan.ultimate.db.init.DBInitialer;
import github.cweijan.ultimate.test.bean.CreateTest;
import github.cweijan.ultimate.util.Log;
import org.junit.BeforeClass;
import org.junit.Test;

public class PostgreBaseTest {

    protected static DbConfig dbConfig;
    protected static DBInitialer dbInitialer;

    @BeforeClass
    public static void initConfig() {

        dbConfig = new DbConfig();
        dbConfig.setUrl("jdbc:postgresql://localhost/test");
        dbConfig.setUsername("postgres");
        dbConfig.setPassword("123456");
        dbConfig.setDriver("org.postgresql.Driver");
        DbUltimate ultimate =DbUltimate.init(dbConfig);
        dbInitialer = new DBInitialer(dbConfig, ultimate.getDataSource());
    }

    @Test
    public void testInit() {
        ComponentInfo componentInfo = ComponentInfo.init(CreateTest.class, false);
        Log.info(componentInfo.tableName);
        Log.info("init successful");
    }

}
