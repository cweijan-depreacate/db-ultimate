package github.cweijan.ultimate.test.springboot;

import github.cweijan.ultimate.annotation.TableComponentScan;
import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.json.Json;
import github.cweijan.ultimate.springboot.UltimateAutoConfiguration;
import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.test.bean.Lib;
import github.cweijan.ultimate.test.springboot.service.AdminService;
import github.cweijan.ultimate.test.springboot.tc.TransactionalService;
import github.cweijan.ultimate.util.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {UltimateAutoConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SpringBootApplication(scanBasePackages = {"github.cweijan"})
@TableComponentScan("github.cweijan")
public class TestSpringBoot {

    @Autowired
    private TransactionalService testService;

    @Autowired
    private AdminService adminService;

    /**
     * 测试事务是否生效
     */
    @Test
    public void testTransaction(){
        try {
            System.out.println(Query.of(Lib.class).eq("id", 13).get().getTest());
            testService.test();
        } finally {
            System.out.println(Query.of(Lib.class).eq("id", 13).get().getTest());
        }

    }
    @Test
    public void testServiceInject(){
        List<Admin> list = adminService.findAllLimit(0, 10);
        Log.info(Json.toJson(list));
    }


    @Test
    @Transactional
    public void testMutliConnection(){
        for (int i = 0; i < 100; i++) {
            System.out.println(i);
            Query.of(Admin.class).list();
        }
    }

    @Test
    public void testInit(){
        Log.info("init");
    }

}
