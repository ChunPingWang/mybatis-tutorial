package com.example.mybatis.mapper;

import com.example.mybatis.model.Order;
import com.example.mybatis.model.User;
import com.example.mybatis.util.MyBatisUtil;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * UserMapper 單元測試
 */
public class UserMapperTest {

    @BeforeClass
    public static void initSchema() {
        try (SqlSession session = MyBatisUtil.openSession(true)) {
            ScriptRunner runner = new ScriptRunner(session.getConnection());
            runner.setLogWriter(null);
            InputStream schemaStream = UserMapperTest.class.getClassLoader().getResourceAsStream("db/schema.sql");
            if (schemaStream != null) {
                runner.runScript(new InputStreamReader(schemaStream, StandardCharsets.UTF_8));
            }
        }
    }

    @Before
    public void initData() {
        try (SqlSession session = MyBatisUtil.openSession(true)) {
            // 清除並重新載入測試資料
            session.getConnection().createStatement().execute("DELETE FROM orders");
            session.getConnection().createStatement().execute("DELETE FROM users");
            session.getConnection().createStatement().execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
            session.getConnection().createStatement().execute("ALTER TABLE orders ALTER COLUMN id RESTART WITH 1");

            ScriptRunner runner = new ScriptRunner(session.getConnection());
            runner.setLogWriter(null);
            InputStream dataStream = UserMapperTest.class.getClassLoader().getResourceAsStream("db/data.sql");
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
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.selectById(1L);
            assertNotNull(user);
            assertEquals("alice", user.getUsername());
            assertEquals("alice@example.com", user.getEmail());
            assertEquals(Integer.valueOf(28), user.getAge());
        }
    }

    @Test
    public void testSelectAll() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<User> users = mapper.selectAll();
            assertEquals(3, users.size());
        }
    }

    @Test
    public void testInsert() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);

            User newUser = new User("eve", "eve@example.com", 25);
            int rows = mapper.insert(newUser);
            session.commit();

            assertEquals(1, rows);
            assertNotNull(newUser.getId());

            User fetched = mapper.selectById(newUser.getId());
            assertEquals("eve", fetched.getUsername());
        }
    }

    @Test
    public void testUpdateSelective() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);

            User updateUser = new User();
            updateUser.setId(1L);
            updateUser.setEmail("alice.updated@example.com");
            // username 和 age 為 null，不應被更新
            int rows = mapper.updateSelective(updateUser);
            session.commit();

            assertEquals(1, rows);
            User updated = mapper.selectById(1L);
            assertEquals("alice.updated@example.com", updated.getEmail());
            assertEquals("alice", updated.getUsername()); // 未被覆蓋
        }
    }

    @Test
    public void testDeleteById() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);

            // 先刪除 alice 的訂單以避免外鍵約束
            session.getConnection().createStatement().execute("DELETE FROM orders WHERE user_id = 1");

            int rows = mapper.deleteById(1L);
            session.commit();

            assertEquals(1, rows);
            assertNull(mapper.selectById(1L));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSelectByCondition_ageRange() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<User> users = mapper.selectByCondition(null, null, 25, 30);
            // alice(28) 在範圍內
            assertFalse(users.isEmpty());
            for (User u : users) {
                assertTrue(u.getAge() >= 25 && u.getAge() <= 30);
            }
        }
    }

    @Test
    public void testSelectByCondition_username() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<User> users = mapper.selectByCondition("li", null, null, null);
            // alice 和 charlie 都包含 'li'
            assertEquals(2, users.size());
        }
    }

    @Test
    public void testSelectByIds() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<User> users = mapper.selectByIds(Arrays.asList(1L, 3L));
            assertEquals(2, users.size());
        }
    }

    @Test
    public void testSelectWithOrders() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.selectWithOrders(1L);
            assertNotNull(user);
            assertEquals("alice", user.getUsername());
            assertNotNull(user.getOrders());
            assertEquals(2, user.getOrders().size());

            for (Order order : user.getOrders()) {
                assertEquals(Long.valueOf(1L), order.getUserId());
            }
        }
    }

    @Test
    public void testCount() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            int count = mapper.count();
            assertEquals(3, count);
        }
    }
}
