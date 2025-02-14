CREATE TABLE IF NOT EXISTS tb_version(
    id SERIAL PRIMARY KEY,
    version VARCHAR(20),
    url varchar(300)
);
