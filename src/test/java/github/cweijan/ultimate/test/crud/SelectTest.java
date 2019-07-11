package github.cweijan.ultimate.test.crud;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.core.component.TableInfo;
import github.cweijan.ultimate.core.component.info.ComponentInfo;
import github.cweijan.ultimate.test.base.BaseTest;
import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.test.bean.Lib;
import github.cweijan.ultimate.util.Json;
import github.cweijan.ultimate.util.Log;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SelectTest extends BaseTest{

    @Test
    public void testGetByEquals(){

        Query<Admin> query = Query.of(Admin.class);
        query.eq("test", "test2");
        //        query.orEquals("id", "2");
        //        query.setColumn("id, message");
        query.pageSize(1);
        Admin admin = query.get();
        Log.info(admin);
        Log.info(2);
        Log.info("sdfsdf");
        Log.info(true);
        Log.info('s');
        Log.info(Arrays.asList("sdf"));

    }

    @Test
    public void isNullTest(){

        List<Lib> msd = Query.of(Lib.class).isNull("msd").list();
        System.out.println(msd);


    }

    @Test
    public void isNotNullTest(){

        List<Lib> msd = Query.of(Lib.class).isNotNull("message").list();
        System.out.println(msd);


    }


    @Test
    public void testGetMap(){

        String sql = "select * from rh_admin";
        Map<String, Object> result =Query.db.executeSqlOfMap(sql);

        Log.info(result + "");

    }

    @Test
    public void testInSelect(){

        ArrayList<Integer> idList = new ArrayList<>();
        idList.add(2);
        idList.add(3);
        List<Admin> admins = Query.of(Admin.class).in0("id", idList).list();
        Log.info(admins);


    }

    @Test
    public void testInputExcel(){

        List<Admin> admins = Query.of(Admin.class).inputExcel("D://test.xls");
        Log.info(admins);

    }

    @Test
    public void testExportExcel(){

        Query.of(Admin.class).ouputExcel("D://test.xls");

    }

    @Test
    public void testGetBy(){

        Admin admin = Query.db.getByPrimaryKey(Admin.class, "1");
        Log.info(admin);

    }

    @Test
    public void testGet(){

        Admin admin = Query.of(Admin.class).eq("id", 1).get();
        Log.info(Json.toJson(admin));

    }

    @Test
    public void testChildenClass(){

        //        AdminC admin = Query.of(AdminC.class).eq("id", 1).get();
        //        Log.info(admin);

    }

    @Test
    public void testQuery(){

        Admin admin = new Admin();
        //        admin.setId(1);
        admin.setMessage("滚");
        List<Lib> list = Query.of(Lib.class).read(admin).list();
        Log.info(list.size());
    }
    @Test
    public void testSearchLib(){

        List<Lib> list = Query.of(Lib.class).search("msd","滚").list();
        Log.info(list.size());
    }

    @Test
    public void testJoin(){

        List<Admin> adminList = Query.of(Admin.class).join(Lib.class).eq("ad.id", "1").list();

        Log.info(adminList);

    }

    @Test
    public void testFind(){

        List<Admin> admins = Query.of(Admin.class).eq("id", "2").orEq("id", "3").orSearch("id", 4).list();
        System.out.println(admins.size());
    }

    @Test
    public void testFindByQuery(){

        List<Admin> adminList = Query.of(Admin.class).list();
        System.out.println(adminList.size());
        System.out.println(adminList);
    }

    @Test
    public void testFindByComponent(){

        Admin admin = new Admin();
        admin.setId(2);
        List<Admin> admins = Query.of(Admin.class).read(admin).list();
        System.out.println(admins);

    }

    @Test
    public void testGetByComponent(){

        Admin admin = new Admin();
        admin.setId(2);
        admin = Query.of(Admin.class).read(admin).get();
        Log.info(admin);

    }

    @Test
    public void findByComponentClass(){

        List<Admin> admins = Query.of(Admin.class).offset(0).pageSize(20).list();
        Log.info(admins.toString());

    }

    @Test
    public void testFindAll(){

        Query<Admin> query = Query.of(Admin.class);
        List<Admin> admins = query.list();
        ComponentInfo component = TableInfo.INSTANCE.getComponent(Admin.class);
        Log.info(admins.toString());
    }

    @Test
    public void testFindByEquals(){

        List<Admin> admins = Query.of(Admin.class).eq("id", "8").list();

        Log.info(admins.toString());

    }

    @Test
    public void testSearch(){

        Query<Admin> query = Query.of(Admin.class);
        query.search("test", "2");
        List<Admin> admins = query.list();
        Log.info(admins.toString());
        Log.info("hello");

    }

    @Test
    public void testGeAndLe(){

        List<Admin> list = Query.of(Admin.class).ge("id", 2).le("id", 4).list();
        Log.info(list);
    }

}
