package com.utilmate.test.db;

import com.ultimate.bean.Admin;
import com.ultimate.convert.TypeConvert;
import com.utilmate.test.base.BaseTest;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TestJdbc extends BaseTest{

    @Test
    public void TestJdbc() throws SQLException{

        Connection connection = dbConfig.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from rh_admin where message=? ");
        preparedStatement.setString(1,"'msg' or 1=1");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Admin> admins = TypeConvert.resultSetToBeanList(resultSet, Admin.class);
        System.out.println(admins);

    }

}
