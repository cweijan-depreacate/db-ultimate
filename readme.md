# db-ultimate
目前Java有很多操作数据库的框架, 对于单表的操作代码冗余率都太高, 于是, 我开发了db-ultimate, 用于极速开发的ORM框架, 最大力度的减少重复代码

## QuickStart

第一步, 为SpringBoot项目配置依赖

```
<dependency>
    <groupId>io.github.cweijan</groupId>
    <artifactId>db-ultimate</artifactId>
    <version>1.4.6</version>
</dependency>
```

第二步, 配置数据库连接池(**如果工程内已配置数据库连接池可跳过此步**)

在application.properties加上配置信息, 内置[HikariCp](https://github.com/brettwooldridge/HikariCP)数据库连接池, [点击](#SpringBoot配置详解)查看更多配置详情
``` 
ultimate.jdbc.driver=com.mysql.jdbc.Driver
ultimate.jdbc.username=root
ultimate.jdbc.password=root
ultimate.jdbc.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8
```

第三步, 假设你有student表

``` java
import github.cweijan.ultimate.annotation.Primary;

public class Student{

    //必须有名为id的主键
    private Integer id;

    private String name;

    // getter and setter..
}
```

[ORM映射参考](#orm%e6%98%a0%e5%b0%84)

**进行查询**
``` java
@Test
public void testSelect(){
    List<Student> StudentList = Query.of(Student.class).list();
    System.out.println(StudentList);
}
```
至此, 第一个DbUltimate程序运行完成

# Api

## Query对象
DbUltimate的核心操作类, 通过该对象的方法进行各种条件筛选
``` java
Query<Student> query = Query.of(Student.class);
query.eq("test", "test2"); // ==查询
      .ne("test", "test2"); // !=查询
      .like("test", "t"); //like查询
```


## 查询
1. list查询, 调用**list**方法
``` java
List<Student> studentList = Query.of(Student.class).list();
```

2. get查询, 调用**get**方法

``` java
Student student = Query.of(Student.class).eq("id",1).get();
```

3. 分页查询, 调用**pageList**方法 ,自动进行count查询
``` java
Pagination<Student> pagination = Query.of(Student.class)
            .pageList(1,20); // 参数为页码和每页大小
```

## 插入
调用**Query.db.insert**即可持久化至数据库
``` java
Student student = new Student();
student.setName("小明");
Query.db.insert(student);
```

## 更新
1. 调用**Query.db.update**即可直接对数据进行更新, 调用此方法对象**主键必须赋值**
``` java
Student student = new Student();
student.setId(1);
student.setName("小白");
Query.db.update(student);
```
2. 使用Query对象
```java
Query.of(Student.class).eq("id",1).update("name","小白").executeUpdate();
```



## 删除
使用Query对象
``` java
//删除id为1的Student
Query.of(Student.class).eq("id", "1").executeDelete();
```

## ServiceInject

DbUltimate内置了一个方便的Service基类ServiceInject, 只需继承就可让Service拥有增删改查的功能


``` java
import github.cweijan.ultimate.springboot.util.ServiceInject;

@Service
public class StudentService extends ServiceInject<StudentService>{

}
```

## SpringBoot相关配置
```
#是否启用dbultimate
ultimate.jdbc.enable=true

#是否将实体和数据包进行双向更新, 可选值有none、update(只更新表)、init(重新创建表)
ultimate.jdbc.table-mode=none

#配置扫描实体的包名, 多个包使用逗号分割, 不配置扫描包则启用懒加载
ultimate.jdbc.scanPackage=github.cweijan

#开发环境启用可支持对实体进行热加载, 生产环境请设置为false
ultimate.jdbc.develop=false

#是否显示sql语句
ultimate.jdbc.showSql=true

#数据库连接池配置(如果工程内已配置了数据库连接池则以下配置无效)
ultimate.jdbc.driver=com.mysql.jdbc.Driver
ultimate.jdbc.username=root
ultimate.jdbc.password=root
ultimate.jdbc.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8
ultimate.jdbc.minimumIdle=5
ultimate.jdbc.maximumPoolSize=20
```

## ORM映射

``` java
import github.cweijan.ultimate.annotation.Table;
import github.cweijan.ultimate.annotation.Exclude;
import github.cweijan.ultimate.annotation.Primary;
import github.cweijan.ultimate.annotation.Table;

//可通过Table注解配置表名
@Table("s_student")
public class Student{

    //配置主键
    //不配置默认查找名为id的Field
    @Primary
    private Integer id;

    //列名映射
    //如果不添加注解,则将字段名从驼峰命名转为下划线映射为列名
    @Column("s_name")
    private String name;

    //排除, 不进行映射
    @Exclude
    private String mark;


    public Integer getId(){

        return id;
    }
    //...
}
```

## 普通java工程使用DbUltimate
``` java
DbConfig dbConfig = new DbConfig();
dbConfig.setUrl("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8");
dbConfig.setDriver("com.mysql.jdbc.Driver");
dbConfig.setUsername("root");
dbConfig.setPassword("root");

//调用init方法成功后即启动完成
Query.init(dbConfig);

List<Student> studentList = Query.of(Student.class).list();
System.out.println(studentList);

```