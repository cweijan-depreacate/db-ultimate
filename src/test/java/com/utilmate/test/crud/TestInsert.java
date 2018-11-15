package com.utilmate.test.crud;

import com.ultimate.bean.Admin;
import com.utilmate.test.base.BaseTest;
import org.junit.Test;

public class TestInsert extends BaseTest{

    @Test
    public void insert(){

        Admin admin = new Admin();
        admin.setMessage("hello");
        admin.setTest("test");
        dbUltimate.insert(admin);

    }

}
