-- $ {"from": [-1], "to": [0], "dialect":["MySQL", "MariaDB", "Postgres"]}

START TRANSACTION;

DROP TABLE IF EXISTS dbm_versions;

CREATE TABLE IF NOT EXISTS dbm_versions (
  plugin_name VARCHAR(128) NOT NULL,
  version     INTEGER      NOT NULL,
  updated     TIMESTAMP    NOT NULL DEFAULT NOW(),
  PRIMARY KEY (plugin_name)
);

COMMIT;