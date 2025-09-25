CREATE TABLE missions
(
    id                            BIGINT AUTO_INCREMENT PRIMARY KEY,
    type                          VARCHAR(255) NOT NULL COMMENT '미션 타입 Enum',
    title                         VARCHAR(255) NOT NULL COMMENT '미션 제목',
    description                   VARCHAR(255) NOT NULL COMMENT '미션 설명',
    target_value                  INT          NOT NULL COMMENT '미션 목표값',
    completion_notification_title VARCHAR(255) NOT NULL COMMENT '달성 알림 제목',
    completion_notification_body  VARCHAR(255) NOT NULL COMMENT '달성 알림 본문',
    points                        INT NULL COMMENT '보상 포인트',
    item_id                       BIGINT NULL COMMENT '보상 아이템 ID',
    reward_notification_title     VARCHAR(255) NOT NULL COMMENT '보상 알림 제목',
    reward_notification_body      VARCHAR(255) NOT NULL COMMENT '보상 알림 본문',
    repeatable_type               VARCHAR(255) NOT NULL COMMENT '반복 주기 타입 Enum',
    claim_type                    VARCHAR(255) NOT NULL COMMENT '보상 수령 방식 Enum',
    created_at                    DATETIME(6) NOT NULL,
    modified_at                   DATETIME(6) NULL,
    deleted_at                    DATETIME(6) NULL COMMENT '삭제(비활성화) 시간',
    INDEX idx_mission_type_deleted_at (type, deleted_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='미션';

-- 사용자의 미션 진행도를 저장하는 테이블 (복잡한 미션용)
-- BaseTimeEntity로부터 created_at, modified_at, deleted_at 컬럼을 상속받습니다.
CREATE TABLE user_mission_progress
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL COMMENT '사용자 ID',
    mission_id  BIGINT       NOT NULL COMMENT '미션 ID',
    progress_data JSON         NOT NULL COMMENT '미션 진행 상태 데이터 (JSON)',
    created_at  DATETIME(6)  NOT NULL,
    modified_at DATETIME(6)  NULL,
    deleted_at  DATETIME(6)  NULL,
    UNIQUE INDEX uk_user_mission_progress_user_mission (user_id, mission_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='사용자 미션 진행도';

-- 사용자의 미션 완료 및 보상 수령 기록을 저장하는 테이블
-- BaseTimeEntity로부터 created_at, modified_at, deleted_at 컬럼을 상속받습니다.
CREATE TABLE mission_completion_logs
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT      NOT NULL COMMENT '사용자 ID',
    mission_id   BIGINT      NOT NULL COMMENT '미션 ID',
    completed_at DATETIME(6) NOT NULL COMMENT '미션 완료 시간',
    rewarded_at  DATETIME(6) NULL COMMENT '보상 수령 시간',
    created_at   DATETIME(6) NOT NULL,
    modified_at  DATETIME(6) NULL,
    deleted_at   DATETIME(6) NULL,
    INDEX idx_mission_log_user_mission_completed (user_id, mission_id, completed_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='미션 완료 기록';
