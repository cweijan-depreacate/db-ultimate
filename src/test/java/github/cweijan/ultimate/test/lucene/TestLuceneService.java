package github.cweijan.ultimate.test.lucene;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.core.lucene.LuceneQuery;
import github.cweijan.ultimate.core.page.Pagination;
import github.cweijan.ultimate.test.base.BaseTest;
import github.cweijan.ultimate.test.bean.Lib;
import github.cweijan.ultimate.util.Log;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author cweijan
 * @version 2019/7/18/018 20:12
 */
public class TestLuceneService extends BaseTest {

    @Test
    public void testAdd() {

        testDeleteAll();
        LuceneQuery.index(Query.of(Lib.class).eq("id", 1).get());
        LuceneQuery.index(Query.of(Lib.class).eq("id", 4).get());
        LuceneQuery.index(Query.of(Lib.class).eq("id", 3).get());
    }

    @Test
    public void testSearch() {
        Pagination<Lib> libPagination = LuceneQuery.of(Lib.class).all().page(0).pageSize(50).orderDescBy("id").pageList();
        Log.info(libPagination);
    }

    @Test
    public void testDate() {

        long l = LocalDateTime.now().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
        System.out.println(l);
        l = new Date().getTime();
        System.out.println(l);
        System.out.println(Instant.ofEpochMilli(l).atZone(ZoneId.systemDefault()).toLocalDateTime());

        System.out.println(new Date(l));


    }

    @Test
    public void testDeleteAll() {
        LuceneQuery.indexService.deleteAllIndex();
    }

    @Test
    public void testGetById() {
        Lib byPrimaryKey = LuceneQuery.of(Lib.class).getByPrimaryKey(1);
        System.out.println(byPrimaryKey);
    }

}
