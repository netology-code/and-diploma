alter table event_entity add column link text;
update flyway_schema_history set checksum = '-1934991199' where installed_rank = '1';