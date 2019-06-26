package github.cweijan.ultimate.test.util;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.util.Json;
import github.cweijan.ultimate.test.base.BaseTest;
import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.util.Log;
import org.junit.Test;

public class JsonTest extends BaseTest{

    @Test
    public void testJson(){

        Log.info(Query.of(Admin.class).listJson());

    }

    @Test
    public void testGet(){

        Admin admin = new Admin();
        admin.setId(23);
        Log.info(Json.toJson(admin));
        Log.info(Json.toJsonWithEmpty(admin));

    }

    @Test
    public void testReadJson(){

        String json = "{\"message\":\"he'llo\",\"ttt\":2}";
        Admin admin = Query.of(Admin.class).getFromJson(json);
        Log.info(admin);

    }

    @Test
    public void testReadJsonError(){

        String json = "{\\";
        Admin admin = Query.of(Admin.class).getFromJson(json);
        Log.info(admin);

    }


}
