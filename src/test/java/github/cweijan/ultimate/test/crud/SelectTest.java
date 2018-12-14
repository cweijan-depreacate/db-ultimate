package github.cweijan.ultimate.test.crud;

import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.component.TableInfo;
import github.cweijan.ultimate.component.info.ComponentInfo;
import github.cweijan.ultimate.core.Operation;
import github.cweijan.ultimate.test.base.BaseTest;
import github.cweijan.ultimate.test.bean.Lib;
import github.cweijan.ultimate.util.Log;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SelectTest extends BaseTest{

    @Test
    public void testGetMap(){

        String sql = "select * from rh_admin";
        Map<String, Object> result = dbUltimate.executeSqlOfMap(sql);

        Log.getLogger().info(result.toString());

    }

    @Test
    public void testGet(){

        Admin admin = dbUltimate.get(Operation.build(Admin.class));
        assert admin != null;
        Date date = admin.getDate();
        System.out.println(date);
        Log.getLogger().info(admin.toString());

    }

    @Test
    public void testSelectAll(){

        Operation<Admin> operation = Operation.build(Admin.class);
        operation.limit(1);
        List<Admin> admins = dbUltimate.find(operation);
        ComponentInfo component = TableInfo.INSTANCE.getComponent(Admin.class);
        Log.getLogger().info(admins.toString());
    }

    @Test
    public void testSelectByEquals(){

        Operation<Admin> operation = Operation.build(Admin.class);
        operation.equals("test", "test2");
        operation.orEquals("test", "123");
        //        operation.setColumn("id, message");
        List<Admin> admins = dbUltimate.find(operation);
        logger.info(admins.toString());

    }

    @Test
    public void testSearch(){

        Operation<Admin> operation = Operation.build(Admin.class);
        operation.search("test", "2");
        List<Admin> admins = dbUltimate.find(operation);
        logger.info(admins.toString());

    }

}
