CREATE TABLE `app_versions`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '앱 버전 ID',
    `os`            VARCHAR(255) NOT NULL COMMENT '운영체제 (AOS, IOS)',
    `version_name`  VARCHAR(255) NOT NULL COMMENT '버전 이름 (e.g., 1.0.0)',
    `force_update`  BIT(1)       NOT NULL COMMENT '강제 업데이트 여부',
    `created_at`    DATETIME(6)  NOT NULL COMMENT '생성 일시',
    `modified_at`   DATETIME(6)  NULL     COMMENT '수정 일시',
    `deleted_at`    DATETIME(6)  NULL     COMMENT '삭제 일시',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '앱 버전 정보';

-- 인덱스 추가
CREATE INDEX `idx_app_versions_os_created_at` ON `app_versions` (`os`, `created_at` DESC);
