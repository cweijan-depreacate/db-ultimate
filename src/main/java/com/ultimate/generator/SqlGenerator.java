package com.ultimate.generator;

import com.ultimate.component.info.ComponentInfo;
import com.ultimate.core.Condition;

public interface SqlGenerator{

    String generateInsertSql(Object component,boolean selective);

    String generateDeleteSql(ComponentInfo componentInfo, Condition condition);

    String generateUpdateSql(ComponentInfo componentInfo, Condition condition);

    String generateSelectSql(ComponentInfo componentInfo, Condition condition);

    String generateCountSql(ComponentInfo componentInfo, Condition condition);
}
