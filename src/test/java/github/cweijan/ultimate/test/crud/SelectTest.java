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
    public void testGetByEquals(){
        Operation<Admin> operation = Operation.build(Admin.class);
        operation.equals("test", "test2");
        operation.orEquals("id", "2");
        //        operation.setColumn("id, message");
        Admin admin = dbUltimate.get(operation);
        logger.info(admin.toString());
    }

    @Test
    public void testGetMap(){

        String sql = "select * from rh_admin";
        Map<String, Object> result = dbUltimate.executeSqlOfMap(sql);

        Log.getLogger().info(result + "");

    }

    @Test
    public void testGetBy(){

        Admin admin = dbUltimate.getBy(Admin.class, "id", "2");
        Log.getLogger().info(admin.toString());

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
    public void testJoin(){

        Operation<Admin> operation = Operation.build(Admin.class);
        operation.join(Lib.class, "ad.id=l.id");

        Admin admin = dbUltimate.get(operation);
        Log.getLogger().info(admin + "");

    }

    @Test
    public void testFind(){

        List<Admin> admins = dbUltimate.findBy(Admin.class, "id", "2");
        System.out.println(admins);
    }

    @Test
    public void testFindByComponent(){

        Admin admin = new Admin();
        admin.setId(2);
        List<Admin> admins = dbUltimate.find(admin);
        System.out.println(admins);

    }

    @Test
    public void testGetByComponent(){

        Admin admin = new Admin();
        admin.setId(2);
        admin = dbUltimate.get(admin);
        Log.getLogger().info(admin.toString());

    }

    @Test
    public void findByComponentClass(){

        List<Admin> admins = dbUltimate.find(Admin.class, 0, 20);
        Log.getLogger().info(admins.toString());

    }

    @Test
    public void testFindAll(){

        Operation<Admin> operation = Operation.build(Admin.class);
        List<Admin> admins = dbUltimate.find(operation);
        ComponentInfo component = TableInfo.INSTANCE.getComponent(Admin.class);
        Log.getLogger().info(admins.toString());
    }

    @Test
    public void testFindByEquals(){

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
