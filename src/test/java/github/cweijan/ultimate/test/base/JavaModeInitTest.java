package github.cweijan.ultimate.test.base;

import github.cweijan.ultimate.core.DbUltimate;
import github.cweijan.ultimate.db.config.DbConfig;
import github.cweijan.ultimate.test.bean.Admin;
import org.junit.Test;

import java.util.List;

public class JavaModeInitTest{

    @Test
    public void init(){

        DbConfig dbConfig = new DbConfig();
        dbConfig.setUrl("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8");
        dbConfig.setDriver("com.mysql.jdbc.Driver");
        dbConfig.setUsername("root");
        dbConfig.setPassword("665420");
        dbConfig.setScanPackage("github.cweijan");
        dbConfig.setCreateNonexistsTable(true);
        DbUltimate dbUltimate = new DbUltimate(dbConfig);
        List<Admin> admins = dbUltimate.find(Admin.class);
        System.out.println(admins);

    }




}
