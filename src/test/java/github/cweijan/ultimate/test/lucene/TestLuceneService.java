package github.cweijan.ultimate.test.lucene;

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
import java.util.List;

/**
 * @author cweijan
 * @version 2019/7/18/018 20:12
 */
public class TestLuceneService extends BaseTest {

    @Test
    public void testAdd() {
        LuceneQuery.indexService.deleteAllIndex(LuceneObject.class);
        LuceneObject luceneObject = new LuceneObject();
        luceneObject.setCreateTime(LocalDateTime.now());
        luceneObject.setUpdateTime(new Date());
        luceneObject.setIdNumber(2323799423L);
        luceneObject.setId(1);
        luceneObject.setData("测试".getBytes());
        luceneObject.setHello("cweijan-document-呵呵呵1");
        luceneObject.setText("cweijan-document-呵呵呵1");
        luceneObject.setVip(true);
        luceneObject.setPrice(1231.35);
        LuceneQuery.index(luceneObject);

    }

    @Test
    public void testSearch() {
        Pagination<LuceneObject> luceneObjectPagination = LuceneQuery.of(LuceneObject.class).all().page(0).pageSize(50).orderDescBy("id").pageList();
        List<LuceneObject> data = luceneObjectPagination.getList();
        Log.info(data);
        data.forEach(d->Log.info(new String(d.getData())));
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
        LuceneQuery.indexService.deleteAllIndex(LuceneObject.class);
    }

    @Test
    public void testGetById() {
        Lib byPrimaryKey = LuceneQuery.of(Lib.class).getByPrimaryKey(1);
        System.out.println(byPrimaryKey);
    }

}
