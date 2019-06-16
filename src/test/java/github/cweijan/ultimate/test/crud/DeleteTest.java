package github.cweijan.ultimate.test.crud;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.test.base.BaseTest;
import org.junit.Test;

public class DeleteTest extends BaseTest{

    @Test
    public void deleteByEquals(){

        Query<Admin> query = Query.of(Admin.class);
        query.eq("id", "7");
        query.executeDelete();

    }

}
