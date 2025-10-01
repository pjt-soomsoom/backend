# Terraform 및 AWS Provider 설정
terraform {
  required_version = ">= 1.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.36"
    }
  }
  backend "s3" {
      bucket         = "soomsoom-terraform-state-bucket" # 1단계에서 만든 S3 버킷 이름
      key            = "global/terraform.tfstate"      # S3 버킷 내에 상태 파일이 저장될 경로
      region         = "ap-northeast-2"
      dynamodb_table = "soomsoom-terraform-state-lock"   # 1단계에서 만든 DynamoDB 테이블 이름
      encrypt        = true
      # profile        = "soomsoom"
  }
}

# 사용할 AWS 리전을 설정합니다.
provider "aws" {
  # profile = "soomsoom"
  region = "ap-northeast-2"
}

