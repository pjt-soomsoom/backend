-- V2__modify_achievement_conditions_for_late_night_count.sql

-- 1단계: 새로운 ENUM 값('MEDITATION_LATE_NIGHT_COUNT')을 기존 목록에 추가합니다.
-- 이렇게 하면 기존 데이터는 유지한 채로 새로운 값으로 UPDATE가 가능해집니다.
ALTER TABLE achievement_conditions
    MODIFY COLUMN type ENUM (
        'BREATHING_COUNT',
        'BREATHING_MONTHLY_COUNT',
        'BREATHING_MULTI_TYPE_COUNT',
        'BREATHING_STREAK',
        'BREATHING_TOTAL_MINUTES',
        'DIARY_COUNT',
        'DIARY_MONTHLY_COUNT',
        'DIARY_STREAK',
        'HIDDEN_CUSTOMIZE_CHARACTER',
        'HIDDEN_EMOTION_OVERCOME',
        'HIDDEN_STAY_HOME_SCREEN',
        'MEDITATION_COUNT',
        'MEDITATION_LATE_NIGHT_STREAK', -- 아직 삭제하지 않음
        'MEDITATION_LATE_NIGHT_COUNT',  -- 새로운 값 추가
        'MEDITATION_MONTHLY_COUNT',
        'MEDITATION_STREAK',
        'MEDITATION_TOTAL_MINUTES',
        'SOUND_EFFECT_TOTAL_MINUTES'
        );

-- 2단계: 기존의 'MEDITATION_LATE_NIGHT_STREAK' 데이터를 새로운 'MEDITATION_LATE_NIGHT_COUNT'로 모두 업데이트합니다.
UPDATE achievement_conditions
SET type = 'MEDITATION_LATE_NIGHT_COUNT'
WHERE type = 'MEDITATION_LATE_NIGHT_STREAK';

-- 3단계: 이제 사용하지 않는 'MEDITATION_LATE_NIGHT_STREAK' 값을 ENUM 목록에서 최종적으로 제거합니다.
ALTER TABLE achievement_conditions
    MODIFY COLUMN type ENUM (
        'BREATHING_COUNT',
        'BREATHING_MONTHLY_COUNT',
        'BREATHING_MULTI_TYPE_COUNT',
        'BREATHING_STREAK',
        'BREATHING_TOTAL_MINUTES',
        'DIARY_COUNT',
        'DIARY_MONTHLY_COUNT',
        'DIARY_STREAK',
        'HIDDEN_CUSTOMIZE_CHARACTER',
        'HIDDEN_EMOTION_OVERCOME',
        'HIDDEN_STAY_HOME_SCREEN',
        'MEDITATION_COUNT',
        -- 'MEDITATION_LATE_NIGHT_STREAK', -- 여기서 제거
        'MEDITATION_LATE_NIGHT_COUNT',
        'MEDITATION_MONTHLY_COUNT',
        'MEDITATION_STREAK',
        'MEDITATION_TOTAL_MINUTES',
        'SOUND_EFFECT_TOTAL_MINUTES'
        );
