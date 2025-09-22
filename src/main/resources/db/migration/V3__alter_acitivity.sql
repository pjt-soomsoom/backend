-- V3: 'activities' 테이블에 미니 썸네일과 완료 효과 텍스트 관련 컬럼 및 테이블 추가

-- 1. 'activities' 테이블에 미니 썸네일 관련 컬럼들을 추가합니다.
--    이 컬럼들은 주로 호흡(Breathing) 타입의 액티비티에서 사용될 것입니다.
ALTER TABLE activities
ADD COLUMN mini_thumbnail_image_url VARCHAR(255) COMMENT '미니 썸네일 이미지 URL',
ADD COLUMN mini_thumbnail_file_key VARCHAR(255) COMMENT '미니 썸네일 파일 키';

-- 2. 활동 완료 시 보여줄 텍스트 목록을 저장하기 위한 새로운 테이블을 생성합니다.
--    이 테이블은 'activities' 테이블과 1:N 관계를 가집니다.
CREATE TABLE activity_completion_effects (
                                             activity_id BIGINT NOT NULL COMMENT '활동 ID',
                                             effect_text TEXT COMMENT '완료 효과 텍스트',
                                             sequence INT NOT NULL COMMENT '텍스트 순서',
                                             PRIMARY KEY (activity_id, sequence),
                                             CONSTRAINT fk_completion_effects_to_activities
                                                 FOREIGN KEY (activity_id)
                                                     REFERENCES activities (id)
                                                     ON DELETE CASCADE
) COMMENT = '활동 완료 효과 텍스트 목록';
