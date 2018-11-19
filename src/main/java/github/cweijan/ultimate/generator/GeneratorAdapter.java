package github.cweijan.ultimate.generator;

import github.cweijan.ultimate.generator.impl.MysqlGenerator;
import github.cweijan.ultimate.db.config.DbConfig;

import java.util.Objects;

/**
 根据DB配置获取sql生成器
 */
public class GeneratorAdapter{

    private String driverName;

    public GeneratorAdapter(DbConfig config){

        this.driverName =config.getDriver();
    }

    public SqlGenerator getGenerator(){

        Objects.requireNonNull(driverName);
        if("com.mysql.jdbc.Driver".equals(driverName)){
            return new MysqlGenerator();
        }

        return null;
    }
}
