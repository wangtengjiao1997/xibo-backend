#!/bin/bash
set -e
# 创建用户
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE USER xibo WITH PASSWORD '123456';
EOSQL
# 创建数据库
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE xibo
    WITH
    OWNER = xibo
    ENCODING = 'UTF8'
    LOCALE_PROVIDER = 'libc'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
EOSQL
