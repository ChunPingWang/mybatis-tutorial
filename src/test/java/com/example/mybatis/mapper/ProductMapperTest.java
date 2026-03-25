package com.example.mybatis.mapper;

import com.example.mybatis.model.Product;
import com.example.mybatis.util.MyBatisUtil;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * ProductMapper 單元測試
 *
 * 涵蓋所有進階 XML SQL 功能:
 * - choose / when / otherwise
 * - trim
 * - bind
 * - foreach (批次插入 / 批次刪除)
 * - 巢狀查詢 (Nested Select)
 * - selectKey
 * - Map 參數
 */
public class ProductMapperTest {

    @BeforeClass
    public static void initSchema() {
        try (SqlSession session = MyBatisUtil.openSession(true)) {
            ScriptRunner runner = new ScriptRunner(session.getConnection());
            runner.setLogWriter(null);
            InputStream schemaStream = ProductMapperTest.class.getClassLoader().getResourceAsStream("db/schema.sql");
            if (schemaStream != null) {
                runner.runScript(new InputStreamReader(schemaStream, StandardCharsets.UTF_8));
            }
        }
    }

    @Before
    public void initData() {
        try (SqlSession session = MyBatisUtil.openSession(true)) {
            session.getConnection().createStatement().execute("DELETE FROM products");
            session.getConnection().createStatement().execute("DELETE FROM orders");
            session.getConnection().createStatement().execute("DELETE FROM users");
            session.getConnection().createStatement().execute("DELETE FROM categories");
            session.getConnection().createStatement().execute("ALTER TABLE products ALTER COLUMN id RESTART WITH 1");
            session.getConnection().createStatement().execute("ALTER TABLE categories ALTER COLUMN id RESTART WITH 1");
            session.getConnection().createStatement().execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
            session.getConnection().createStatement().execute("ALTER TABLE orders ALTER COLUMN id RESTART WITH 1");

            ScriptRunner runner = new ScriptRunner(session.getConnection());
            runner.setLogWriter(null);
            InputStream dataStream = ProductMapperTest.class.getClassLoader().getResourceAsStream("db/data.sql");
            if (dataStream != null) {
                runner.runScript(new InputStreamReader(dataStream, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ================================================================
    // 基本查詢
    // ================================================================

    @Test
    public void testSelectById() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            Product product = mapper.selectById(1L);
            assertNotNull(product);
            assertEquals("MyBatis 實戰指南", product.getName());
            assertEquals(0, new BigDecimal("59.90").compareTo(product.getPrice()));
        }
    }

    @Test
    public void testSelectAll() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            List<Product> products = mapper.selectAll();
            assertEquals(7, products.size());
        }
    }

    // ================================================================
    // 示範 1: choose / when / otherwise
    // ================================================================

    @Test
    public void testSelectWithDynamicSort_byPrice() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            List<Product> products = mapper.selectWithDynamicSort("price");
            assertFalse(products.isEmpty());
            // 驗證按價格升序排列 (排除 DISCONTINUED)
            for (int i = 1; i < products.size(); i++) {
                assertTrue(products.get(i).getPrice().compareTo(products.get(i - 1).getPrice()) >= 0);
            }
        }
    }

    @Test
    public void testSelectWithDynamicSort_byStock() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            List<Product> products = mapper.selectWithDynamicSort("stock");
            assertFalse(products.isEmpty());
            // 驗證按庫存降序排列
            for (int i = 1; i < products.size(); i++) {
                assertTrue(products.get(i).getStock() <= products.get(i - 1).getStock());
            }
        }
    }

    @Test
    public void testSelectWithDynamicSort_default() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            // 傳入未知值，應按預設 id 排序
            List<Product> products = mapper.selectWithDynamicSort("unknown");
            assertFalse(products.isEmpty());
            for (int i = 1; i < products.size(); i++) {
                assertTrue(products.get(i).getId() > products.get(i - 1).getId());
            }
        }
    }

    @Test
    public void testSelectByPriceRange_cheap() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            List<Product> products = mapper.selectByPriceRange("cheap", null);
            // 已下架商品 price=10 < 50
            for (Product p : products) {
                assertTrue(p.getPrice().compareTo(new BigDecimal("50")) < 0);
            }
        }
    }

    @Test
    public void testSelectByPriceRange_medium() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            List<Product> products = mapper.selectByPriceRange("medium", "ON_SALE");
            assertFalse(products.isEmpty());
            for (Product p : products) {
                assertTrue(p.getPrice().compareTo(new BigDecimal("50")) >= 0);
                assertTrue(p.getPrice().compareTo(new BigDecimal("100")) <= 0);
                assertEquals("ON_SALE", p.getStatus());
            }
        }
    }

    @Test
    public void testCountByType_all() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            int count = mapper.countByType("all");
            assertEquals(7, count);
        }
    }

    @Test
    public void testCountByType_onSale() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            int count = mapper.countByType("on_sale");
            // ON_SALE: MyBatis實戰, Java核心, Redis, Docker, K8s = 5
            assertEquals(5, count);
        }
    }

    // ================================================================
    // 示範 2: trim
    // ================================================================

    @Test
    public void testSelectByTrimCondition_allNull() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            // 所有條件為 null，應返回全部商品
            List<Product> products = mapper.selectByTrimCondition(null, null, null);
            assertEquals(7, products.size());
        }
    }

    @Test
    public void testSelectByTrimCondition_withStatus() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            List<Product> products = mapper.selectByTrimCondition(null, "OUT_OF_STOCK", null);
            assertEquals(1, products.size());
            assertEquals("MySQL 高效能", products.get(0).getName());
        }
    }

    @Test
    public void testSelectByTrimCondition_multipleConditions() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            List<Product> products = mapper.selectByTrimCondition(null, "ON_SALE", 1L);
            // 分類 1 (程式設計) 且 ON_SALE: MyBatis實戰, Java核心 = 2
            assertEquals(2, products.size());
        }
    }

    @Test
    public void testUpdateByTrim() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);

            Product update = new Product();
            update.setId(1L);
            update.setPrice(new BigDecimal("49.90"));
            update.setStock(80);
            // name, status, description 為 null，不應被更新

            int rows = mapper.updateByTrim(update);
            session.commit();

            assertEquals(1, rows);
            Product updated = mapper.selectById(1L);
            assertEquals(0, new BigDecimal("49.90").compareTo(updated.getPrice()));
            assertEquals(Integer.valueOf(80), updated.getStock());
            assertEquals("MyBatis 實戰指南", updated.getName()); // 未被覆蓋
        }
    }

    // ================================================================
    // 示範 3: bind
    // ================================================================

    @Test
    public void testSearchByName() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            List<Product> products = mapper.searchByName("Java");
            assertEquals(1, products.size());
            assertEquals("Java 核心技術", products.get(0).getName());
        }
    }

    @Test
    public void testSearchByName_multipleResults() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            // "實" 出現在多個商品名稱中
            List<Product> products = mapper.searchByName("實");
            assertTrue(products.size() >= 2);
        }
    }

    // ================================================================
    // 示範 4: foreach 批次插入
    // ================================================================

    @Test
    public void testBatchInsert() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);

            Product p1 = new Product("批次商品A", 1L, new BigDecimal("29.90"), 10);
            Product p2 = new Product("批次商品B", 2L, new BigDecimal("39.90"), 20);
            Product p3 = new Product("批次商品C", 3L, new BigDecimal("49.90"), 30);

            int rows = mapper.batchInsert(Arrays.asList(p1, p2, p3));
            session.commit();

            assertEquals(3, rows);

            // 驗證插入成功
            List<Product> all = mapper.selectAll();
            assertEquals(10, all.size()); // 7 + 3
        }
    }

    // ================================================================
    // 示範 5: foreach 批次刪除
    // ================================================================

    @Test
    public void testBatchDeleteByIds() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);

            int rows = mapper.batchDeleteByIds(Arrays.asList(6L, 7L));
            session.commit();

            assertEquals(2, rows);
            assertNull(mapper.selectById(6L));
            assertNull(mapper.selectById(7L));
        }
    }

    // ================================================================
    // 示範 6: 巢狀查詢 (Nested Select)
    // ================================================================

    @Test
    public void testSelectWithCategory() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            Product product = mapper.selectWithCategory(1L);
            assertNotNull(product);
            assertEquals("MyBatis 實戰指南", product.getName());
            // 巢狀查詢會自動載入分類
            assertNotNull(product.getCategory());
            assertEquals("程式設計", product.getCategory().getName());
        }
    }

    @Test
    public void testSelectAllWithCategory() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            List<Product> products = mapper.selectAllWithCategory();
            assertFalse(products.isEmpty());
            // 驗證每個商品都有分類資訊
            for (Product p : products) {
                assertNotNull("Product " + p.getName() + " should have category", p.getCategory());
                assertNotNull(p.getCategory().getName());
            }
        }
    }

    // ================================================================
    // 示範 7: selectKey
    // ================================================================

    @Test
    public void testInsertWithSelectKey() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);

            Product product = new Product("selectKey 測試商品", 1L, new BigDecimal("19.90"), 5);
            int rows = mapper.insertWithSelectKey(product);
            session.commit();

            assertEquals(1, rows);
            assertNotNull(product.getId());
            assertTrue(product.getId() > 0);

            // 驗證可以用回填的 ID 查詢
            Product fetched = mapper.selectById(product.getId());
            assertNotNull(fetched);
            assertEquals("selectKey 測試商品", fetched.getName());
        }
    }

    // ================================================================
    // 示範 8: Map 參數
    // ================================================================

    @Test
    public void testSelectByMap_priceRange() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("minPrice", new BigDecimal("50"));
            params.put("maxPrice", new BigDecimal("80"));
            params.put("status", "ON_SALE");
            params.put("orderBy", "price");

            List<Product> products = mapper.selectByMap(params);
            assertFalse(products.isEmpty());
            for (Product p : products) {
                assertTrue(p.getPrice().compareTo(new BigDecimal("50")) >= 0);
                assertTrue(p.getPrice().compareTo(new BigDecimal("80")) <= 0);
                assertEquals("ON_SALE", p.getStatus());
            }
        }
    }

    @Test
    public void testSelectByMap_nameSearch() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("name", "Docker");

            List<Product> products = mapper.selectByMap(params);
            assertEquals(1, products.size());
            assertEquals("Docker 實踐", products.get(0).getName());
        }
    }

    @Test
    public void testSelectByMap_emptyParams() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);

            Map<String, Object> params = new HashMap<String, Object>();
            List<Product> products = mapper.selectByMap(params);
            assertEquals(7, products.size()); // 全部商品
        }
    }

    @Test
    public void testSelectByCategoryId() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            ProductMapper mapper = session.getMapper(ProductMapper.class);
            List<Product> products = mapper.selectByCategoryId(2L);
            // 分類 2 (資料庫): MySQL高效能, Redis = 2
            assertEquals(2, products.size());
        }
    }
}
