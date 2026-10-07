create table security_master
(
    ID uuid DEFAULT gen_random_uuid () primary key,
    NAME           VARCHAR(128) not null,
    TICKER         VARCHAR(10) not null,
    EXCHANGE       VARCHAR(10) not null,
    SECURITY_TYPE           VARCHAR(36) not null,
    SECURITY_SUB_TYPE       VARCHAR(36),
    REGION         VARCHAR(36) not null ,
    INDUSTRY       VARCHAR(256) not null,
    SUB_INDUSTRY   VARCHAR(256),
    MARKET_CAP     NUMERIC not null,
    SHARES_NUMBER  NUMERIC not null,
    STATUS         NUMERIC not null,
    CREATION_DATE timestamp,
    CREATED_BY CHAR(36),
    LAST_UPDATE_DATE timestamp,
    LAST_UPDATE_BY CHAR(36)
)
TABLESPACE pg_default;
alter table security_master owner to ${USER_NAME};