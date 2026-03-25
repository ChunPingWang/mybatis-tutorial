# MyBatis JDK 8 教程

一個完整的 MyBatis 教程專案，相容 JDK 8，使用 H2 內嵌資料庫，開箱即用。

## 技術棧

| 元件 | 版本 |
|------|------|
| JDK | 1.8+ |
| MyBatis | 3.5.13 |
| H2 Database | 2.1.214 |
| JUnit | 4.13.2 |
| Logback | 1.2.12 |

## 專案結構

```
src/
├── main/
│   ├── java/com/example/mybatis/
│   │   ├── App.java                  # 主程式 (示範入口)
│   │   ├── mapper/
│   │   │   ├── UserMapper.java       # 使用者 Mapper (XML 方式)
│   │   │   └── OrderMapper.java      # 訂單 Mapper (註解方式)
│   │   ├── model/
│   │   │   ├── User.java             # 使用者實體
│   │   │   └── Order.java            # 訂單實體
│   │   └── util/
│   │       └── MyBatisUtil.java      # SqlSessionFactory 工具類別
│   └── resources/
│       ├── mybatis-config.xml         # MyBatis 全域配置
│       ├── logback.xml                # 日誌配置
│       ├── db/
│       │   ├── schema.sql             # 建表語句
│       │   └── data.sql               # 初始資料
│       └── mapper/
│           ├── UserMapper.xml         # 使用者 SQL 映射
│           └── OrderMapper.xml        # 訂單 SQL 映射
└── test/
    └── java/com/example/mybatis/mapper/
        ├── UserMapperTest.java        # 使用者 Mapper 測試
        └── OrderMapperTest.java       # 訂單 Mapper 測試
```

## 教程涵蓋功能

### 1. 基本 CRUD 操作
- 新增 (Insert) — 自動回填主鍵
- 查詢 (Select) — 單筆/列表
- 更新 (Update) — 選擇性更新非 null 欄位
- 刪除 (Delete)

### 2. 動態 SQL
- `<if>` — 條件判斷
- `<where>` — 智慧 WHERE 子句
- `<set>` — 智慧 SET 子句 (避免多餘逗號)
- `<foreach>` — 批次 IN 查詢

### 3. 關聯查詢
- **一對多** — User → Orders (`<collection>`)
- **多對一** — Order → User (`<association>`)

### 4. 兩種 SQL 定義方式
- **XML 映射檔** — `UserMapper.xml` (適合複雜 SQL)
- **註解方式** — `OrderMapper.java` 使用 `@Select`, `@Insert`, `@Update`, `@Delete`

### 5. 其他特性
- 駝峰命名自動映射 (`mapUnderscoreToCamelCase`)
- SQL 片段重用 (`<sql>` + `<include>`)
- ResultMap 繼承 (`extends`)
- SLF4J + Logback 日誌整合

## 快速開始

```bash
# 編譯並執行測試
mvn clean test

# 執行主程式示範
mvn exec:java -Dexec.mainClass="com.example.mybatis.App"
```

## 執行測試

```bash
mvn test
```

測試包含 17 個測試案例，涵蓋所有 CRUD、動態 SQL 和關聯查詢功能。
