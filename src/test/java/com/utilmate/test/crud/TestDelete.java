package com.utilmate.test.crud;

import com.ultimate.bean.Admin;
import com.utilmate.test.base.BaseTest;
import org.junit.Test;

public class TestDelete extends BaseTest{

    @Test
    public void deleteByEquals(){
        condition.equals("id","1");
        dbUltimate.delete(condition, Admin.class);
    }

}
