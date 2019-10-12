package github.cweijan.ultimate.core.query;

/**
 * @author cweijan
 * @version 2019/10/12/012 21:39
 */
public enum  QueryType {

    equals("="),not_equlas("!="),
    great_equlas(">="),less_equals("<="),
    isNull("is null"),isNotNull("is not null")
    ,like("like"),in("in");

    private String code;

    public String getCode() {
        return code;
    }

    QueryType(String code) {
        this.code = code;
    }

}
