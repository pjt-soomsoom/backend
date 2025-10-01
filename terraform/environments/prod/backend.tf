terraform {
    required_version = ">= 1.0"

    required_providers {
        aws = {
            source  = "hashicorp/aws"
            version = "~> 5.36"
        }
    }

    backend "s3" {
        bucket         = "soomsoom-terraform-state-bucket"
        key            = "prod/terraform.tfstate" # prod 환경 전용 상태 파일 경로
        region         = "ap-northeast-2"
        dynamodb_table = "soomsoom-terraform-state-lock"
        encrypt        = true
        profile        = "soomsoom"
    }
}
