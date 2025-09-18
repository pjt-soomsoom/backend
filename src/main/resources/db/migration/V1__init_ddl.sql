CREATE TABLE shedlock (
                          name VARCHAR(64) NOT NULL,
                          lock_until TIMESTAMP NOT NULL,
                          locked_at TIMESTAMP NOT NULL,
                          locked_by VARCHAR(255) NOT NULL,
                          PRIMARY KEY (name)
) engine=InnoDB;


create table achievement_conditions (
                                        target_value integer not null,
                                        achievement_id bigint not null,
                                        id bigint not null auto_increment,
                                        type enum ('BREATHING_COUNT','BREATHING_MONTHLY_COUNT','BREATHING_MULTI_TYPE_COUNT','BREATHING_STREAK','BREATHING_TOTAL_SECONDS','CONNECTION_STREAK','DIARY_COUNT','DIARY_MONTHLY_COUNT','DIARY_STREAK','HIDDEN_CUSTOMIZE_CHARACTER','HIDDEN_EMOTION_OVERCOME','HIDDEN_STAY_HOME_SCREEN','MEDITATION_COUNT','MEDITATION_LATE_NIGHT_STREAK','MEDITATION_MONTHLY_COUNT','MEDITATION_STREAK','MEDITATION_TOTAL_SECONDS','SOUND_EFFECT_TOTAL_SECONDS'),
                                        primary key (id)
) engine=InnoDB;

create table achievements (
                              reward_points integer,
                              created_at datetime(6) not null,
                              deleted_at datetime(6),
                              id bigint not null auto_increment,
                              modified_at datetime(6),
                              reward_item_id bigint,
                              description varchar(255),
                              name varchar(255),
                              phrase varchar(255),
                              category enum ('BREATHING','DIARY','HIDDEN','MEDITATION'),
                              grade enum ('BRONZE','GOLD','SILVER','SPECIAL'),
                              primary key (id)
) engine=InnoDB;

create table activities (
                            duration_in_seconds integer not null,
                            author_id bigint not null,
                            created_at datetime(6) not null,
                            deleted_at datetime(6),
                            id bigint not null auto_increment,
                            modified_at datetime(6),
                            narrator_id bigint not null,
                            activity_type varchar(31) not null,
                            audio_file_key varchar(255),
                            audio_url varchar(255),
                            thumbnail_file_key varchar(255),
                            thumbnail_image_url varchar(255),
                            title varchar(255),
                            category enum ('BREATHING','MEDITATION','REST','SLEEP') not null,
                            primary key (id)
) engine=InnoDB;

create table activity_completion_log (
                                         activity_id bigint not null,
                                         created_at datetime(6) not null,
                                         deleted_at datetime(6),
                                         id bigint not null auto_increment,
                                         modified_at datetime(6),
                                         user_id bigint not null,
                                         activity_type enum ('BREATHING','MEDITATION','SOUND_EFFECT') not null,
                                         primary key (id)
) engine=InnoDB;

create table activity_descriptions (
                                       sequence integer not null,
                                       activity_id bigint not null,
                                       description TEXT,
                                       primary key (sequence, activity_id)
) engine=InnoDB;

create table activity_progress (
                                   progress_seconds integer not null,
                                   activity_id bigint not null,
                                   created_at datetime(6) not null,
                                   deleted_at datetime(6),
                                   id bigint not null auto_increment,
                                   modified_at datetime(6),
                                   user_id bigint not null,
                                   primary key (id)
) engine=InnoDB;

create table banners (
                         display_order integer not null,
                         is_active bit not null,
                         created_at datetime(6) not null,
                         deleted_at datetime(6),
                         id bigint not null auto_increment,
                         linked_activity_id bigint not null,
                         modified_at datetime(6),
                         button_text varchar(255) not null,
                         description TEXT,
                         image_file_key varchar(255) not null,
                         image_url varchar(255) not null,
                         primary key (id)
) engine=InnoDB;

create table breathing_activities (
                                      activity_id bigint not null,
                                      primary key (activity_id)
) engine=InnoDB;

create table cart_items (
                            cart_id bigint not null,
                            created_at datetime(6) not null,
                            deleted_at datetime(6),
                            id bigint not null auto_increment,
                            item_id bigint not null,
                            modified_at datetime(6),
                            primary key (id)
) engine=InnoDB;

create table carts (
                       created_at datetime(6) not null,
                       deleted_at datetime(6),
                       id bigint not null auto_increment,
                       modified_at datetime(6),
                       user_id bigint not null,
                       primary key (id)
) engine=InnoDB;

create table collection_items (
                                  collection_id bigint not null,
                                  item_id bigint not null,
                                  primary key (collection_id, item_id)
) engine=InnoDB;

create table collections (
                             created_at datetime(6) not null,
                             deleted_at datetime(6),
                             id bigint not null auto_increment,
                             modified_at datetime(6),
                             description TEXT,
                             image_file_key varchar(255) not null,
                             image_url varchar(255) not null,
                             lottie_file_key varchar(255),
                             lottie_url varchar(255),
                             name varchar(255) not null,
                             phrase TEXT,
                             primary key (id)
) engine=InnoDB;

create table connection_logs (
                                 created_at datetime(6) not null,
                                 deleted_at datetime(6),
                                 id bigint not null auto_increment,
                                 modified_at datetime(6),
                                 user_id bigint not null,
                                 primary key (id)
) engine=InnoDB;

create table diaries (
                         created_at datetime(6) not null,
                         deleted_at datetime(6),
                         id bigint not null auto_increment,
                         modified_at datetime(6),
                         user_id bigint not null,
                         memo varchar(255),
                         emotion enum ('ANGER','DEPRESSION','HAPPINESS','JOY','NEUTRAL','SADNESS'),
                         primary key (id)
) engine=InnoDB;

create table favorites (
                           activity_id bigint not null,
                           created_at datetime(6) not null,
                           deleted_at datetime(6),
                           id bigint not null auto_increment,
                           modified_at datetime(6),
                           user_id bigint not null,
                           primary key (id)
) engine=InnoDB;

create table follows (
                         created_at datetime(6) not null,
                         deleted_at datetime(6),
                         followee_id bigint not null,
                         follower_id bigint not null,
                         id bigint not null auto_increment,
                         modified_at datetime(6),
                         primary key (id)
) engine=InnoDB;

create table instructors (
                             created_at datetime(6) not null,
                             deleted_at datetime(6),
                             id bigint not null auto_increment,
                             modified_at datetime(6),
                             bio TEXT,
                             name varchar(255) not null,
                             profile_image_file_key TEXT,
                             profile_image_url TEXT,
                             primary key (id)
) engine=InnoDB;

create table items (
                       current_quantity integer,
                       price integer not null,
                       total_quantity integer,
                       created_at datetime(6) not null,
                       deleted_at datetime(6),
                       id bigint not null auto_increment,
                       modified_at datetime(6),
                       description TEXT,
                       image_file_key varchar(255) not null,
                       image_url varchar(255) not null,
                       lottie_file_key varchar(255),
                       lottie_url varchar(255),
                       name varchar(255) not null,
                       phrase TEXT,
                       acquisition_type enum ('DEFAULT','PURCHASE','REWARD') not null,
                       equip_slot enum ('BACKGROUND','EYEWEAR','FLOOR','FRAME','HAT','SHELF') not null,
                       item_type enum ('ACCESSORY','BACKGROUND','FLOOR','FRAME','FURNITURE','HAT','SHELF') not null,
                       primary key (id)
) engine=InnoDB;

create table meditation_activities (
                                       activity_id bigint not null,
                                       primary key (activity_id)
) engine=InnoDB;

create table message_variations (
                                    is_active bit not null,
                                    created_at datetime(6) not null,
                                    deleted_at datetime(6),
                                    id bigint not null auto_increment,
                                    modified_at datetime(6),
                                    notification_templates_id bigint not null,
                                    body_template varchar(1024) not null,
                                    title_template varchar(255) not null,
                                    primary key (id)
) engine=InnoDB;

create table notification_histories (
                                        clicked_at datetime(6),
                                        created_at datetime(6) not null,
                                        deleted_at datetime(6),
                                        id bigint not null auto_increment,
                                        message_variation_id bigint not null,
                                        modified_at datetime(6),
                                        sent_at datetime(6) not null,
                                        user_id bigint not null,
                                        primary key (id)
) engine=InnoDB;

create table notification_templates (
                                        is_active bit not null,
                                        trigger_condition integer,
                                        created_at datetime(6) not null,
                                        deleted_at datetime(6),
                                        id bigint not null auto_increment,
                                        modified_at datetime(6),
                                        description varchar(255),
                                        type enum ('ACHIEVEMENT_UNLOCKED','DIARY_REMINDER','NEWS_UPDATE','REWARD_ACQUIRED','RE_ENGAGEMENT') not null,
                                        primary key (id)
) engine=InnoDB;

create table purchase_logs (
                               price integer not null,
                               created_at datetime(6) not null,
                               deleted_at datetime(6),
                               id bigint not null auto_increment,
                               item_id bigint not null,
                               modified_at datetime(6),
                               user_id bigint not null,
                               acquisition_type enum ('DEFAULT','PURCHASE','REWARD') not null,
                               primary key (id)
) engine=InnoDB;

create table refresh_tokens (
                                expiry_date datetime(6) not null,
                                user_id bigint not null,
                                token varchar(255) not null,
                                primary key (token)
) engine=InnoDB;

create table screen_time_logs (
                                  duration_in_seconds integer not null,
                                  created_at datetime(6) not null,
                                  deleted_at datetime(6),
                                  id bigint not null auto_increment,
                                  modified_at datetime(6),
                                  user_id bigint not null,
                                  primary key (id)
) engine=InnoDB;

create table sound_effect_activities (
                                         activity_id bigint not null,
                                         primary key (activity_id)
) engine=InnoDB;

create table timeline_events (
                                 duration float(53),
                                 sequence integer,
                                 time float(53) not null,
                                 activity_id bigint,
                                 id bigint not null auto_increment,
                                 action varchar(255),
                                 text_content varchar(255),
                                 primary key (id)
) engine=InnoDB;

create table user_achieved (
                               achieved_at datetime(6),
                               achievement_id bigint not null,
                               id bigint not null auto_increment,
                               user_id bigint not null,
                               primary key (id)
) engine=InnoDB;

create table user_activity_summary (
                                       created_at datetime(6) not null,
                                       deleted_at datetime(6),
                                       id bigint not null auto_increment,
                                       modified_at datetime(6),
                                       total_play_seconds bigint not null,
                                       user_id bigint,
                                       primary key (id)
) engine=InnoDB;

create table user_devices (
                              created_at datetime(6) not null,
                              deleted_at datetime(6),
                              id bigint not null auto_increment,
                              modified_at datetime(6),
                              user_id bigint not null,
                              fcm_token varchar(255) not null,
                              os_type enum ('ANDROID','IOS') not null,
                              primary key (id)
) engine=InnoDB;

create table user_equipped_collections (
                                           collection_id bigint,
                                           user_id bigint not null
) engine=InnoDB;

create table user_notification_settings (
                                            diary_notification_enabled bit not null,
                                            diary_notification_time time(6),
                                            re_engagement_notification_enabled bit not null,
                                            soomsoom_news_notification_enabled bit not null,
                                            created_at datetime(6) not null,
                                            deleted_at datetime(6),
                                            id bigint not null auto_increment,
                                            modified_at datetime(6),
                                            user_id bigint not null,
                                            primary key (id)
) engine=InnoDB;

create table user_owned_collections (
                                        collection_id bigint,
                                        user_id bigint not null
) engine=InnoDB;

create table user_owned_items (
                                  item_id bigint,
                                  user_id bigint not null
) engine=InnoDB;

create table user_progress (
                               current_value integer not null,
                               id bigint not null auto_increment,
                               user_id bigint not null,
                               type enum ('BREATHING_COUNT','BREATHING_MONTHLY_COUNT','BREATHING_MULTI_TYPE_COUNT','BREATHING_STREAK','BREATHING_TOTAL_SECONDS','CONNECTION_STREAK','DIARY_COUNT','DIARY_MONTHLY_COUNT','DIARY_STREAK','HIDDEN_CUSTOMIZE_CHARACTER','HIDDEN_EMOTION_OVERCOME','HIDDEN_STAY_HOME_SCREEN','MEDITATION_COUNT','MEDITATION_LATE_NIGHT_STREAK','MEDITATION_MONTHLY_COUNT','MEDITATION_STREAK','MEDITATION_TOTAL_SECONDS','SOUND_EFFECT_TOTAL_SECONDS'),
                               primary key (id)
) engine=InnoDB;

create table users (
                       points integer not null,
                       background bigint,
                       created_at datetime(6) not null,
                       deleted_at datetime(6),
                       eyewear bigint,
                       floor bigint,
                       frame bigint,
                       hat bigint,
                       id bigint not null auto_increment,
                       modified_at datetime(6),
                       shelf bigint,
                       device_id varchar(255),
                       password varchar(255),
                       social_id varchar(255),
                       username varchar(255),
                       account_type enum ('ANONYMOUS','ID_PASSWORD','SOCIAL'),
                       daily_duration enum ('TEN_MINUTES','THIRTY_MINUTES','THREE_MINUTES','TWENTY_MINUTES'),
                       focus_goal enum ('BE_PRESENT','HAVE_A_CALM_MIND','IMPROVE_SLEEP_QUALITY','MANAGE_ANXIETY','MANAGE_STRESS','OTHER'),
                       role enum ('ROLE_ADMIN','ROLE_ANONYMOUS','ROLE_USER'),
                       social_provider enum ('APPLE','GOOGLE'),
                       primary key (id)
) engine=InnoDB;

alter table carts
    add constraint UK64t7ox312pqal3p7fg9o503c2 unique (user_id);

alter table connection_logs
    add constraint UKmy6ahkfsjoe0iu3bj1aehpot0 unique (user_id, created_at);

alter table user_activity_summary
    add constraint UK2hy0knhpjdqu9dro0jyf8higj unique (user_id);

alter table user_devices
    add constraint UKo6vhn2gmiutbtiqk0ljgt5us0 unique (fcm_token);

alter table user_notification_settings
    add constraint UKiopsy42i35vabad4lpu2xcfbq unique (user_id);

alter table user_progress
    add constraint UKkwnipgc8pxp8bca3jmp5b41cp unique (user_id, type);

alter table users
    add constraint UK6jl2ojdbeq8i75ouaqq7ks31 unique (device_id);

alter table users
    add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);

alter table activity_descriptions
    add constraint FKniqcakrxygumslmtvyqonixss
        foreign key (activity_id)
            references activities (id);

alter table breathing_activities
    add constraint FK2tuodar6suymu5ax9s9x689xw
        foreign key (activity_id)
            references activities (id);

alter table cart_items
    add constraint FKpcttvuq4mxppo8sxggjtn5i2c
        foreign key (cart_id)
            references carts (id);

alter table collection_items
    add constraint FK44osh81d1xg7h7j84jjf9e5vp
        foreign key (item_id)
            references items (id);

alter table collection_items
    add constraint FKrkp5wpkksx5aqex6v4qnq9912
        foreign key (collection_id)
            references collections (id);

alter table meditation_activities
    add constraint FKglmn3dwlgsldrcv8g7q4xdfl7
        foreign key (activity_id)
            references activities (id);

alter table message_variations
    add constraint FKt7nxs58cheahihvrih0g2akd1
        foreign key (notification_templates_id)
            references notification_templates (id);

alter table sound_effect_activities
    add constraint FKbp3irtquxcg3s1oqje6l58t33
        foreign key (activity_id)
            references activities (id);

alter table timeline_events
    add constraint FKixcwtgioskeskbrhhb4guejmo
        foreign key (activity_id)
            references breathing_activities (activity_id);

alter table user_equipped_collections
    add constraint FKo2uk7dnmh8i2r84eu997mt7jf
        foreign key (user_id)
            references users (id);

alter table user_owned_collections
    add constraint FK25unw0ln6m6x32gk10sv07puj
        foreign key (user_id)
            references users (id);

alter table user_owned_items
    add constraint FKioe695fj13n7ngldanei2s0pp
        foreign key (user_id)
            references users (id);
