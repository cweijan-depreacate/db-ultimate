package github.cweijan.ultimate.test.db;

import github.cweijan.ultimate.test.bean.Admin;
import github.cweijan.ultimate.convert.TypeConvert;
import github.cweijan.ultimate.test.base.BaseTest;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcTest extends BaseTest{

    @Test
    public void TestGetJdbc() throws SQLException{

        Connection connection = dbConfig.openConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from rh_admin where message=? ");
        preparedStatement.setString(1,"'msg' or 1=1");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Admin> admins = TypeConvert.INSTANCE.resultSetToBeanList(resultSet, Admin.class);
        System.out.println(admins);

    }

}
