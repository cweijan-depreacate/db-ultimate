package github.cweijan.ultimate.test.crud;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.test.base.BaseTest;
import github.cweijan.ultimate.test.bean.Lib;
import github.cweijan.ultimate.test.code.AdminTypeEnum;
import kotlin.ranges.IntRange;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.stream.IntStream;

public class InsertTest extends BaseTest{

    @Test
    public void testInsertLib(){

        Lib lib = new Lib();
        //        lib.setId(12);
        lib.setMessage("cweijan");
        Query.db.insert(lib);
        System.out.println(lib.getId());
    }

    @Test
    public void testInsertAdmin(){
        Admin admin = new Admin();
        admin.setDate(LocalDateTime.now());
        admin.setAdminType(AdminTypeEnum.admin);
        admin.setDelete(true);
        Query.db.insert(admin);
    }

    @Test
    public void testInsert(){

        IntStream.range(0, 9).forEach(i->{
            Admin admin = new Admin();
            admin.setMessage("hello");
            if(i < 3) admin.setTest("test1");
            else if(i < 6) admin.setTest("test2");
            else if(i < 9) admin.setTest("test");
                 admin.setAdminType(AdminTypeEnum.user);
            admin.setDate(LocalDateTime.now());
            Query.db.insert(admin);
        });

    }

}
