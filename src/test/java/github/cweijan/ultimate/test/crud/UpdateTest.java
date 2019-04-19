package github.cweijan.ultimate.test.crud;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.test.base.BaseTest;
import org.junit.Test;

public class UpdateTest extends BaseTest{

    @Test
    public void testUpdateByOperation(){
        Query<Admin> query = Query.of(Admin.class);
        query.update("test","test2");
        dbUltimate.update(query);
    }

    @Test
    public void testUpdate(){

        Admin admin = new Admin();
        admin.setId(2);
//        admin.setMessage("cweijain");
        dbUltimate.update(admin);

    }

}
