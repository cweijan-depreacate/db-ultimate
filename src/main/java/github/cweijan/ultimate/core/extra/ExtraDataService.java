package github.cweijan.ultimate.core.extra;

import github.cweijan.ultimate.annotation.extra.ExtraName;
import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.util.Json;
import github.cweijan.ultimate.util.StringUtils;
import kotlin.text.Charsets;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public final class ExtraDataService {

    private ExtraDataService() {
    }

    public static void save(Object key, Object extraObject, String extraName) {

        if (key == null || extraObject==null) return;

        String finalExtraName = StringUtils.isEmpty(extraName) ? getExtraName(extraObject.getClass()) : extraName;
        ExtraData extraData = getQuery(key, finalExtraName).get();
        if (extraData == null) {
            extraData = new ExtraData();
            extraData.setData(Json.toJson(extraObject).getBytes());
            extraData.setExtraKey(key.toString());
            extraData.setCreateDate(LocalDateTime.now());
            extraData.setTypeName(finalExtraName);
            extraData.setExprieMinute(getExtraMinute(extraObject.getClass()));
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

        String finalExtraName = StringUtils.isEmpty(extraName) ? getExtraName(extraType) : extraName;
        ExtraData extraData = getQuery(key, finalExtraName).get();
        if (extraData != null && (extraData.getExprieMinute() == -1 || extraData.getUpdateDate().plusMinutes(extraData.getExprieMinute()).isAfter(LocalDateTime.now()))) {
            return Json.parse(new String(extraData.getData(), Charsets.UTF_8), extraType);
        } else {
            return null;
        }
    }


    public static <T> T getExtraData(Object key, Class<T> extraType) {
        return getExtraData(key, extraType, null);
    }

    public static void expireExtraData(@NotNull Object key, @NotNull Class<?> extraType, int minute) {
        getQuery(key, getExtraName(extraType)).update("exprieMinute", minute).executeUpdate();
    }

    private static Query<ExtraData> getQuery(Object key, String extraName) {
        return Query.of(ExtraData.class).eq("extraKey", key).eq("typeName", extraName);
    }

    private static int getExtraMinute(Class<?> extraType) {

        ExtraName extraNameAnnotaion = extraType.getAnnotation(ExtraName.class);
        return extraNameAnnotaion != null ? extraNameAnnotaion.expireMinute() : -1;
    }

    private static String getExtraName(@NotNull Class<?> extraType) {
        ExtraName extraNameAnnotaion = extraType.getAnnotation(ExtraName.class);
        return extraNameAnnotaion != null ? extraNameAnnotaion.value() : extraType.getName();
    }
}
