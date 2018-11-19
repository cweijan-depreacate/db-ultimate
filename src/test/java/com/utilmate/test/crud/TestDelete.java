package com.utilmate.test.crud;

import com.ultimate.bean.Admin;
import com.utilmate.test.base.BaseTest;
import org.junit.Test;

public class TestDelete extends BaseTest{

    @Test
    public void deleteByEquals(){
        operation.equals("id","1");
        dbUltimate.delete(operation, Admin.class);
    }

}
