package github.cweijan.ultimate.test.base;

import github.cweijan.ultimate.core.DbUltimate;
import github.cweijan.ultimate.db.init.DBInitialer;
import github.cweijan.ultimate.db.config.DbConfig;
import github.cweijan.ultimate.util.Log;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

public class BaseTest{

    protected DbConfig dbConfig;
    protected DbUltimate dbUltimate;
    protected DBInitialer dbInitialer;

    @Before
    public void initConfig(){

        dbConfig = new DbConfig();
        dbConfig.setUrl("jdbc:mysql://localhost:3306/ultimate");
        dbConfig.setUsername("root");
        dbConfig.setPassword("665420");
        dbConfig.setDriver("com.mysql.jdbc.Driver");
        dbConfig.setCreateNonexistsTable(true);
        dbConfig.setScanPackage("github.cweijan.ultimate");
        dbUltimate = new DbUltimate(dbConfig);
        dbInitialer=new DBInitialer(dbConfig);
    }

    @Test
    public void testInit(){
        Log.info("init successful");
    }

}
