CREATE TABLE IF NOT EXISTS problem
  (
     problem_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
     title       VARCHAR(100) NOT NULL,
     description VARCHAR(1000) NOT NULL
  );

CREATE TABLE IF NOT EXISTS question
  (
     question_id BIGINT AUTO_INCREMENT PRIMARY KEY,
     statement   VARCHAR(500) NOT NULL,
     problem_id  BIGINT NOT NULL,
     FOREIGN KEY (problem_id) REFERENCES problem(problem_id)
  );

CREATE TABLE IF NOT EXISTS choice
  (
     choice_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
     content      VARCHAR(100) NOT NULL,
     correct_flag BOOLEAN NOT NULL,
     question_id  BIGINT NOT NULL,
     FOREIGN KEY (question_id) REFERENCES question(question_id)
  );
