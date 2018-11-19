package com.utilmate.test.crud;

import com.ultimate.bean.Admin;
import com.utilmate.test.base.BaseTest;
import org.junit.Test;

public class TestUpdate extends BaseTest{

    @Test
    public void testUpdate(){
        operation.update("test","test2");
        dbUltimate.update(operation, Admin.class);
    }

}
