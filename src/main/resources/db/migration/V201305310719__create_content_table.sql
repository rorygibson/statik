create table statik_content (
    domain varchar(255) not null,
    path varchar(255) not null,
    selector varchar(255) not null,
    content text,
    img varchar(255),
    is_copy boolean,
    is_live boolean,
    language varchar(2) default 'en'
)