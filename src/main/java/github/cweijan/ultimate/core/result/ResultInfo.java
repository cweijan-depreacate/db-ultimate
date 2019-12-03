package github.cweijan.ultimate.core.result;

/**
 * @author cweijan
 * @version 2019/10/12 18:00
 */
public class ResultInfo {

    private Integer updateLine;
    private Long generateKey;

    public Long getGenerateKey() {
        return generateKey;
    }

    public void setGenerateKey(Long generateKey) {
        this.generateKey = generateKey;
    }

    public Integer getUpdateLine() {
        return updateLine;
    }

    public void setUpdateLine(Integer updateLine) {
        this.updateLine = updateLine;
    }
}
