package github.cweijan.ultimate.test.crud;

import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.component.TableInfo;
import github.cweijan.ultimate.component.info.ComponentInfo;
import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.test.base.BaseTest;
import github.cweijan.ultimate.test.bean.Lib;
import github.cweijan.ultimate.util.Log;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SelectTest extends BaseTest{

    @Test
    public void testGetByEquals(){

        Query<Admin> query = Query.of(Admin.class);
        query.equals("test", "test2");
        query.orEquals("id", "2");
        //        query.setColumn("id, message");
        Admin admin = dbUltimate.getByQuery(query);
        Log.info(admin.toString());
    }

    @Test
    public void testGetMap(){

        String sql = "select * from rh_admin";
        Map<String, Object> result = dbUltimate.executeSqlOfMap(sql);

        Log.info(result + "");

    }

    @Test
    public void testGetBy(){

        Admin admin = dbUltimate.getByPrimaryKey(Admin.class,  "2");

        Log.info(admin);

    }

    @Test
    public void testGet(){

        Admin admin = dbUltimate.getByQuery(Query.of(Admin.class));
        assert admin != null;
//        Date date = admin.getDate();
//        System.out.println(date);
        Log.info(admin.toString());

    }

    @Test
    public void testQuery(){

        Admin admin = new Admin();
        admin.setDate(LocalDateTime.now());
        Query<Lib> libQuery = Query.of(Lib.class).readObject(admin);
    }

    @Test
    public void testJoin(){

        Query<Admin> query = Query.of(Admin.class);
        query.join(Lib.class);

        Admin admin = dbUltimate.getByQuery(query);
        Log.info(admin + "");

    }

    @Test
    public void testFind(){


        List<Admin> admins = dbUltimate.find(Query.of(Admin.class).equals("id","2"));
        System.out.println(admins);
    }

    @Test
    public void testFindByComponent(){

        Admin admin = new Admin();
        admin.setId(2);
        List<Admin> admins = dbUltimate.find(Query.of(Admin.class).readObject(admin));
        System.out.println(admins);

    }

    @Test
    public void testGetByComponent(){

        Admin admin = new Admin();
        admin.setId(2);
        admin = dbUltimate.getByQuery(Query.of(Admin.class).readObject(admin));
        Log.info(admin);

    }

    @Test
    public void findByComponentClass(){

        List<Admin> admins = dbUltimate.find(Query.of(Admin.class).offset(0).pageSize(20));
        Log.info(admins.toString());

    }

    @Test
    public void testFindAll(){

        Query<Admin> query = Query.of(Admin.class);
        List<Admin> admins = dbUltimate.find(query);
        ComponentInfo component = TableInfo.INSTANCE.getComponent(Admin.class);
        Log.info(admins.toString());
    }

    @Test
    public void testFindByEquals(){

        Query<Admin> query = Query.of(Admin.class);
        query.equals("test", "test2").orEquals("test", "123");
        //        query.setColumn("id, message");
        List<Admin> admins = dbUltimate.find(query);

        Log.info(admins.toString());

    }

    @Test
    public void testSearch(){

        Query<Admin> query = Query.of(Admin.class);
        query.search("test", "2");
        List<Admin> admins = dbUltimate.find(query);
        Log.info(admins.toString());
        Log.info("hello");

    }

}
