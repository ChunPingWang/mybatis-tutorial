-- 初始化使用者資料
INSERT INTO users (username, email, age) VALUES ('alice', 'alice@example.com', 28);
INSERT INTO users (username, email, age) VALUES ('bob', 'bob@example.com', 35);
INSERT INTO users (username, email, age) VALUES ('charlie', 'charlie@example.com', 22);

-- 初始化訂單資料
INSERT INTO orders (user_id, product, amount, status) VALUES (1, 'MyBatis 實戰指南', 59.90, 'COMPLETED');
INSERT INTO orders (user_id, product, amount, status) VALUES (1, 'Java 核心技術', 89.00, 'PENDING');
INSERT INTO orders (user_id, product, amount, status) VALUES (2, 'Spring Boot 入門', 45.50, 'COMPLETED');
INSERT INTO orders (user_id, product, amount, status) VALUES (3, 'Docker 實踐', 72.00, 'SHIPPED');

-- 初始化商品分類資料
INSERT INTO categories (name) VALUES ('程式設計');
INSERT INTO categories (name) VALUES ('資料庫');
INSERT INTO categories (name) VALUES ('DevOps');

-- 初始化商品資料
INSERT INTO products (name, category_id, price, stock, status, description)
VALUES ('MyBatis 實戰指南', 1, 59.90, 100, 'ON_SALE', '全面掌握 MyBatis 框架的最佳實踐');
INSERT INTO products (name, category_id, price, stock, status, description)
VALUES ('Java 核心技術', 1, 89.00, 50, 'ON_SALE', '深入理解 Java 語言核心機制');
INSERT INTO products (name, category_id, price, stock, status, description)
VALUES ('MySQL 高效能', 2, 75.00, 0, 'OUT_OF_STOCK', '資料庫效能調優完全手冊');
INSERT INTO products (name, category_id, price, stock, status, description)
VALUES ('Redis 設計與實現', 2, 68.00, 200, 'ON_SALE', '深入理解 Redis 內部機制');
INSERT INTO products (name, category_id, price, stock, status, description)
VALUES ('Docker 實踐', 3, 72.00, 30, 'ON_SALE', '容器化技術入門到進階');
INSERT INTO products (name, category_id, price, stock, status, description)
VALUES ('Kubernetes 權威指南', 3, 98.00, 5, 'ON_SALE', 'K8s 叢集管理完全指南');
INSERT INTO products (name, category_id, price, stock, status, description)
VALUES ('已下架商品', 1, 10.00, 0, 'DISCONTINUED', '此商品已停售');
