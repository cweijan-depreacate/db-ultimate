package github.cweijan.ultimate.test.postgres;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.test.postgres.base.PostgreBaseTest;
import org.junit.Test;

import java.util.Date;

/**
 * @author cweijan
 * @version 2019/11/1 17:47
 */
public class InsertTest extends PostgreBaseTest {
    @Test
    public void testInsertTest(){
        github.cweijan.ultimate.test.postgres.bean.Test test = new github.cweijan.ultimate.test.postgres.bean.Test();
        test.setTest2(new Date());
        Query.db.insert(test);

    }
}
