package github.cweijan.ultimate.test.crud;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.test.base.BaseTest;
import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.test.bean.CreateTest;
import github.cweijan.ultimate.test.bean.Lib;
import github.cweijan.ultimate.test.code.AdminTypeEnum;
import github.cweijan.ultimate.util.Json;
import github.cweijan.ultimate.util.Log;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

public class InsertTest extends BaseTest{

    @Test
    public void testInsertExtra(){

        List<String> test = Arrays.asList("test", "sdfxc;l");
        CreateTest createTest = new CreateTest();
        createTest.setAge(12);
        System.out.println();
        createTest.setMagenetList(test);
//        Query.db.insert(createTest);

        CreateTest id = Query.of(CreateTest.class).eq("id", "4").get();
        Log.info(Json.toJson(id));

    }

    @Test
    public void testInsertLib(){

        Lib lib = new Lib();

//        lib.setList(Collections.singletonList("hello"));
        //        lib.setId(12);
        lib.setMessage("cweijan");
        lib.setCreateDate(new Date());
        lib.setCreateTime(new Date());
        lib.setCreateTime2(LocalDateTime.now());
        Query.db.insert(lib);
        System.out.println(lib.getId());
    }

    @Test
    public void testInsertAdmin(){
        Admin admin = new Admin();
        admin.setDate(LocalDateTime.now());
        admin.setAdminType(AdminTypeEnum.admin);
        admin.setDelete(true);
        admin.setMessage("hello");
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
