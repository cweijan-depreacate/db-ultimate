package github.cweijan.ultimate.test.crud;

import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.test.base.BaseTest;
import github.cweijan.ultimate.test.bean.Lib;
import org.junit.Test;

import java.util.Date;

public class InsertTest extends BaseTest{

    @Test
    public void testInsertLib(){
        Lib lib = new Lib();
//        lib.setId(12);
        lib.setMessage("cweijan");
        dbUltimate.insert(lib);
    }

    @Test
    public void testInsert(){

        Admin admin = new Admin();
//        admin.setMessage("hello");
//        admin.setTest("test");
//        admin.setDate(new Date());
        dbUltimate.insert(admin);

    }

}
