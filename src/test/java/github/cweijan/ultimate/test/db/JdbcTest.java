package github.cweijan.ultimate.test.db;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.test.base.BaseTest;
import github.cweijan.ultimate.test.bean.Admin;
import org.junit.Test;

public class JdbcTest extends BaseTest{

    @Test
    public void testMultiConnection(){

        for (int i = 0; i < 100; i++) {
            System.out.println(i);
            Query.of(Admin.class).list();
        }

    }

}
