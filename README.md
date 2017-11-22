# DBM

## What is it

A simplified, smart database abstraction layer for database management beyond what spigot normally provides.

## Update scripts & SQL Header Format

Scripts that want to use the update resolution mechanism need to implement the `List<SQLScript> getScripts()` in `DatabaseInterface` and provide a list of SQL scripts. The header has to be in the following format:

```
-- $ {"from": [-1], "to": [0], "dialect":["MySQL", "PostgreSQL", "MariaDB"]}
```
or
```
-- $ {
-- $   "from":[
-- $     -1
-- $   ],
-- $   "to":[
-- $     0
-- $   ],
-- $   "dialect":[
-- $     "MySQL",
-- $     "PostgreSQL",
-- $     "MariaDB"
-- $   ]
-- $ }
```

`from` is a list of integers specifiying the version this script upgrades from (use -1 for no previous tables installed, aka blank database for this plugin).
`to` is a list of integer specifying the versions this script upgrades to. 
`dialect` specifies a list of strings of dialects this script is valid for.

## Usage

Use `DBManagerPlugin.register(plugin)` to  acquire a database interface. Don't block connections for too long.

### Allowed database names for SQL scripts

* SQL99
* DEFAULT
* CUBRID
* DERBY
* FIREBIRD
* FIREBIRD_2_5
* FIREBIRD_3_0
* H2
* HSQLDB
* MARIADB
* MYSQL
* MYSQL_5_7
* MYSQL_8_0
* POSTGRES
* POSTGRES_9_3
* POSTGRES_9_4
* POSTGRES_9_5
* SQLITE
