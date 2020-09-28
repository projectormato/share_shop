CREATE TABLE IF NOT EXISTS problem
  (
     problem_id  SERIAL PRIMARY KEY NOT NULL,
     title       VARCHAR(100) NOT NULL,
     description VARCHAR(1000) NOT NULL
  );

CREATE TABLE IF NOT EXISTS question
  (
     question_id SERIAL PRIMARY KEY NOT NULL,
     statement   VARCHAR(500) NOT NULL,
     problem_id  INTEGER NOT NULL,
     FOREIGN KEY (problem_id) REFERENCES problem(problem_id)
  );

CREATE TABLE IF NOT EXISTS choice
  (
     choice_id    SERIAL PRIMARY KEY NOT NULL,
     content      VARCHAR(100) NOT NULL,
     correct_flag BOOLEAN NOT NULL,
     question_id  INTEGER NOT NULL,
     FOREIGN KEY (question_id) REFERENCES question(question_id)
  );