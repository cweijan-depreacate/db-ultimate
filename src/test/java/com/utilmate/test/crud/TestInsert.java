package com.utilmate.test.crud;

import com.ultimate.bean.Admin;
import com.utilmate.test.base.BaseTest;
import org.junit.Test;

import java.util.Date;

public class TestInsert extends BaseTest{

    @Test
    public void testInsert(){

        Admin admin = new Admin();
        admin.setMessage("hello");
        admin.setTest("test");
        dbUltimate.insert(admin);

    }

    @Test
    public void testInsertSelective(){

        Admin admin = new Admin();
        admin.setMessage("hello");
        admin.setTest("test");
        admin.setDate(new Date());
        dbUltimate.insertSelective(admin);

    }

}
