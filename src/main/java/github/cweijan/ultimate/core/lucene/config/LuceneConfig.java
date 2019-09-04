package github.cweijan.ultimate.core.lucene.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cweijan
 * @version 2019/7/17/017 10:20
 */
@ConfigurationProperties(prefix = "ultimate.lucene")
public class LuceneConfig {

    /**
     * lucene service index path.
     */
    private String indexDirPath;

    public String getIndexDirPath() {
        return indexDirPath;
    }

    public void setIndexDirPath(String indexDirPath) {
        this.indexDirPath = indexDirPath;
    }
}
