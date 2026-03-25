package com.example.mybatis;

import com.example.mybatis.mapper.OrderMapper;
import com.example.mybatis.mapper.UserMapper;
import com.example.mybatis.model.Order;
import com.example.mybatis.model.User;
import com.example.mybatis.util.MyBatisUtil;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * MyBatis 教程主程式
 *
 * 示範以下功能:
 * 1. 基本 CRUD 操作
 * 2. 動態 SQL 查詢
 * 3. 一對多 / 多對一關聯查詢
 * 4. 批次查詢 (foreach)
 * 5. 註解方式 vs XML 方式
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        // 初始化資料庫
        initDatabase();

        // 1. 基本 CRUD 操作示範
        demoCrud();

        // 2. 動態 SQL 查詢示範
        demoDynamicSql();

        // 3. 關聯查詢示範
        demoAssociationQuery();

        // 4. 註解方式操作示範
        demoAnnotationMapper();

        logger.info("=== 所有示範完成 ===");
    }

    /**
     * 初始化資料庫: 執行建表語句和初始資料
     */
    private static void initDatabase() {
        logger.info("=== 初始化資料庫 ===");
        try (SqlSession session = MyBatisUtil.openSession(true)) {
            ScriptRunner runner = new ScriptRunner(session.getConnection());
            runner.setLogWriter(null);

            InputStream schemaStream = App.class.getClassLoader().getResourceAsStream("db/schema.sql");
            InputStream dataStream = App.class.getClassLoader().getResourceAsStream("db/data.sql");

            if (schemaStream != null) {
                runner.runScript(new InputStreamReader(schemaStream, StandardCharsets.UTF_8));
            }
            if (dataStream != null) {
                runner.runScript(new InputStreamReader(dataStream, StandardCharsets.UTF_8));
            }
            logger.info("資料庫初始化完成");
        }
    }

    /**
     * 示範 1: 基本 CRUD 操作
     */
    private static void demoCrud() {
        logger.info("=== 示範 1: 基本 CRUD 操作 ===");

        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper userMapper = session.getMapper(UserMapper.class);

            // Create - 新增使用者
            User newUser = new User("david", "david@example.com", 30);
            userMapper.insert(newUser);
            session.commit();
            logger.info("[新增] 使用者已建立, ID = {}", newUser.getId());

            // Read - 查詢使用者
            User user = userMapper.selectById(newUser.getId());
            logger.info("[查詢] {}", user);

            // Update - 更新使用者 (只更新 email)
            User updateUser = new User();
            updateUser.setId(newUser.getId());
            updateUser.setEmail("david.new@example.com");
            userMapper.updateSelective(updateUser);
            session.commit();
            logger.info("[更新] email 已更新");

            User updated = userMapper.selectById(newUser.getId());
            logger.info("[查詢] 更新後: {}", updated);

            // Delete - 刪除使用者
            userMapper.deleteById(newUser.getId());
            session.commit();
            logger.info("[刪除] 使用者 ID={} 已刪除", newUser.getId());

            // Count
            int count = userMapper.count();
            logger.info("[統計] 目前共有 {} 位使用者", count);
        }
    }

    /**
     * 示範 2: 動態 SQL 查詢
     */
    private static void demoDynamicSql() {
        logger.info("=== 示範 2: 動態 SQL 查詢 ===");

        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper userMapper = session.getMapper(UserMapper.class);

            // 條件查詢: 搜尋年齡在 25~40 之間的使用者
            List<User> users = userMapper.selectByCondition(null, null, 25, 40);
            logger.info("[條件查詢] 年齡 25~40 的使用者:");
            for (User u : users) {
                logger.info("  - {}", u);
            }

            // 條件查詢: 搜尋 username 包含 'li' 的使用者
            List<User> filteredUsers = userMapper.selectByCondition("li", null, null, null);
            logger.info("[條件查詢] username 包含 'li' 的使用者:");
            for (User u : filteredUsers) {
                logger.info("  - {}", u);
            }

            // 批次查詢: 根據 ID 列表查詢
            List<User> batchUsers = userMapper.selectByIds(Arrays.asList(1L, 3L));
            logger.info("[批次查詢] ID 為 1, 3 的使用者:");
            for (User u : batchUsers) {
                logger.info("  - {}", u);
            }
        }
    }

    /**
     * 示範 3: 關聯查詢 (一對多 / 多對一)
     */
    private static void demoAssociationQuery() {
        logger.info("=== 示範 3: 關聯查詢 ===");

        try (SqlSession session = MyBatisUtil.openSession()) {
            UserMapper userMapper = session.getMapper(UserMapper.class);
            OrderMapper orderMapper = session.getMapper(OrderMapper.class);

            // 一對多: 查詢使用者及其所有訂單
            User userWithOrders = userMapper.selectWithOrders(1L);
            logger.info("[一對多] 使用者: {}", userWithOrders.getUsername());
            if (userWithOrders.getOrders() != null) {
                for (Order order : userWithOrders.getOrders()) {
                    logger.info("  訂單: {} - ${}", order.getProduct(), order.getAmount());
                }
            }

            // 多對一: 查詢訂單及其所屬使用者
            Order orderWithUser = orderMapper.selectWithUser(1L);
            logger.info("[多對一] 訂單: {} 所屬使用者: {}",
                    orderWithUser.getProduct(),
                    orderWithUser.getUser().getUsername());
        }
    }

    /**
     * 示範 4: 使用註解方式操作 (OrderMapper)
     */
    private static void demoAnnotationMapper() {
        logger.info("=== 示範 4: 註解方式操作 ===");

        try (SqlSession session = MyBatisUtil.openSession()) {
            OrderMapper orderMapper = session.getMapper(OrderMapper.class);

            // 新增訂單
            Order newOrder = new Order(2L, "MyBatis 從入門到精通", new BigDecimal("68.00"));
            orderMapper.insert(newOrder);
            session.commit();
            logger.info("[新增] 訂單已建立, ID = {}", newOrder.getId());

            // 查詢使用者的所有訂單
            List<Order> bobOrders = orderMapper.selectByUserId(2L);
            logger.info("[查詢] Bob 的訂單:");
            for (Order o : bobOrders) {
                logger.info("  - {} (${}) [{}]", o.getProduct(), o.getAmount(), o.getStatus());
            }

            // 更新訂單狀態
            orderMapper.updateStatus(newOrder.getId(), "COMPLETED");
            session.commit();
            logger.info("[更新] 訂單 ID={} 狀態已更新為 COMPLETED", newOrder.getId());

            // 刪除訂單
            orderMapper.deleteById(newOrder.getId());
            session.commit();
            logger.info("[刪除] 訂單 ID={} 已刪除", newOrder.getId());
        }
    }
}
