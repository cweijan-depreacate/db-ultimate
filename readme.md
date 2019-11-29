# db-ultimate
目前Java有很多操作数据库的框架, 但是都不和我的心意, 需要重复的代码太多, 于是, 我开发了db-ultimate, 一个极速的ORM框架, 最大力度的减少重复代码

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
#配置需要扫描的TableCommponent包名
ultimate.jdbc.scanPackage=gitbhu.cweijan
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

#数据库连接池最小空闲连接数量
ultimate.jdbc.minimumIdle=5

#数据库连接池数量
ultimate.jdbc.maximumPoolSize=20

#是否自动创建TableComponent的表,目前只支持mysql
ultimate.jdbc.createNonexistsTable=true

#需要扫描TableComponent的包名,多个包使用逗号,分割
ultimate.jdbc.scanPackage=github.cweijan

#是否启用DbUltimate,默认为true
ultimate.jdbc.enable=true

#是否启用开发模式,开发模式支持热加载TableComponent
ultimate.jdbc.develop=false

#是否显示sql语句
ultimate.jdbc.showSql=true

#数据库连接
ultimate.jdbc.driver=com.mysql.jdbc.Driver
ultimate.jdbc.username=root
ultimate.jdbc.password=root
ultimate.jdbc.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8
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
//如果使用Spring则是在spring配置文件中配置DbConfig,并在配置文件中将DbConfig注入DbUltimate

DbConfig dbConfig = new DbConfig();
dbConfig.setUrl("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8");
dbConfig.setDriver("com.mysql.jdbc.Driver");
dbConfig.setUsername("root");
dbConfig.setPassword("root");
dbConfig.setScanPackage("github.cweijan");
dbConfig.setCreateNonexistsTable(true);

//调用init方法成功后即启动完成
Query.init(dbConfig);

List<Admin> admins = Query.of(Admin.class).list();
System.out.println(admins);

```