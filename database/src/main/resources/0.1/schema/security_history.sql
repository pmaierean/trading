create table security_history
(
    ID uuid DEFAULT gen_random_uuid () primary key,
    SECURITY_MASTER_ID uuid not null REFERENCES security_master(ID),
    TIMESTAMP timestamp not null,
    VOLUME NUMERIC not null,
    OPEN NUMERIC not null,
    CLOSE NUMERIC not null,
    ADJ_CLOSE NUMERIC not null,
    DIVIDENDS NUMERIC not null,
    HIGH NUMERIC not null,
    LOW NUMERIC not null,
    STOCK_SPLIT NUMERIC not null,
    STATUS         NUMERIC not null,
    CREATION_DATE timestamp,
    CREATED_BY CHAR(36),
    LAST_UPDATE_DATE timestamp,
    LAST_UPDATE_BY CHAR(36)
)
    TABLESPACE pg_default;
alter table security_history owner to ${USER_NAME};