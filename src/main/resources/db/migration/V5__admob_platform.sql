ALTER TABLE rewarded_ads
    ADD COLUMN platform ENUM('ANDROID', 'IOS') NULL;

UPDATE rewarded_ads
SET platform = 'IOS'
WHERE platform IS NULL;

ALTER TABLE rewarded_ads
    MODIFY COLUMN platform ENUM('ANDROID', 'IOS') NOT NULL;

DROP INDEX idx_rewarded_ads_active ON rewarded_ads;

CREATE INDEX idx_rewarded_ads_active_platform
    ON rewarded_ads (active, platform);
