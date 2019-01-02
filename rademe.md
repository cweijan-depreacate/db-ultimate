# db-ultimate
目前Java有很多操作Db的框架, 但是用起来都不和我的心意, 需要重复的地方太多, 于是, db-ultimate诞生了, 一个极速开发的ORM框架

## QuickStart
1. [下载](https://github.com/cweijan/db-ultimate/releases)Jar包, 加入项目依赖,对DbUltimate进行集成配置
2. 使用**Table**注解对数据库对象和实体进行映射

## 定义TableComponent
``` java
import github.cweijan.ultimate.annotation.Table;
import github.cweijan.ultimate.annotation.Exclude;
import github.cweijan.ultimate.annotation.Primary;
import github.cweijan.ultimate.annotation.Table;

//值为标名,默认表名为类名
@Table(value = "t_booklist")
public class Book{

    //表明主键
    @Primary
    private Integer id;

    //列名映射,如果不添加注解,则为字段名(默认会将驼峰转为下划线模式)
    @Column("book_title")
    private String booktitle;

    private String isbn;

    //需要排除ORM映射的字段
    @Exclude
    private String mark;

    public Integer getId(){

        return id;
    }
    //...
    //省略了其他getter和setter方法    
}
```


## 集成Spring-Boot
在applciation.properites加上配置信息, 内置[HikariCp](https://github.com/brettwooldridge/HikariCP)数据库连接池, [点击](#SpringBoot配置详解)查看更多配置信息
``` 
ultimate.jdbc.driver=com.mysql.jdbc.Driver
ultimate.jdbc.username=root
ultimate.jdbc.password=root
ultimate.jdbc.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8
#配置扫描的TableCommponent路径
ultimate.jdbc.scanPackage=gitbhu.cweijan
```

**进行查询**
``` java
@Autowired
private DbUltimate ultimate;

public void testSelect(){
    List<Book> books = ultimate.find(Book.class);
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

DbUltimate dbUltimate = new DbUltimate(dbConfig);

List<Admin> admins = dbUltimate.find(Admin.class);
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

## Operation对象
除了插入操作外, 更新删除查询主要围绕Operation对象进行
``` java
Operation<Admin> operation = Operation.build(Admin.class);
operation.equals("test", "test2"); // ==查询
operation.notEquals("test", "test2"); // !=查询
operation.search("test", "t"); //like查询
operation.join(Lib.class, "ad.id=l.id"); //连表查询,建议在TableComponent配置表别名
```

## 插入
直接插入Java对象即可
``` java
Admin admin = new Admin();
admin.setMessage("hello");
admin.setTest("test");
admin.setDate(new Date());
dbUltimate.insert(admin);
```

## 更新
``` java
//直接对对象进行更新,此方法只支持根据主键更新
Admin admin = new Admin();
admin.setId(2);
admin.setMessage("cweijain");
dbUltimate.update(admin);

//使用operation模式
Operation<Admin> operation = Operation.build(Admin.class);
operation.update("test","test2");
dbUltimate.update(operation);
```


## 查询
``` java
//使用查询Admin
Operation<Admin> operation = Operation.build(Admin.class);
List<Admin> admins = dbUltimate.find(operation);
//使用TableComponent的class对象直接查询
List<Admin> admins = dbUltimate.find(Admin.class, 0, 20);
```

## 删除
``` java
//删除id为1的admin
Operation<Admin> operation = Operation.build(Admin.class);    
operation.equals("id", "1");
dbUltimate.delete(operation);
```