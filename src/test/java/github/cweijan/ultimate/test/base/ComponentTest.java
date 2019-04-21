package github.cweijan.ultimate.test.base;

import github.cweijan.ultimate.component.TableInfo;
import github.cweijan.ultimate.component.info.ComponentInfo;
import github.cweijan.ultimate.test.bean.Admin;
import org.junit.Test;

public class ComponentTest extends BaseTest{


    @Test
    public void testBuildModel(){
    }

    @Test
    public void testJava(){
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        String s = "helloWorldTest".replaceAll(regex, replacement).toLowerCase();
        System.out.println(s);

    }

    @Test
    public void scan(){

        ComponentInfo component = TableInfo.INSTANCE.getComponent(Admin.class);
        System.out.println(component);

    }

}
