package github.cweijan.ultimate.test.crud;

import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.test.base.BaseTest;
import org.junit.Test;

public class UpdateTest extends BaseTest{

    @Test
    public void testUpdateByOperation(){
        operation.update("test","test2");
        dbUltimate.update(operation, Admin.class);
    }

    @Test
    public void testUpdate(){

        Admin admin = new Admin();
        admin.setId(2);
        admin.setMessage("cweijain");
        dbUltimate.update(admin);

    }

}
