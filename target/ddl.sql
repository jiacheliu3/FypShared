
    alter table tag_user 
        drop 
        foreign key FK_odvn6qyd3r5tltxv3ykuaqux3;

    alter table tag_user 
        drop 
        foreign key FK_rnua5txy1k096swnv5wgqs102;

    alter table user_weibo 
        drop 
        foreign key FK_phkd22jelcs3dmy9tnn9cwqma;

    alter table user_weibo 
        drop 
        foreign key FK_sthsaa923hdsqmauf7te3igxe;

    alter table weibo 
        drop 
        foreign key FK_p55u5x0sjcbsvq4tv3v7bw3pj;

    drop table if exists background;

    drop table if exists comment;

    drop table if exists environment;

    drop table if exists framework;

    drop table if exists job;

    drop table if exists tag;

    drop table if exists tag_user;

    drop table if exists task;

    drop table if exists user;

    drop table if exists user_commented;

    drop table if exists user_commenting;

    drop table if exists user_forwarded;

    drop table if exists user_forwarding;

    drop table if exists user_liked;

    drop table if exists user_liking;

    drop table if exists user_mentioned;

    drop table if exists user_mentioning;

    drop table if exists user_weibo;

    drop table if exists weibo;

    drop table if exists weibo_friend_map;

    create table background (
        id bigint not null auto_increment,
        version bigint not null,
        primary key (id)
    );

    create table comment (
        id bigint not null auto_increment,
        version bigint not null,
        content varchar(255),
        owner_name varchar(255) not null,
        primary key (id)
    );

    create table environment (
        id bigint not null auto_increment,
        version bigint not null,
        primary key (id)
    );

    create table framework (
        id bigint not null auto_increment,
        version bigint not null,
        primary key (id)
    );

    create table job (
        id bigint not null auto_increment,
        version bigint not null,
        cluster_complete bit not null,
        cluster_started bit not null,
        deep_crawl_complete bit not null,
        deep_crawl_started bit not null,
        interaction_complete bit not null,
        interaction_started bit not null,
        keywords_complete bit not null,
        keywords_started bit not null,
        nothing_to_show bit not null,
        stat_complete bit not null,
        stat_started bit not null,
        timeline_complete bit not null,
        timeline_started bit not null,
        user_crawl_complete bit not null,
        user_crawl_started bit not null,
        user_name varchar(255) not null,
        primary key (id)
    );

    create table tag (
        id bigint not null auto_increment,
        version bigint not null,
        name varchar(255) not null,
        primary key (id)
    );

    create table tag_user (
        tag_users_id bigint,
        user_id bigint
    );

    create table task (
        id bigint not null auto_increment,
        version bigint not null,
        task_size integer not null,
        primary key (id)
    );

    create table user (
        id bigint not null auto_increment,
        version bigint not null,
        email varchar(255),
        face_url varchar(255),
        info_element blob,
        keywords VARBINARY(100000),
        name varchar(255),
        phone varchar(255),
        tags tinyblob,
        terminals tinyblob,
        url varchar(255),
        weibo_id varchar(255) not null,
        weibo_name varchar(255) not null,
        primary key (id)
    );

    create table user_commented (
        commented blob,
        commented_idx varchar(255),
        commented_elt varchar(255) not null
    );

    create table user_commenting (
        commenting blob,
        commenting_idx varchar(255),
        commenting_elt varchar(255) not null
    );

    create table user_forwarded (
        forwarded blob,
        forwarded_idx varchar(255),
        forwarded_elt varchar(255) not null
    );

    create table user_forwarding (
        forwarding blob,
        forwarding_idx varchar(255),
        forwarding_elt varchar(255) not null
    );

    create table user_liked (
        liked blob,
        liked_idx varchar(255),
        liked_elt varchar(255) not null
    );

    create table user_liking (
        liking blob,
        liking_idx varchar(255),
        liking_elt varchar(255) not null
    );

    create table user_mentioned (
        mentioned blob,
        mentioned_idx varchar(255),
        mentioned_elt varchar(255) not null
    );

    create table user_mentioning (
        mentioning blob,
        mentioning_idx varchar(255),
        mentioning_elt varchar(255) not null
    );

    create table user_weibo (
        user_weibos_id bigint,
        weibo_id bigint
    );

    create table weibo (
        id bigint not null auto_increment,
        version bigint not null,
        comment_count integer not null,
        comment_element blob,
        content text,
        created_time datetime,
        forward_count integer not null,
        full_element blob,
        image_url varchar(255),
        is_forwarded bit not null,
        like_element blob,
        links tinyblob,
        org_content text,
        org_owner_name varchar(255),
        owner_name varchar(255) not null,
        repost_element blob,
        sub_id varchar(255),
        tag_id bigint,
        terminal varchar(255),
        url varchar(255),
        weibo_id varchar(255) not null,
        primary key (id)
    );

    create table weibo_friend_map (
        friend_map blob,
        friend_map_idx varchar(255),
        friend_map_elt varchar(255) not null
    );

    alter table tag 
        add constraint UK_1wdpsed5kna2y38hnbgrnhi5b  unique (name);

    alter table user 
        add constraint UK_pr6dqghmf9qso0ljs1tyenw8f  unique (weibo_id);

    alter table user 
        add constraint UK_5sbnkiupvgjk6kuw2c1l21hlu  unique (weibo_name);

    alter table weibo 
        add constraint UK_k8iauas71pv65f4lvo5030ypj  unique (weibo_id);

    alter table tag_user 
        add constraint FK_odvn6qyd3r5tltxv3ykuaqux3 
        foreign key (user_id) 
        references user (id);

    alter table tag_user 
        add constraint FK_rnua5txy1k096swnv5wgqs102 
        foreign key (tag_users_id) 
        references tag (id);

    alter table user_weibo 
        add constraint FK_phkd22jelcs3dmy9tnn9cwqma 
        foreign key (weibo_id) 
        references weibo (id);

    alter table user_weibo 
        add constraint FK_sthsaa923hdsqmauf7te3igxe 
        foreign key (user_weibos_id) 
        references user (id);

    alter table weibo 
        add constraint FK_p55u5x0sjcbsvq4tv3v7bw3pj 
        foreign key (tag_id) 
        references tag (id);
