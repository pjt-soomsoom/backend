CREATE TABLE shedlock (
                          name VARCHAR(64) NOT NULL,
                          lock_until TIMESTAMP NOT NULL,
                          locked_at TIMESTAMP NOT NULL,
                          locked_by VARCHAR(255) NOT NULL,
                          PRIMARY KEY (name)
) engine=InnoDB;


create table achievement_conditions (
                                        target_value integer,
                                        achievement_id bigint not null,
                                        id bigint not null auto_increment,
                                        type enum ('BREATHING_COUNT','BREATHING_MONTHLY_COUNT','BREATHING_MULTI_TYPE_COUNT','BREATHING_STREAK','BREATHING_TOTAL_MINUTES','DIARY_COUNT','DIARY_MONTHLY_COUNT','DIARY_STREAK','HIDDEN_CUSTOMIZE_CHARACTER','HIDDEN_EMOTION_OVERCOME','HIDDEN_STAY_HOME_SCREEN','MEDITATION_COUNT','MEDITATION_LATE_NIGHT_STREAK','MEDITATION_MONTHLY_COUNT','MEDITATION_STREAK','MEDITATION_TOTAL_MINUTES','SOUND_EFFECT_TOTAL_MINUTES'),
                                        primary key (id)
) engine=InnoDB;

create table achievements (
                              reward_points integer,
                              created_at datetime(6) not null,
                              deleted_at datetime(6),
                              id bigint not null auto_increment,
                              modified_at datetime(6),
                              reward_item_id bigint,
                              name varchar(255),
                              phrase varchar(255),
                              reward_body_template varchar(255),
                              reward_title_template varchar(255),
                              unlocked_body_template varchar(255),
                              unlocked_title_template varchar(255),
                              category enum ('BREATHING','DIARY','HIDDEN','MEDITATION'),
                              grade enum ('BRONZE','GOLD','SILVER','SPECIAL'),
                              primary key (id)
) engine=InnoDB;

create table activities (
                            duration_in_seconds integer,
                            author_id bigint,
                            created_at datetime(6) not null,
                            deleted_at datetime(6),
                            id bigint not null auto_increment,
                            modified_at datetime(6),
                            narrator_id bigint,
                            activity_type varchar(31) not null,
                            audio_file_key varchar(255),
                            audio_url varchar(255),
                            mini_thumbnail_file_key varchar(255),
                            mini_thumbnail_image_url varchar(255),
                            thumbnail_file_key varchar(255),
                            thumbnail_image_url varchar(255),
                            title varchar(255),
                            category enum ('BREATHING','MEDITATION','REST','SLEEP') not null,
                            primary key (id)
) engine=InnoDB;

create table activity_completion_effects (
                                             sequence integer not null,
                                             activity_id bigint not null,
                                             effect_text TEXT,
                                             primary key (sequence, activity_id)
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

create table ad_reward_logs (
                                amount integer,
                                created_at datetime(6) not null,
                                deleted_at datetime(6),
                                id bigint not null auto_increment,
                                modified_at datetime(6),
                                user_id bigint not null,
                                ad_unit_id varchar(255),
                                transaction_id varchar(255),
                                primary key (id)
) engine=InnoDB;

create table announcements (
                               created_at datetime(6) not null,
                               deleted_at datetime(6),
                               id bigint not null auto_increment,
                               modified_at datetime(6),
                               sent_at datetime(6),
                               title varchar(255),
                               content longtext,
                               primary key (id)
) engine=InnoDB;

create table banners (
                         active bit not null,
                         display_order integer not null,
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

create table breathing_activity_jpa_entity (
                                               id bigint not null,
                                               primary key (id)
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
                                  constraint uk_collection_items primary key (collection_id, item_id)
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
                                       id bigint not null,
                                       primary key (id)
) engine=InnoDB;

create table message_variations (
                                    active bit not null,
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
                                        message_variations_id bigint not null,
                                        modified_at datetime(6),
                                        sent_at datetime(6) not null,
                                        user_id bigint not null,
                                        primary key (id)
) engine=InnoDB;

create table notification_templates (
                                        active bit not null,
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

create table rewarded_ads (
                              active bit not null,
                              reward_amount integer,
                              created_at datetime(6) not null,
                              deleted_at datetime(6),
                              id bigint not null auto_increment,
                              modified_at datetime(6),
                              ad_unit_id varchar(255),
                              title varchar(255),
                              primary key (id)
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
                                         id bigint not null,
                                         primary key (id)
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
                               achievement_id bigint,
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

create table user_announcements (
                                    `read` bit not null,
                                    announcement_id bigint,
                                    created_at datetime(6) not null,
                                    deleted_at datetime(6),
                                    id bigint not null auto_increment,
                                    modified_at datetime(6),
                                    read_at datetime(6),
                                    received_at datetime(6),
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
                                            diary_notification_enabled bit,
                                            diary_notification_time time(6),
                                            re_engagement_notification_enabled bit,
                                            soomsoom_news_notification_enabled bit,
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
                               current_value integer,
                               id bigint not null auto_increment,
                               user_id bigint not null,
                               type enum ('BREATHING_COUNT','BREATHING_MONTHLY_COUNT','BREATHING_MULTI_TYPE_COUNT','BREATHING_STREAK','BREATHING_TOTAL_MINUTES','DIARY_COUNT','DIARY_MONTHLY_COUNT','DIARY_STREAK','HIDDEN_CUSTOMIZE_CHARACTER','HIDDEN_EMOTION_OVERCOME','HIDDEN_STAY_HOME_SCREEN','MEDITATION_COUNT','MEDITATION_LATE_NIGHT_STREAK','MEDITATION_MONTHLY_COUNT','MEDITATION_STREAK','MEDITATION_TOTAL_MINUTES','SOUND_EFFECT_TOTAL_MINUTES'),
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

create index idx_ac_achievement_id
    on achievement_conditions (achievement_id);

create index idx_ac_type
    on achievement_conditions (type);

create index idx_achievements_deleted_at
    on achievements (deleted_at);

create index idx_achievements_category
    on achievements (category);

alter table achievements
    add constraint UKktpif54u9a3ssn6rpxxqx6jvp unique (name);

create index idx_activities_type_category
    on activities (activity_type, category);

create index idx_activities_author_id
    on activities (author_id);

create index idx_activities_narrator_id
    on activities (narrator_id);

create index idx_activities_deleted_at
    on activities (deleted_at);

create index idx_acl_user_type_created
    on activity_completion_log (user_id, activity_type, created_at);

alter table activity_progress
    add constraint uk_ap_user_activity unique (user_id, activity_id);

create index idx_arl_user_ad_created
    on ad_reward_logs (user_id, ad_unit_id, created_at);

alter table ad_reward_logs
    add constraint UK8tepffuut86g94davf44suyeq unique (transaction_id);

create index idx_announcements_deleted_at
    on announcements (deleted_at);

create index idx_banners_active_deleted_order
    on banners (active, deleted_at, display_order);

alter table cart_items
    add constraint uk_cart_item_cart_item unique (cart_id, item_id);

alter table carts
    add constraint UK64t7ox312pqal3p7fg9o503c2 unique (user_id);

alter table collections
    add constraint UKddet6fvs2nj65hg90ew34sgjy unique (name);

create index idx_connection_logs_user_created
    on connection_logs (user_id, created_at);

create index idx_diaries_user_created
    on diaries (user_id, created_at);

alter table favorites
    add constraint uk_favorite_user_activity unique (user_id, activity_id);

alter table follows
    add constraint uk_follow_follower_followee unique (follower_id, followee_id);

create index idx_instructors_deleted_at
    on instructors (deleted_at);

create index idx_items_item_type
    on items (item_type);

create index idx_items_acquisition_type
    on items (acquisition_type);

create index idx_items_deleted_at
    on items (deleted_at);

alter table items
    add constraint UKmnhl79u3u6jdvutuoeq54stne unique (name);

create index idx_mv_notification_templates_id
    on message_variations (notification_templates_id);

create index idx_nh_user_sent_at
    on notification_histories (user_id, sent_at desc);

create index idx_nt_type_active
    on notification_templates (type, active);

create index idx_purchase_logs_user_created
    on purchase_logs (user_id, created_at desc);

create index idx_refresh_tokens_user_id
    on refresh_tokens (user_id);

create index idx_rewarded_ads_active
    on rewarded_ads (active);

alter table rewarded_ads
    add constraint UK8jsaf6tall9vhyqyw1b5whu1q unique (ad_unit_id);

create index idx_screen_time_logs_user_created
    on screen_time_logs (user_id, created_at);

create index idx_timeline_events_activity_id
    on timeline_events (activity_id);

alter table user_achieved
    add constraint uk_user_achieved_user_achievement unique (user_id, achievement_id);

alter table user_activity_summary
    add constraint UK2hy0knhpjdqu9dro0jyf8higj unique (user_id);

create index idx_ua_user_received
    on user_announcements (user_id, received_at desc);

create index idx_user_announcements_unread
    on user_announcements (user_id, `read`, deleted_at);

create index idx_user_announcements_announcement_id
    on user_announcements (announcement_id);

create index idx_user_devices_user_id
    on user_devices (user_id);

alter table user_devices
    add constraint UKo6vhn2gmiutbtiqk0ljgt5us0 unique (fcm_token);

create index idx_uns_diary_reminder
    on user_notification_settings (diary_notification_enabled, diary_notification_time);

alter table user_notification_settings
    add constraint UKiopsy42i35vabad4lpu2xcfbq unique (user_id);

alter table user_progress
    add constraint UK6lyhl5a7d1o2tn65pji43rm1j unique (user_id, type);

create index idx_users_social
    on users (social_provider, social_id);

create index idx_users_deleted_at
    on users (deleted_at);

alter table users
    add constraint UK6jl2ojdbeq8i75ouaqq7ks31 unique (device_id);

alter table users
    add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);

alter table achievement_conditions
    add constraint FKdmql3v0s4stdvirkrl0at4tat
        foreign key (achievement_id)
            references achievements (id);

alter table activity_completion_effects
    add constraint FKjeyexcyjeanfdsnjmcgufg298
        foreign key (activity_id)
            references activities (id);

alter table activity_descriptions
    add constraint FKniqcakrxygumslmtvyqonixss
        foreign key (activity_id)
            references activities (id);

alter table breathing_activity_jpa_entity
    add constraint FK2pmevk79r5xpm4ifcoxetfm1b
        foreign key (id)
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
    add constraint FKdmu28i5shi5dwv950v03s7jmq
        foreign key (id)
            references activities (id);

alter table message_variations
    add constraint FKt7nxs58cheahihvrih0g2akd1
        foreign key (notification_templates_id)
            references notification_templates (id);

alter table sound_effect_activities
    add constraint FKstjl8bi2odt1a6gy092muuq1x
        foreign key (id)
            references activities (id);

alter table timeline_events
    add constraint FK2eeryyu8do8m7euwxo2p7lu18
        foreign key (activity_id)
            references breathing_activity_jpa_entity (id);

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
