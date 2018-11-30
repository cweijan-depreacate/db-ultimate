package github.cweijan.ultimate.test.base;

import github.cweijan.ultimate.component.TableInfo;
import github.cweijan.ultimate.test.bean.Admin;
import org.junit.Test;

public class ComponentTest extends BaseTest{


    @Test
    public void testBuildModel(){

    }

    @Test
    public void scan(){


        System.out.println(TableInfo.INSTANCE.getComponent(Admin.class));

    }

}
