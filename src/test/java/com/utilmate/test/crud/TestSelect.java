package com.utilmate.test.crud;

import com.ultimate.bean.Admin;
import com.ultimate.component.TableInfo;
import com.ultimate.component.info.ComponentInfo;
import com.ultimate.core.Operation;
import com.ultimate.util.Log;
import com.utilmate.test.base.BaseTest;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class TestSelect extends BaseTest{

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
