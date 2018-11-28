package github.cweijan.ultimate.test.crud;

import github.cweijan.ultimate.core.Operation;
import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.test.base.BaseTest;
import org.junit.Test;

public class DeleteTest extends BaseTest{

    @Test
    public void deleteByEquals(){

        Operation<Admin> operation = Operation.build(Admin.class);
        operation.equals("id", "1");
        dbUltimate.delete(operation);

    }

}
