ALTER TABLE announcements
    ADD COLUMN image_url VARCHAR(1024) NULL COMMENT '공지사항 이미지 URL',
    ADD COLUMN image_file_key VARCHAR(1024) NULL COMMENT 'S3 이미지 파일 키';
