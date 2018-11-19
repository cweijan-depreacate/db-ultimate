package github.cweijan.ultimate.generator;

import github.cweijan.ultimate.component.info.ComponentInfo;
import github.cweijan.ultimate.core.Operation;

public interface SqlGenerator{

    String generateInsertSql(Object component,boolean selective);

    String generateDeleteSql(ComponentInfo componentInfo, Operation operation);

    String generateUpdateSql(ComponentInfo componentInfo, Operation operation);

    String generateSelectSql(ComponentInfo componentInfo, Operation operation);

    String generateCountSql(ComponentInfo componentInfo, Operation operation);
}
