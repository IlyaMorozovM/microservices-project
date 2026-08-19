CREATE TABLE IF NOT EXISTS songs (
                                     id BIGINT NOT NULL,
                                     album VARCHAR(100) NOT NULL,
    artist VARCHAR(100) NOT NULL,
    duration VARCHAR(5) NOT NULL,
    name VARCHAR(100) NOT NULL,
    year VARCHAR(4) NOT NULL,
    PRIMARY KEY (id)
    );