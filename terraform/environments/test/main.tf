provider "aws" {
    region = "ap-northeast-2"
}

module "app" {
    source = "../../modules/app" # 공통 모듈 경로

    # 모듈에 필요한 변수들을 전달합니다.
    environment                         = "test"
    project_name                        = var.project_name
    domain_name                         = var.domain_name
    github_repo                         = var.github_repo
    db_username                         = var.db_username
    db_password                         = var.db_password
    db_driver_class_name                = var.db_driver_class_name
    jpa_ddl_auto                        = var.jpa_ddl_auto
    jpa_show_sql                        = var.jpa_show_sql
    jpa_format_sql                      = var.jpa_format_sql
    jwt_secret                          = var.jwt_secret
    jwt_access_expiration               = var.jwt_access_expiration
    jwt_refresh_expiration              = var.jwt_refresh_expiration
    aws_s3_bucket                       = var.aws_s3_bucket
    aws_s3_base_url                     = var.aws_s3_base_url
    aws_region                          = var.aws_region
    oauth_google_client_id              = var.oauth_google_client_id
    oauth_apple_client_id               = var.oauth_apple_client_id
    firebase_service_account_json       = var.firebase_service_account_json
    app_daily_cutoff_hour               = var.app_daily_cutoff_hour
    diary_default_time                  = var.diary_default_time
    alarm_inactive_user_default_time    = var.alarm_inactive_user_default_time
    alarm_batch_size                    = var.alarm_batch_size
    reward_ad_base_path                 = var.reward_ad_base_path
    mission_page_visit_identifier_yawoongi = var.mission_page_visit_identifier_yawoongi
}
