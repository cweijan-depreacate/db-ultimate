package com.utilmate.test.crud;

import com.ultimate.bean.Admin;
import com.ultimate.component.info.ComponentInfo;
import com.ultimate.component.TableInfo;
import com.ultimate.core.Condition;
import com.ultimate.util.Log;
import com.utilmate.test.base.BaseTest;
import org.junit.Test;

import java.util.List;

public class TestSelect extends BaseTest{

    @Test
    public void testGet(){

        Admin admin = dbUltimate.get(new Condition(), Admin.class);
        Log.getLogger().info(admin.toString());

    }

    @Test
    public void testSelectAll(){

        List<Admin> admins = dbUltimate.find(new Condition(), Admin.class);
        ComponentInfo component = TableInfo.getComponent(Admin.class);
        Log.getLogger().info(admins.toString());
    }

    @Test
    public void testSelectByEquals(){

        condition.equals("test", "test2");
        condition.orEquals("test", "123");
        condition.setColumn("id, message");
        List<Admin> admins = dbUltimate.find(condition, Admin.class);
        logger.info(admins.toString());

    }

    @Test
    public void testSearch(){

        condition.search("test", "2");
        List<Admin> admins = dbUltimate.find(condition, Admin.class);
        logger.info(admins.toString());

    }

}
