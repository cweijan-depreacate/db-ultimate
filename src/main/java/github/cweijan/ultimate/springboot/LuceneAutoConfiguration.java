package github.cweijan.ultimate.springboot;

import github.cweijan.ultimate.core.lucene.IndexService;
import github.cweijan.ultimate.core.lucene.LuceneQuery;
import github.cweijan.ultimate.core.lucene.config.LuceneConfig;
import github.cweijan.ultimate.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cweijan
 * @version 2019/7/17/017 10:23
 */
@Configuration
@EnableConfigurationProperties(LuceneConfig.class)
public class LuceneAutoConfiguration {

    @Autowired(required = false)
    private LuceneConfig luceneConfig;

    @Bean
    public IndexService indexService(){
        if(luceneConfig==null || StringUtils.isEmpty(luceneConfig.getIndexDirPath()))return null;

        LuceneQuery.init(luceneConfig.getIndexDirPath());


        return LuceneQuery.indexService;
    }

}
