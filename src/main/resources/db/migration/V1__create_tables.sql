create table IF NOT EXISTS event_entity (id bigint generated by default as identity, type varchar(255), url varchar(255), content TEXT, lat DOUBLE PRECISION, long DOUBLE PRECISION, datetime bigint, published bigint, event_type varchar(255), author_id bigint, primary key (id));
create table IF NOT EXISTS event_entity_like_owner_ids (event_entity_id bigint not null, like_owner_ids bigint);
create table IF NOT EXISTS event_entity_participants_ids (event_entity_id bigint not null, participants_ids bigint);
create table IF NOT EXISTS event_entity_speaker_ids (event_entity_id bigint not null, speaker_ids bigint);
create table IF NOT EXISTS job_entity (id bigint generated by default as identity, finish bigint, link varchar(255), name TEXT, position TEXT, start bigint not null, user_id bigint, primary key (id));
create table IF NOT EXISTS post_entity (id bigint generated by default as identity, type varchar(255), url varchar(255), content TEXT, lat DOUBLE PRECISION, long DOUBLE PRECISION, link varchar(255), published bigint not null, author_id bigint, primary key (id));
create table IF NOT EXISTS post_entity_like_owner_ids (post_entity_id bigint not null, like_owner_ids bigint);
create table IF NOT EXISTS post_entity_mention_ids (post_entity_id bigint not null, mention_ids bigint);
create table IF NOT EXISTS push_token_entity (id bigint generated by default as identity, token varchar(255) not null, user_id bigint not null, primary key (id));
create table IF NOT EXISTS token_entity (token varchar(255) not null, user_id bigint, primary key (token));
create table IF NOT EXISTS user_entity (id bigint generated by default as identity, avatar varchar(255), login varchar(255), name varchar(255), pass varchar(255), primary key (id));
alter table push_token_entity add constraint UK_q4qujtnuepogowl64iohtird5 unique (token);
alter table user_entity add constraint UK_1dnnlrof07tq8gn7s3om9bhao unique (login);
alter table event_entity add constraint FK7o49vmyxtl8a0thwq60ia8k2g foreign key (author_id) references user_entity;
alter table event_entity_like_owner_ids add constraint FKt17lw2mbnl7xcxii7deb48ici foreign key (event_entity_id) references event_entity;
alter table event_entity_participants_ids add constraint FK6hudmnn4jn9hwusr7ou2m4cx2 foreign key (event_entity_id) references event_entity;
alter table event_entity_speaker_ids add constraint FKh2d22da72gvau8dtbnrkmq3u9 foreign key (event_entity_id) references event_entity;
alter table job_entity add constraint FK4pnej78uru0mgylijw67cgfto foreign key (user_id) references user_entity;
alter table post_entity add constraint FKmqyrpi535ad5my31g1sn0bfrg foreign key (author_id) references user_entity;
alter table post_entity_like_owner_ids add constraint FK4iaw1unkc6j4gi282cfrf3my8 foreign key (post_entity_id) references post_entity;
alter table post_entity_mention_ids add constraint FK3k03udqblebqv06mf8kglr2ee foreign key (post_entity_id) references post_entity;
alter table token_entity add constraint FKchycpasyr16kt66k09e6ompve foreign key (user_id) references user_entity;