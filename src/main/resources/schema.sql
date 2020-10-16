CREATE TABLE IF NOT EXISTS shop
  (
     shop_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
     user_id  VARCHAR(100) NOT NULL,
     url      VARCHAR(200) NOT NULL,
     `name`   VARCHAR(100) NOT NULL,
     address  VARCHAR(1000) NOT NULL,
     hours    VARCHAR(1000) NOT NULL
  );

CREATE TABLE IF NOT EXISTS share_shared
  (
     share_shared_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
     share_id         VARCHAR(100) NOT NULL,
     shared_id        VARCHAR(100) NOT NULL
  );

CREATE TABLE IF NOT EXISTS `user`
  (
     id       BIGINT AUTO_INCREMENT PRIMARY KEY,
     user_id  VARCHAR(100) NOT NULL,
     email    VARCHAR(100) NOT NULL
  );
