locals {
    app_parameters = {
        "spring.datasource.username"                 = { value = var.db_username, type = "SecureString" }
        "spring.datasource.password"                 = { value = var.db_password, type = "SecureString" }
        "spring.datasource.driver-class-name"        = { value = var.db_driver_class_name, type = "String" }
        "spring.jpa.hibernate.ddl-auto"              = { value = var.jpa_ddl_auto, type = "String" }
        "spring.jpa.show-sql"                        = { value = var.jpa_show_sql, type = "String" }
        "spring.jpa.properties.hibernate.format_sql" = { value = var.jpa_format_sql, type = "String" }
        "jwt.secret"                                 = { value = var.jwt_secret, type = "SecureString" }
        "jwt.access.expiration"                      = { value = var.jwt_access_expiration, type = "String" }
        "jwt.refresh.expiration"                     = { value = var.jwt_refresh_expiration, type = "String" }
        "cloud.aws.s3.bucket"                        = { value = var.aws_s3_bucket, type = "String" }
        "cloud.aws.s3.base-url"                      = { value = var.aws_s3_base_url, type = "String" }
        "oauth.google.client-id"                     = { value = var.oauth_google_client_id, type = "SecureString" }
        "oauth.apple.client-id"                      = { value = var.oauth_apple_client_id, type = "SecureString" }
        "firebase.service-account"                   = { value = var.firebase_service_account_json, type = "SecureString" }
        "app.daily-cutoff-hour"                      = { value = var.app_daily_cutoff_hour, type = "String" }
        "diary.default-time"                         = { value = var.diary_default_time, type = "String" }
        "alarm.inactive-user.default-time"           = { value = var.alarm_inactive_user_default_time, type = "String" }
        "alarm.batch-size"                           = { value = var.alarm_batch_size, type = "String" }
        "reward-ad.base-path"                        = { value = var.reward_ad_base_path, type = "String" }
        "mission.page-visit.identifier.yawoongi"     = { value = var.mission_page_visit_identifier_yawoongi, type = "String" }
    }
}

resource "aws_ssm_parameter" "db_url" {
    name  = "/config/soomsoom_${var.environment}/spring.datasource.url"
    type  = "SecureString"
    value = "jdbc:mysql://${aws_db_instance.main.endpoint}/${aws_db_instance.main.db_name}"

    tags = {
        Project     = var.project_name
        Environment = var.environment
    }
}


resource "aws_ssm_parameter" "app_config" {
    for_each = local.app_parameters

    name  = "/config/soomsoom_${var.environment}/${each.key}"
    type  = each.value.type
    value = each.value.value

    tags = {
        Project     = var.project_name
        Environment = var.environment
    }
}
