package github.cweijan.ultimate.test.crud;

import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.component.TableInfo;
import github.cweijan.ultimate.component.info.ComponentInfo;
import github.cweijan.ultimate.core.Operation;
import github.cweijan.ultimate.test.base.BaseTest;
import github.cweijan.ultimate.util.Log;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class SelectTest extends BaseTest{

    @Test
    public void testGet(){

        Admin admin = dbUltimate.get(new Operation(), Admin.class);
        Date date = admin.getDate();
        System.out.println(date);
        Log.getLogger().info(admin.toString());

    }

    @Test
    public void testSelectAll(){

        List<Admin> admins = dbUltimate.find(new Operation(), Admin.class);
        ComponentInfo component = TableInfo.getComponent(Admin.class);
        Log.getLogger().info(admins.toString());
    }

    @Test
    public void testSelectByEquals(){

        operation.equals("test", "test2");
        operation.orEquals("test", "123");
        operation.setColumn("id, message");
        List<Admin> admins = dbUltimate.find(operation, Admin.class);
        logger.info(admins.toString());

    }

    @Test
    public void testSearch(){

        operation.search("test", "2");
        List<Admin> admins = dbUltimate.find(operation, Admin.class);
        logger.info(admins.toString());

    }

}
