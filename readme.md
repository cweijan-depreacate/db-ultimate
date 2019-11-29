# db-ultimate
目前Java有很多操作数据库的框架, 但是都不和我的心意, 需要重复的代码太多, 于是, 我开发了db-ultimate, 一个极速的ORM框架, 最大力度的减少重复代码

## QuickStart

第一步, 为SpringBoot项目配置依赖

```
<dependency>
    <groupId>io.github.cweijan</groupId>
    <artifactId>db-ultimate</artifactId>
    <version>1.4.5</version>
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

## ORM映射
**定义TableComponent**
``` java
import github.cweijan.ultimate.annotation.Table;
import github.cweijan.ultimate.annotation.Exclude;
import github.cweijan.ultimate.annotation.Primary;
import github.cweijan.ultimate.annotation.Table;

//值为表名,默认表名为类名
@Table("t_booklist")
public class Book{

    //表明主键
    @Primary
    private Integer id;

    //列名映射,如果不添加注解,则为字段名(默认会将驼峰转为下划线模式)
    @Column("book_title")
    private String booktitle;

    private String isbn;

    public Integer getId(){

        return id;
    }
    //...
}
```

**进行查询**
``` java

@Test
public void testSelect(){
    List<Book> books = Query.of(Book.class).list();
    System.out.println(books);
}

```
至此, 第一个DbUltimate程序运行完成, [更多操作Api](#Api)

## 不使用SpringBoot使用DbUltimate
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

## SpringBoot配置详解
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

# Api

## 插入
直接插入Java对象即可
``` java
Admin admin = new Admin();
admin.setMessage("hello");
admin.setTest("test");
admin.setDate(new Date());
Query.db.insert(admin);
```

## Query对象
更新删除查询主要围绕Query对象进行
``` java
Query<Admin> query = Query.of(Admin.class);
query.eq("test", "test2"); // ==查询
query.ne("test", "test2"); // !=查询
query.search("test", "t"); //like查询
query.join(Lib.class, "ad.id=l.id"); //连表查询,建议在TableComponent配置表别名
List<Admin> adminList=quert.list();
```

## 更新
``` java
//直接对对象进行更新,此方法只支持根据主键更新
Admin admin = new Admin();
admin.setId(2);
admin.setMessage("cweijain");
dbUltimate.update(admin);

//使用operation模式
Query<Admin> query = Query.of(Admin.class);
query.update("test","test2");
query.executeUpdate();
```


## 查询
``` java
//使用查询Admin
List<Admin> admins = Query.of(Admin.class).list();
```

## 删除
``` java
//删除id为1的admin
Query<Admin> query = Query.of(Admin.class);
query.eq("id", "1");
query.executeDelete();
```
