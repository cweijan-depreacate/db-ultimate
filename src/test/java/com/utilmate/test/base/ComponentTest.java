package com.utilmate.test.base;

import com.ultimate.bean.Admin;
import com.ultimate.bean.Lib;
import com.ultimate.component.info.ComponentInfo;
import com.ultimate.component.TableInfo;
import org.junit.Test;

public class ComponentTest extends BaseTest{

    @Test
    public void testGetAllColumns(){

        ComponentInfo component = TableInfo.getComponent(Admin.class);
        ComponentInfo lib = TableInfo.getComponent(Lib.class);
        String allColumns = component.getAllColumns();
        System.out.println(allColumns);

    }

    @Test
    public void scan(){


        System.out.println(TableInfo.getComponent(Admin.class));

    }

}
