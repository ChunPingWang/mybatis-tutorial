package com.example.mybatis.mapper;

import com.example.mybatis.model.Order;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 訂單 Mapper 介面
 *
 * 示範功能:
 * - 使用註解方式定義 SQL (對比 UserMapper 的 XML 方式)
 * - 多對一關聯查詢 (XML 方式定義於 OrderMapper.xml)
 */
public interface OrderMapper {

    /**
     * 根據 ID 查詢訂單 (註解方式)
     */
    @Select("SELECT * FROM orders WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "product", column = "product"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at")
    })
    Order selectById(Long id);

    /**
     * 根據使用者 ID 查詢訂單列表 (註解方式)
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<Order> selectByUserId(Long userId);

    /**
     * 查詢訂單並載入使用者資訊 (多對一, XML 方式)
     */
    Order selectWithUser(Long id);

    /**
     * 新增訂單 (註解方式)
     */
    @Insert("INSERT INTO orders (user_id, product, amount, status) VALUES (#{userId}, #{product}, #{amount}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    /**
     * 更新訂單狀態 (註解方式)
     */
    @Update("UPDATE orders SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 刪除訂單 (註解方式)
     */
    @Delete("DELETE FROM orders WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 查詢所有訂單
     */
    @Select("SELECT * FROM orders")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<Order> selectAll();
}
