package github.cweijan.ultimate.test.base;

import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.test.bean.Lib;
import github.cweijan.ultimate.component.info.ComponentInfo;
import github.cweijan.ultimate.component.TableInfo;
import org.junit.Test;

public class ComponentTest extends BaseTest{


    @Test
    public void scan(){


        System.out.println(TableInfo.INSTANCE.getComponent(Admin.class));

    }

}
