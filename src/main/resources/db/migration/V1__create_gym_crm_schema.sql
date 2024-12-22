CREATE TABLE user(
    id         VARCHAR(255) PRIMARY KEY NOT NULL,
    first_name VARCHAR(50)              NOT NULL,
    last_name  VARCHAR(50)              NOT NULL,
    username   VARCHAR(50)              NOT NULL UNIQUE,
    password   VARCHAR(255)             NOT NULL,
    is_active  BOOLEAN                  NOT NULL,
    user_type  VARCHAR(255)             NOT NULL
);

CREATE INDEX idx_username ON user (username);

CREATE TABLE trainee(
    id            VARCHAR(255) PRIMARY KEY NOT NULL,
    date_of_birth DATE,
    address       VARCHAR(255),
    user_id       VARCHAR(255)             NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE trainer(
    id             VARCHAR(255) PRIMARY KEY NOT NULL,
    specialization VARCHAR(100)             NOT NULL,
    user_id        VARCHAR(255)             NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE training_type(
    id                 VARCHAR(255) PRIMARY KEY NOT NULL,
    training_type_name VARCHAR(100)             NOT NULL UNIQUE
);

CREATE TABLE training(
    id                VARCHAR(255) PRIMARY KEY NOT NULL,
    trainee_id        VARCHAR(255)             NOT NULL,
    trainer_id        VARCHAR(255)             NOT NULL,
    training_name     VARCHAR(100)             NOT NULL,
    training_type_id  VARCHAR(255)             NOT NULL,
    training_date     DATE                     NOT NULL,
    training_duration NUMERIC                  NOT NULL,
    FOREIGN KEY (trainee_id) REFERENCES trainee (id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES trainer (id),
    FOREIGN KEY (training_type_id) REFERENCES training_type (id)
);

CREATE TABLE trainee_trainer(
    trainee_id VARCHAR(255) NOT NULL,
    trainer_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (trainee_id, trainer_id),
    FOREIGN KEY (trainee_id) REFERENCES trainee (id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES trainer (id) ON DELETE CASCADE
);
