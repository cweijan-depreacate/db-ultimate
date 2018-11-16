package com.ultimate.generator;

import com.ultimate.db.config.DbConfig;
import com.ultimate.generator.impl.MysqlGenerator;

import java.util.Objects;

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
