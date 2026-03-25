-- 初始化使用者資料
INSERT INTO users (username, email, age) VALUES ('alice', 'alice@example.com', 28);
INSERT INTO users (username, email, age) VALUES ('bob', 'bob@example.com', 35);
INSERT INTO users (username, email, age) VALUES ('charlie', 'charlie@example.com', 22);

-- 初始化訂單資料
INSERT INTO orders (user_id, product, amount, status) VALUES (1, 'MyBatis 實戰指南', 59.90, 'COMPLETED');
INSERT INTO orders (user_id, product, amount, status) VALUES (1, 'Java 核心技術', 89.00, 'PENDING');
INSERT INTO orders (user_id, product, amount, status) VALUES (2, 'Spring Boot 入門', 45.50, 'COMPLETED');
INSERT INTO orders (user_id, product, amount, status) VALUES (3, 'Docker 實踐', 72.00, 'SHIPPED');
