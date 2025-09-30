# Terraform 및 AWS Provider 설정
terraform {
  required_version = ">= 1.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# 사용할 AWS 리전을 설정합니다.
provider "aws" {
  profile = "soomsoom"
  region = "ap-northeast-2"
}

