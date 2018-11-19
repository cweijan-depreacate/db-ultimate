package com.utilmate.test.base;

import com.ultimate.component.ComponentScan;
import com.ultimate.core.Operation;
import com.ultimate.core.DbUltimate;
import com.ultimate.db.DBInitialer;
import com.ultimate.db.config.DbConfig;
import com.ultimate.util.Log;
import org.junit.Before;
import org.slf4j.Logger;

public class BaseTest{

    protected DbConfig dbConfig;
    protected DbUltimate dbUltimate;
    protected DBInitialer dbInitialer;
    protected Operation operation ;
    protected static Logger logger = Log.getLogger();

    @Before
    public void initConfig(){

        dbConfig = new DbConfig();
        dbConfig.setUrl("jdbc:mysql://localhost:3306/ultimate");
        dbConfig.setUsername("root");
        dbConfig.setPassword("665420");
        dbConfig.setDriver("com.mysql.jdbc.Driver");
        dbConfig.setCreateNonexistsTable(true);
        dbUltimate = new DbUltimate(dbConfig);
        operation=new Operation();
    }
}
