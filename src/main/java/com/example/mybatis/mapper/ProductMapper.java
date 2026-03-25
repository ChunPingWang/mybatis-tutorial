package com.example.mybatis.mapper;

import com.example.mybatis.model.Product;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品 Mapper 介面
 *
 * 全部使用 XML 方式配置 SQL，示範進階功能:
 * - choose / when / otherwise (條件分支)
 * - trim (自訂前綴後綴裁剪)
 * - bind (OGNL 變數綁定)
 * - foreach 批次插入
 * - 巢狀查詢 (Nested Select / 延遲載入)
 * - selectKey (主鍵生成策略)
 * - resultMap 的 discriminator (鑑別器)
 * - 多參數 Map 傳遞
 */
public interface ProductMapper {

    /**
     * 根據 ID 查詢商品 (基本查詢)
     */
    Product selectById(Long id);

    /**
     * 查詢所有商品
     */
    List<Product> selectAll();

    /**
     * 使用 choose/when/otherwise 排序查詢
     * orderBy: "price", "stock", "name"，其他情況預設按 id 排序
     */
    List<Product> selectWithDynamicSort(@Param("orderBy") String orderBy);

    /**
     * 使用 trim 構建自訂條件查詢
     */
    List<Product> selectByTrimCondition(@Param("name") String name,
                                         @Param("status") String status,
                                         @Param("categoryId") Long categoryId);

    /**
     * 使用 bind 進行模糊搜尋 (跨資料庫相容)
     */
    List<Product> searchByName(@Param("keyword") String keyword);

    /**
     * 使用 foreach 批次插入商品
     */
    int batchInsert(@Param("products") List<Product> products);

    /**
     * 帶巢狀查詢的商品查詢 (延遲載入分類資訊)
     */
    Product selectWithCategory(Long id);

    /**
     * 帶巢狀查詢的商品列表 (延遲載入分類資訊)
     */
    List<Product> selectAllWithCategory();

    /**
     * 使用 selectKey 插入商品 (示範 selectKey 用法)
     */
    int insertWithSelectKey(Product product);

    /**
     * 根據價格區間和狀態查詢 (使用 choose 判斷價格區間)
     * priceRange: "cheap" (<50), "medium" (50~100), "expensive" (>100)
     */
    List<Product> selectByPriceRange(@Param("priceRange") String priceRange,
                                      @Param("status") String status);

    /**
     * 使用 Map 傳遞多個參數進行條件查詢
     */
    List<Product> selectByMap(Map<String, Object> params);

    /**
     * 使用 trim 構建動態更新 (取代 set 標籤)
     */
    int updateByTrim(Product product);

    /**
     * 使用 foreach 批次刪除
     */
    int batchDeleteByIds(@Param("ids") List<Long> ids);

    /**
     * 使用 choose 統計不同狀態的商品數量
     * countType: "all", "on_sale", "out_of_stock"
     */
    int countByType(@Param("countType") String countType);

    /**
     * 根據分類 ID 查詢商品 (用於巢狀查詢的子查詢)
     */
    List<Product> selectByCategoryId(Long categoryId);
}
