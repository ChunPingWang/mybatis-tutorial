package com.example.mybatis.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * MyBatis 工具類別
 *
 * 負責初始化 SqlSessionFactory 並提供 SqlSession 的取得方式。
 * SqlSessionFactory 為重量級物件，整個應用程式只需建立一次。
 */
public class MyBatisUtil {

    private static final SqlSessionFactory sqlSessionFactory;

    static {
        String resource = "mybatis-config.xml";
        try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to initialize SqlSessionFactory: " + e.getMessage());
        }
    }

    private MyBatisUtil() {
        // 防止實例化
    }

    /**
     * 取得 SqlSessionFactory
     */
    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    /**
     * 取得 SqlSession (手動提交模式)
     */
    public static SqlSession openSession() {
        return sqlSessionFactory.openSession();
    }

    /**
     * 取得 SqlSession (可指定是否自動提交)
     */
    public static SqlSession openSession(boolean autoCommit) {
        return sqlSessionFactory.openSession(autoCommit);
    }
}
