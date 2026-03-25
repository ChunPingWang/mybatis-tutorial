package com.example.mybatis.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品實體類別
 */
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Long categoryId;
    private BigDecimal price;
    private Integer stock;
    private String status;
    private String description;
    private Date createdAt;
    private Date updatedAt;

    /** 多對一: 商品所屬分類 */
    private Category category;

    public Product() {
    }

    public Product(String name, Long categoryId, BigDecimal price, Integer stock) {
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.stock = stock;
        this.status = "ON_SALE";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryId=" + categoryId +
                ", price=" + price +
                ", stock=" + stock +
                ", status='" + status + '\'' +
                '}';
    }
}
