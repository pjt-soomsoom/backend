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
        key            = "test/terraform.tfstate" # test 환경 전용 상태 파일 경로
        region         = "ap-northeast-2"
        dynamodb_table = "soomsoom-terraform-state-lock"
        encrypt        = true
    }
}
