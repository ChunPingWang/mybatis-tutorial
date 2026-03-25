package com.example.mybatis.mapper;

import com.example.mybatis.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 使用者 Mapper 介面
 *
 * 示範功能:
 * - 基本 CRUD 操作
 * - 動態 SQL (if, where, set, foreach)
 * - 一對多關聯查詢
 * - 分頁查詢
 */
public interface UserMapper {

    /**
     * 根據 ID 查詢使用者
     */
    User selectById(Long id);

    /**
     * 查詢所有使用者
     */
    List<User> selectAll();

    /**
     * 根據條件動態查詢使用者 (示範 <where> + <if>)
     */
    List<User> selectByCondition(@Param("username") String username,
                                  @Param("email") String email,
                                  @Param("minAge") Integer minAge,
                                  @Param("maxAge") Integer maxAge);

    /**
     * 查詢使用者並載入其訂單列表 (一對多)
     */
    User selectWithOrders(Long id);

    /**
     * 根據 ID 列表批次查詢 (示範 <foreach>)
     */
    List<User> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 新增使用者 (自動回填 ID)
     */
    int insert(User user);

    /**
     * 動態更新使用者 (示範 <set> + <if>，只更新非 null 欄位)
     */
    int updateSelective(User user);

    /**
     * 根據 ID 刪除使用者
     */
    int deleteById(Long id);

    /**
     * 統計使用者數量
     */
    int count();
}
