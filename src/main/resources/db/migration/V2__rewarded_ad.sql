CREATE TABLE rewarded_ads
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(255) NOT NULL COMMENT '광고 제목',
    ad_unit_id    VARCHAR(255) NOT NULL UNIQUE COMMENT 'Google AdMob 광고 단위 ID',
    reward_amount INT          NOT NULL COMMENT '보상 포인트 양',
    active        BOOLEAN      NOT NULL DEFAULT TRUE COMMENT '활성화 여부',
    created_at    DATETIME(6)  NOT NULL,
    modified_at   DATETIME(6)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- =====================================================================================================================
-- Ad Reward Logs (사용자의 광고 시청 '기록')
-- =====================================================================================================================
CREATE TABLE ad_reward_logs
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT       NOT NULL COMMENT '사용자 ID',
    ad_unit_id     VARCHAR(255) NOT NULL COMMENT 'Google AdMob 광고 단위 ID',
    transaction_id VARCHAR(255) NOT NULL UNIQUE COMMENT 'AdMob SSV 트랜잭션 ID',
    amount         INT          NOT NULL COMMENT '지급된 보상 양',
    created_at     DATETIME(6)  NOT NULL,
    modified_at    DATETIME(6),
    INDEX          idx_ad_reward_logs_user_id_ad_unit_id_created_at (user_id, ad_unit_id, created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
