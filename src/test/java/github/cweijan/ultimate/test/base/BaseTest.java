package github.cweijan.ultimate.test.base;

import github.cweijan.ultimate.core.DbUltimate;
import github.cweijan.ultimate.core.component.info.ComponentInfo;
import github.cweijan.ultimate.core.lucene.LuceneQuery;
import github.cweijan.ultimate.core.tx.TransactionHelper;
import github.cweijan.ultimate.db.config.DbConfig;
import github.cweijan.ultimate.db.init.DBInitialer;
import github.cweijan.ultimate.test.bean.CreateTest;
import github.cweijan.ultimate.util.Log;
import org.junit.BeforeClass;
import org.junit.Test;

public class BaseTest {

    protected static DbConfig dbConfig;
    protected static DBInitialer dbInitialer;
    protected static TransactionHelper transactionHelper;

    @BeforeClass
    public static void initConfig() {

        dbConfig = new DbConfig();
        dbConfig.setUrl("jdbc:mysql://localhost:3306/ultimate");
        dbConfig.setUsername("root");
        dbConfig.setPassword("123456");
        dbConfig.setDriver("com.mysql.jdbc.Driver");
        dbConfig.setScanPackage("github.cweijan.ultimate");
        DbUltimate ultimate =DbUltimate.init(dbConfig);
        transactionHelper = ultimate.getTransactionHelper();
        LuceneQuery.init("D:\\temp-index");
        dbInitialer = new DBInitialer(dbConfig, transactionHelper);
    }

    @Test
    public void testInit() {
        ComponentInfo componentInfo = ComponentInfo.init(CreateTest.class, false);
        Log.info(componentInfo.tableName);
        Log.info("init successful");
    }

}
