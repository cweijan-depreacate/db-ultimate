package github.cweijan.ultimate.core.extra;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.json.Json;
import kotlin.text.Charsets;

public final class ExtraDataService {

    private ExtraDataService() {
    }

    public static void save(Object key, Object extraObject) {
        Query query = Query.of(ExtraData.class).eq("extraKey", key).eq("typeName", extraObject.getClass().getName());
        ExtraData extraData = (ExtraData) query.get();
        if (extraData == null) {
            String sql = "INSERT INTO ultimate_extra_data (extra_key, DATA,type_name) VALUES ('" + key + "', ?, '" + extraObject.getClass().getName() + "');";
            Query.core.executeSql(sql, new Object[]{Json.toJson(extraObject).getBytes()});
        } else {
            query.update("data", Json.toJson(extraObject).getBytes()).executeUpdate();
        }

    }

    public static <T> T getExtraData(Object key, Class<T> extraType) {

        ExtraData extraData = Query.of(ExtraData.class).eq("extraKey", key).eq("typeName", extraType.getName()).get();
        if (extraData != null) {
            return Json.toObject(new String(extraData.getData(), Charsets.UTF_8), extraType);
        } else {
            return null;
        }
    }

}
