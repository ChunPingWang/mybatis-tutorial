package com.example.mybatis.mapper;

import com.example.mybatis.model.Order;
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
import java.util.List;

import static org.junit.Assert.*;

/**
 * OrderMapper 單元測試
 */
public class OrderMapperTest {

    @BeforeClass
    public static void initSchema() {
        try (SqlSession session = MyBatisUtil.openSession(true)) {
            ScriptRunner runner = new ScriptRunner(session.getConnection());
            runner.setLogWriter(null);
            InputStream schemaStream = OrderMapperTest.class.getClassLoader().getResourceAsStream("db/schema.sql");
            if (schemaStream != null) {
                runner.runScript(new InputStreamReader(schemaStream, StandardCharsets.UTF_8));
            }
        }
    }

    @Before
    public void initData() {
        try (SqlSession session = MyBatisUtil.openSession(true)) {
            session.getConnection().createStatement().execute("DELETE FROM orders");
            session.getConnection().createStatement().execute("DELETE FROM users");
            session.getConnection().createStatement().execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
            session.getConnection().createStatement().execute("ALTER TABLE orders ALTER COLUMN id RESTART WITH 1");

            ScriptRunner runner = new ScriptRunner(session.getConnection());
            runner.setLogWriter(null);
            InputStream dataStream = OrderMapperTest.class.getClassLoader().getResourceAsStream("db/data.sql");
            if (dataStream != null) {
                runner.runScript(new InputStreamReader(dataStream, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSelectById() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            OrderMapper mapper = session.getMapper(OrderMapper.class);
            Order order = mapper.selectById(1L);
            assertNotNull(order);
            assertEquals("MyBatis 實戰指南", order.getProduct());
            assertEquals(Long.valueOf(1L), order.getUserId());
        }
    }

    @Test
    public void testSelectByUserId() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            OrderMapper mapper = session.getMapper(OrderMapper.class);
            List<Order> orders = mapper.selectByUserId(1L);
            assertEquals(2, orders.size());
        }
    }

    @Test
    public void testSelectWithUser() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            OrderMapper mapper = session.getMapper(OrderMapper.class);
            Order order = mapper.selectWithUser(1L);
            assertNotNull(order);
            assertNotNull(order.getUser());
            assertEquals("alice", order.getUser().getUsername());
        }
    }

    @Test
    public void testInsert() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            OrderMapper mapper = session.getMapper(OrderMapper.class);
            Order newOrder = new Order(2L, "測試商品", new BigDecimal("99.99"));
            int rows = mapper.insert(newOrder);
            session.commit();

            assertEquals(1, rows);
            assertNotNull(newOrder.getId());
        }
    }

    @Test
    public void testUpdateStatus() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            OrderMapper mapper = session.getMapper(OrderMapper.class);
            int rows = mapper.updateStatus(1L, "SHIPPED");
            session.commit();

            assertEquals(1, rows);
            Order updated = mapper.selectById(1L);
            assertEquals("SHIPPED", updated.getStatus());
        }
    }

    @Test
    public void testDeleteById() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            OrderMapper mapper = session.getMapper(OrderMapper.class);
            int rows = mapper.deleteById(1L);
            session.commit();

            assertEquals(1, rows);
            assertNull(mapper.selectById(1L));
        }
    }

    @Test
    public void testSelectAll() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            OrderMapper mapper = session.getMapper(OrderMapper.class);
            List<Order> orders = mapper.selectAll();
            assertEquals(4, orders.size());
        }
    }
}
