package github.cweijan.ultimate.test.crud;

import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.test.base.BaseTest;
import github.cweijan.ultimate.test.bean.Lib;
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
        dbUltimate.insert(lib);
    }

    @Test
    public void testInsert(){

        IntStream.range(0, 9).forEach(i->{
            Admin admin = new Admin();
            admin.setMessage("hello");
            if(i < 3) admin.setTest("test1");
            else if(i < 6) admin.setTest("test2");
            else if(i < 9) admin.setTest("test");
            admin.setDate(LocalDateTime.now());
            dbUltimate.insert(admin);
        });

    }

}
