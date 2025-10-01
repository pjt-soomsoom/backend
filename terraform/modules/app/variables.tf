# --- 프로젝트 공통 변수

variable "project_name" {
    description = "프로젝트 이름. 모든 AWS 자원의 이릅에 접두사로 사용"
    type        = string
    default     = "soomsoom"
}

variable "environment" {
    description = "배포 환경 (test, prod). 자원 이름과 설정을 구분하는 데 사용"
    type        = string
}

variable "domain_name" {
    description = "실제로 구매한 도메인 이름. prod 환경의 HTTPS 인증서 발급 및 DNS 설정에 사용"
    type        = string
}

# --- Github OIDC 연동을 위한 변수

variable "github_repo" {
    description = "Github 리포지토리 경로"
    type        = string
    default     = "pjt-soomsoom/backend"
}

# --- Datasource ---

variable "db_username" {
    description = "RDS 데이터베이스 사용자 이름"
    type        = string
    sensitive   = true
}

variable "db_password" {
    description = "RDS 데이터베이스 비밀번호"
    type        = string
    sensitive   = true
}

variable "db_driver_class_name" {
    description = "DB 드라이버 클래스 이름"
    type        = string
}

# --- JPA ---
variable "jpa_ddl_auto" {
    type    = string
    default = "none"
}

variable "jpa_show_sql" {
    type    = string
    default = "false"
}

variable "jpa_format_sql" {
    type    = string
    default = "false"
}

# --- JWT ---
variable "jwt_secret" {
    type      = string
    sensitive = true
}

variable "jwt_access_expiration" {
    type = string
}

variable "jwt_refresh_expiration" {
    type = string
}

# --- AWS S3 ---
variable "aws_s3_bucket" {
    type = string
}

variable "aws_s3_base_url" {
    type = string
}

variable "aws_region" {
    description = "배포할 AWS 리전"
    type        = string
    default     = "ap-northeast-2"
}

# --- OAuth ---
variable "oauth_google_client_id" {
    type      = string
    sensitive = true
}

variable "oauth_apple_client_id" {
    type      = string
    sensitive = true
}

# --- Firebase ---
variable "firebase_service_account_json" {
    type      = string
    sensitive = true
}

variable "app_daily_cutoff_hour" {
    type = string
}

variable "diary_default_time" {
    type = string
}

variable "alarm_inactive_user_default_time" {
    type = string
}

variable "alarm_batch_size" {
    type = string
}

variable "reward_ad_base_path" {
    type = string
}

variable "mission_page_visit_identifier_yawoongi" {
    type = string
}
