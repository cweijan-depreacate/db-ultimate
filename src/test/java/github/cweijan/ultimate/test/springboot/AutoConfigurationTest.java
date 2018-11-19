package github.cweijan.ultimate.test.springboot;

import github.cweijan.ultimate.annotation.TableComponentScan;
import github.cweijan.ultimate.springboot.UltimateAutoConfiguration;
import github.cweijan.ultimate.util.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {UltimateAutoConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SpringBootApplication(scanBasePackages = {"github.cweijan"})
@TableComponentScan("github.cweijan")
public class AutoConfigurationTest{

    @Test
    public void testInit(){
        Log.getLogger().info("init");
    }

}
