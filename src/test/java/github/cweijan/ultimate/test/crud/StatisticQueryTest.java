package github.cweijan.ultimate.test.crud;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.test.base.BaseTest;
import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.util.Log;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class StatisticQueryTest extends BaseTest{

    @Test
    public void testGenerateShowColumn(){

//        Query.of(Admin.class).min("id").max("id").generateColumns();
    }

    @Test
    public void testStatistic(){

        Query<Admin> query = Query.of(Admin.class).min("id").max("id").sum("id").avg("id").countDistinct("id").addShowColumn("test").groupBy("test").having("test!='test3'").ge("id", 10);
        List<Map<String, Object>> statisticList = query.statistic();
        statisticList.forEach(statistic->{
            statistic.forEach((key, value)->{
                Log.info(key + "=" + value);
            });
            System.out.println("-------------------------------------------------------");
        });

    }

}
