create table statik_content (
    domain varchar(255) not null,
    path varchar(255) not null,
    selector varchar(255) not null,
    content clob,
    is_copy boolean default false,
    is_live boolean default false,
    language varchar(2) default 'en'
)