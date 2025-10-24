# mybatis-plus-enhancer

[![Maven Central](https://img.shields.io/maven-central/v/io.github.luxmixus/mybatis-plus-enhancer)](https://mvnrepository.com/artifact/io.github.luxmixus/mybatis-plus-enhancer)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![GitHub](https://img.shields.io/github/stars/luxmixus/mybatis-plus-enhancer?style=social)](https://github.com/luxmixus/mybatis-plus-enhancer)

MyBatis-Plus 增强工具包，提供动态SQL构建、后缀映射查询、IService和BaseMapper增强功能，以及Excel导入导出支持。

## 功能特性

- **后缀SQL构建**：支持`字段`+`后缀`自动映射不同类型查询
- **动态SQL构建**：支持根据入参动态拼接条件
- **联表属性查询**：支持非本表字段的查询自动映射
- **Map查询条件**：自动转化Map参数
- **数据字段映射**：自动转换属性为数据库字段
- **SQL反注入**：通过预编译SQL, 防止SQL注入
- **Lambda链式调用**：支持链式调用追加参数条件
- **VO类型转化**：自动将查询结果转化为指定类
- **BaseMapper增强**：添加`voById`、`voList`、`voPage`等方法
- **IService查询增强**：添加`voById`、`voList`、`voPage` 等方法
- **IService业务增强**：添加`insertByDTO`、`updateByDTO`等方法
- **IService集成Excel**：集成`FastExcel`和`EasyExcel`, 支持Excel导入/导出


## 仓库地址

- GitHub: https://github.com/luxmixus/mybatis-plus-enhancer
- Maven Central: https://central.sonatype.com/artifact/io.github.luxmixus/mybatis-plus-enhancer

## maven依赖
当前最新版本为:  
[![Maven Central](https://img.shields.io/maven-central/v/io.github.luxmixus/mybatis-plus-enhancer)](https://mvnrepository.com/artifact/io.github.luxmixus/mybatis-plus-enhancer)
```xml
<dependency>
    <groupId>io.github.luxmixus</groupId>
    <artifactId>mybatis-plus-enhancer</artifactId>
    <version>latest</version>
</dependency>
```

## 快速开始

### 1. 创建mybatis-plus实体类和BaseMapper 
若已有mybatis-plus实体类和BaseMapper, 可跳过此步骤

```java
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    // getter/setter...
}
```
```java
public interface SysUserMapper extends BaseMapper<SysUser> {
    
}
```

### 2. 扩展mapper接口
* 创建或指定`VO类`, 用于展示查询结果
* 使`mapper`接口继承[EnhancedMapper](src/main/java/io/github/luxmixus/mybatisplus/enhancer/EnhancedMapper.java), 并指定泛型为VO类
* 通过`工具类`获取`mapper.xml`内容, 并将其复制到对应xml文件中
* (可选) 若有service层, 可使service实现[EnhancedService](src/main/java/io/github/luxmixus/mybatisplus/enhancer/EnhancedService.java)接口, 即可拥有mapper对应所有方法


```java
// 用于封装查询结果的VO类, 可以继承自实体类, 也可以直接使用实体类
public class SysUserVO {
    private Long id;
    private String name;
    private Integer age;
    // getter/setter...
}
```
```java
// mapper接口文件, 使其继承EnhancedMapper接口
public interface SysUserMapper extends BaseMapper<SysUser>, 
        EnhancedMapper<SysUserVO> {
}
```

```java
import util.io.github.luxmixus.mybatisplus.enhancer.MapperUtil;

// 通过工具类获取mapper.xml文件的sql片段
public static void main(String[] args) {
    var mapperContent = MapperUtil.getMapperContent(SysUserMapper.class);
    System.out.println(mapperContent);
}
```
```xml
<!--复制工具类生成的该sql片段到mapper.xml文件中-->
<select id="voQueryByXml" resultType="com.example.test.vo.SysUserVO">
    SELECT a.* FROM sys_user a
    <where>
        <include refid="io.github.luxmixus.mybatisplus.enhancer.EnhancedMapper.queryFragment"/>
    </where>
    <trim prefix="ORDER BY" prefixOverrides=",">
        <include refid="io.github.luxmixus.mybatisplus.enhancer.EnhancedMapper.sortFragment"/>
    </trim>
</select>
```

### 3. 使用示例

```java
import helper.query.io.github.luxmixus.mybatisplus.enhancer.SqlHelper;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    // 根据ID查询VO
    @GetMapping("/{id}")
    public SysUserVO getUserById(@PathVariable Long id) {
        return sysUserService.voById(id);
    }

    // 通过DTO对象查询
    @PostMapping("/dto")
    public List<SysUserVO> getUsersByDTO(@RequestBody SysUserDTO dto) {
        return sysUserService.voList(dto);
    }

    // 通过map条件查询(支持后缀映射不同类型查询)
    @PostMapping("/map")
    public List<SysUserVO> getUsersByMap(@RequestBody Map<String, Object> params) {
        return sysUserService.voList(params);
    }

    // 入参拼装动态sql查询
    @PostMapping("/sql")
    public List<SysUserVO> getUsersBySql(@RequestBody SqlHelper<SysUser> sqlHelper) {
        return sysUserService.voList(sqlHelper);
    }

    // lambda调用,封装必须条件
    @PostMapping("/lambda")
    public List<SysUserVO> getUsersBySql(@RequestBody Map<String, Object> params) {
        return SqlHelper.of(SysUser.class)
                .with(params) // 添加参数, 支持实体类, DTO对象, map, SqlHelper等
                .eq(SysUser::getState,1) // state=1
                .ge(SysUser::getAge, 18) // age>=18
                .like(SysUser::getUserName, "tom") // userName like '%tom%'
                .wrap(sysUserService)
                .voList();
    }

    // 分页查询
    @PostMapping("/page/{current}/{size}")
    public IPage<SysUserVO> getUserPage(@RequestBody Map<String, Object> params,
                                        @PathVariable("current") Long current,
                                        @PathVariable("size") Long size) {
        return sysUserService.voPage(params, current, size);
    }

    // Excel导入
    @PostMapping("/excel/import")
    public int importExcel(@RequestParam("file") MultipartFile file) {
        // 返回导入条数
        return sysUserService.importExcel(file, SysUserVO.class);
        ;
    }

    // Excel导出
    @PostMapping("/excel/export/{current}/{size}")
    public void exportExcel(@RequestBody Map<String, Object> params,
                            @PathVariable("current") Long current,
                            @PathVariable("size") Long size) {
        sysUserService.exportExcel(fileName, SysUserVO.class);
    }


}
```
java代码使用方式请参考:[测试用例](src/test/java/com/example)

## 核心功能

### 后缀查询
- 前端可以在传入参数中添加`字段后缀`轻松实现各种查询需求
- 前端入参在不添加后缀时, 等同于`等于`查询
- 后端可用`实体类`或`Map`接收参数

#### 前端入参示例
若实体属性如下
```json
{
  "name": "mike",
  "version": 1,
  "age": 18,
  "state": 1
}
```
查询`name`包含`mike`, `version`为`1`, `age`在`18-60`之间, `state`为`1`或`2`或`3`数据: 

```json
{
  "nameLike": "mike",
  "version": 1,
  "ageGe": 18,
  "ageLt": 60,
  "stateIn": [1, 2, 3]
}
```

#### 默认的后缀关键字：
- `Ne` - 不等于
- `Lt` - 小于
- `Le` - 小于等于
- `Gt` - 大于
- `Ge` - 大于等于
- `Like` - 模糊匹配
- `NotLike` - 反模糊匹配
- `In` - IN查询
- `NotIn` - NOT IN查询
- `IsNull` - IS NULL
- `IsNotNull` - IS NOT NULL
- `BitWith` - 位运算, 包含指定bit位
- `BitWithout` - 位运算, 不包含指定bit位

#### 自定义后缀
- 在`EnhancedMapper`的子类中,可以重写`voQuery`方法实现自定义的逻辑
- 框架提供`FieldSuffixProcessor`用于便捷的封装自定义后缀
- 支持的操作符(不区分大小写): 
  - `=` - 等于,
  - `<>` - 不等于
  - `>` - 大于
  - `>=` - 大于等于
  - `<` - 小于
  - `<=` - 小于等于
  - `LIKE` - 模糊匹配
  - `NOT LIKE` - 反模糊匹配
  - `IN` - IN查询
  - `NOT IN` - NOT IN查询
  - `IS NULL` - 指定字段为NULL
  - `IS NOT NULL` - 指定字段不为NULL
  - `$>` - 位运算, 包含指定bit位
  - `$=` - 位运算, 不包含指定bit位
```java
public interface SysUserMapper extends BaseMapper<SysUser>, EnhancedMapper<SysUserVO> {
    
  @Override
  default List<V> voQuery(Object param, IPage<V> page) {
    Class<?> entityClass = MybatisPlusReflectUtil.resolveTypeArguments(getClass(), BaseMapper.class)[0];
    HashMap<String, String> map = new HashMap<String, String>(); // 指定后缀和操作符的映射关系
    map.put("_like", "LIKE");
    map.put("_ge", ">=");
    map.put("_le", "<=");
    map.put("_not_eq", "<>");
    map.put("_like", "LIKE");
    SqlHelper<SysUser> sqlHelper = SqlHelper.of(SysUser.class) // 创建对应实体类的sqlHelper
            .with(param) // 封装原来的参数
            .process(FieldSuffixProcessor.of(map)::process) // 字段校验/二次封装
            ;
    return voQueryByXml(sqlHelper, page);
  }

}

```
### 动态SQL

- 前端可以自由指定需要查询的`字段`和`值`, 并自由指定查询类型, 拼接, 排序, 组合多条件
- 后端使用`SqlHelper`对象接收参数

#### 入参示例

原始字段:
```json
{
  "name": "mike",
  "version": 1,
  "age": 18,
  "state": 1
}
```
#### 指定字段检索条件
- 通过`conditions`字段指定查询条件,
- 其中每个条件对象`field`表示字段,`value`表示值,`operator`表示操作符号
- `operator`不填写时,默认为等于, 可选值(不区分大小写)：
  - `=` - 等于(默认),
  - `<>` - 不等于
  - `>` - 大于
  - `>=` - 大于等于
  - `<` - 小于
  - `<=` - 小于等于
  - `LIKE` - 模糊匹配
  - `NOT LIKE` - 反模糊匹配
  - `IN` - IN查询
  - `NOT IN` - NOT IN查询
  - `IS NULL` - 指定字段为NULL
  - `IS NOT NULL` - 指定字段不为NULL
  - `$>` - 位运算, 包含指定bit位
  - `$=` - 位运算, 不包含指定bit位

查询`name`为`mike`, `version`大于等于`1`, `state`为`1`或`2`或`3`的数据
```json
{
  "conditions": [
    {
      "field": "name",
      "value": "mike"
    },
    {
      "field": "version",
      "operator": ">=",
      "value": 1
    },
    {
      "field": "state",
      "operator": "IN",
      "value": [1, 2, 3]
    }
  ]
}
```
#### 指定排序字段
- 通过`sorts`字段指定排序字段, 
- 其中每个条件对象`field`表示排序的字段,`isDesc`表示是否倒序(未指定时默认升序)

查询`name`为`mike`, `version`为`1`的数据, 并将结果按照`id`降序, `age`升序排列
```json
{
  "conditions": [
    {
      "field": "name",
      "value": "mike"
    },
    {
      "field": "version",
      "value": 1
    }
  ],
  "sorts": [
    {
      "field": "id",
      "isDesc": true
    },
    {
      "field": "age"
    }
  ]
}
```
#### 复杂条件拼接
SqlHelper完整结构
- `conditions` - 查询条件
- `sorts` - 排序字段, 仅根节点有效
- `connector` - 条件间的连接符号, `AND`或`OR`, 不指定时默认`AND`
- `child` - 子节点, 一般用于组合嵌套`OR`条件
  - `conditions` - 子节点查询条件
  - `connector` - 子节点条件间的连接符号, `AND`或`OR`, 不指定时默认`AND`
  - `child` - 子子节点(可重复嵌套)

使用建议:
- 根节点的`conditions`字段用于组合`AND`条件 
- 当需要组合`OR`条件时, 将`OR`条件组合在`child`中
- `connector`默认为`AND`,不组合`OR`条件时无需传递
- `child`不使用时, 无需传递
- 
```json
{
  "conditions": [],
  "sorts": [],
  "child": {
    "conditions": [],
    "connector": "OR",
    "child": {
      "conditions": [],
      "connector": "AND",
      "child": {
        "conditions": []
      }
    }
  }
}
```
查询 `version`大于`1`,`state`为`1`, `name`为`mike`或`john`, `age`小于`18`或大于`60`的数据
```sql
select * from sys_user where (version > 1 and state = 1) and (name = 'mike' or name = 'john') and (age < 18 or age > 60)
```
输入参数:
```json
{
  "conditions": [
    {
      "field": "version",
      "operator": ">",
      "value": 1
    },
    {
      "field": "state",
      "value": 1
    }
  ],
  "child": {
    "connector": "OR",
    "conditions": [
      {
        "field": "name",
        "value": "mike"
      },
      {
        "field": "name",
        "value": "john"
      }
    ],
    "child": {
      "connector": "OR",
      "conditions": [
        {
          "field": "age",
          "operator": "<",
          "value": 18
        },
        {
          "field": "age",
          "operator": ">",
          "value": 60
        }
      ]
    }
  }
}
```

## 字段映射
默认字段映射规则为:
- 通过Mybatis-plus的配置和注解来获取字段和数据库列的映射关系
- 满足后缀查询时, 会自动去掉后缀并转化为对应类型查询
- 若后缀查询和字段冲突, 则使用字段映射关系, 例如`nameLike`字段已存在时, 不会映射为`name`的模糊查询
- 若找不到对应的字段映射关系, 则会自动将字段放入`unmapped`中, 供后续处理
- 默认字段映射关系如下:
  - 获取实体类对应的表信息
  - 获取实体类字段信息
  - 获取`@TableField`注解的属性
  - 获取`EnhancedEntity`接口映射的属性

## 多表联查
支持以下方式查询非本表字段
- 自动映射, 兼容`动态SQL`和`动态后缀`查询
  - 通过`@TableField(exist = false, value="xxx")`注解, 将字段封装为指定数据表的指定列
  - 实现`EnhancedEntity`接口, 在`extraFieldColumnMap()`方法中定义字段名和数据库表/列的映射关系
- 在`mapper.xml`文件中自行手动指定

自动映射时, 需要在xml文件中添加需要连接的表和表名

### 通过`@TableFiled`指定

```java
public class SysUserVO {

  @TableField("user_name") // 字段为user_name
  private String userName;

  @TableField(exist = false, value = "role.name") // 映射为role表的name字段
  private String roleName;

  @TableField(exist = false, value = "dept.name") // 映射为dept表的name字段
  private String deptName;
}
``` 

### 实现EnhancedEntity接口

```java
public class SysUserVO implements EnhancedEntity {
  // 属性列表....
    
  @Override
  public Map<String, String> extraFieldColumnMap() {
    var map = new HashMap<Object, Object>();
    map.put("userName", "user_name"); // 将userName映射为实体类对应表的user_name字段
    map.put("roleId", "role.id"); // 将roleId映射为role表的id字段
    map.put("deptId", "dept.id"); // 将deptId映射为dept表的id字段
    return map;
  }
}
``` 

### 在`mapper.xml`文件中自行手动指定
所有不能自动映射的字段和值, 会作为`K`,`V`放入`param1.unmapped`中, 供后续处理, 可以在`mapper.xml`文件中自行手动指定, 如下:
```xml

<select id="voQueryByXml" resultType="com.example.test.vo.SysUserVO">
    SELECT a.* FROM
    sys_user a
    left join sys_role b on a.role_id = b.id
    left join sys_dept c on a.dept_id = c.id
    <where>
        <include refid="io.github.luxmixus.mybatisplus.enhancer.EnhancedMapper.queryFragment"/>
        <!--判断并字段是否存在值, 存在则添加条件-->
        <if test="param1.unmapped.roleName!=null">
            AND b.name = #{param1.unmapped.roleName}
        </if>
        <if test="param1.unmapped.deptName!=null">
            AND c.name = #{param1.unmapped.deptName}
        </if>
    </where>
    <trim prefix="ORDER BY" prefixOverrides=",">
        <include refid="io.github.luxmixus.mybatisplus.enhancer.EnhancedMapper.sortFragment"/>
        <!--添加自定义排序条件-->
        , a.create_time DESC, a.id DESC
    </trim>
</select>
```
