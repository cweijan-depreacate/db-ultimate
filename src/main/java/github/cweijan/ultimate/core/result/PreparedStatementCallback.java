package github.cweijan.ultimate.core.result;

import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementCallback<T> {

	@Nullable
	T handlerResultSet(ResultSet resultSet, ResultInfo reusltInfo) throws SQLException, DataAccessException;

}
