package github.cweijan.ultimate.core.extra;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.util.Json;
import github.cweijan.ultimate.util.StringUtils;
import kotlin.text.Charsets;

import java.time.LocalDateTime;

public final class ExtraDataService {

    private ExtraDataService() {
    }

    public static void save(Object key, Object extraObject, String extraName) {

        if (key == null || extraObject==null) return;

        String finalExtraName = StringUtils.isEmpty(extraName) ? extraObject.getClass().getName() : extraName;
        ExtraData extraData = getQuery(key, finalExtraName).get();
        if (extraData == null) {
            extraData = new ExtraData();
            extraData.setData(Json.toJson(extraObject).getBytes());
            extraData.setExtraKey(key.toString());
            extraData.setCreateDate(LocalDateTime.now());
            extraData.setTypeName(finalExtraName);
            extraData.setUpdateDate(LocalDateTime.now());
            Query.db.insert(extraData);
        } else {
            extraData.setUpdateDate(LocalDateTime.now());
            extraData.setData(Json.toJson(extraObject).getBytes());
            Query.db.update(extraData);
        }
    }

    public static void save(Object key, Object extraObject) {
        save(key, extraObject, null);
    }


    public static <T> T getExtraData(Object key, Class<T> extraType, String extraName) {

        if(key==null || extraType==null)return null;

        String finalExtraName = StringUtils.isEmpty(extraName) ? extraType.getName() : extraName;
        ExtraData extraData = getQuery(key, finalExtraName).get();
        if (extraData != null) {
            return Json.parse(new String(extraData.getData(), Charsets.UTF_8), extraType);
        } else {
            return null;
        }
    }


    public static <T> T getExtraData(Object key, Class<T> extraType) {
        return getExtraData(key, extraType, null);
    }

    private static Query<ExtraData> getQuery(Object key, String extraName) {
        return Query.of(ExtraData.class).eq("extraKey", key).eq("typeName", extraName);
    }

}
