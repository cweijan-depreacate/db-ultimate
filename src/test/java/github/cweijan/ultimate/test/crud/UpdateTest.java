package github.cweijan.ultimate.test.crud;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.test.base.BaseTest;
import github.cweijan.ultimate.test.bean.Lib;
import org.junit.Test;

import java.time.LocalDateTime;

public class UpdateTest extends BaseTest{

    @Test
    public void testUpdateByOperation(){

        Query<Admin> query = Query.of(Admin.class);
        query.update("test", "test2");
        dbUltimate.update(query);
    }

    @Test
    public void testUpdateByQuery(){

        Query.of(Lib.class).update("test", "lib").equals("id", 2).executeUpdate();

    }

    @Test
    public void testUpdate(){

        Admin admin = new Admin();
        admin.setId(2);
        admin.setMsd("cweijain");
        admin.setDate(LocalDateTime.now());
        dbUltimate.update(admin);

    }

}
