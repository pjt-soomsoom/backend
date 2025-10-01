# Terraform 및 AWS Provider 설정
terraform {
    required_version = ">= 1.0"
    required_providers {
        aws = {
            source  = "hashicorp/aws"
            version = "~> 5.36"
        }
    }
    # 공유 자원은 상태 파일도 별도로 관리합니다.
    backend "s3" {
        bucket         = "soomsoom-terraform-state-bucket"
        key            = "shared/terraform.tfstate"
        region         = "ap-northeast-2"
        dynamodb_table = "soomsoom-terraform-state-lock"
        encrypt        = true
        profile        = "soomsoom"
    }
}

provider "aws" {
    region = "ap-northeast-2"
    profile = "soomsoom"
}

# --- 변수 선언 ---
variable "project_name" {
    type    = string
    default = "soomsoom"
}
variable "github_repo" {
    type    = string
    default = "pjt-soomsoom/backend"
}

# --- Github Actions OIDC용 IAM 역할 및 Provider ---

resource "aws_iam_openid_connect_provider" "github" {
    url             = "https://token.actions.githubusercontent.com"
    client_id_list  = ["sts.amazonaws.com"]
    thumbprint_list = ["1c58a3a8518e8759bf075b76b750d4f2df264fcd"]
}

data "aws_iam_policy_document" "github_actions_assume_role" {
    statement {
        effect  = "Allow"
        actions = ["sts:AssumeRoleWithWebIdentity"]
        principals {
            type        = "Federated"
            identifiers = [aws_iam_openid_connect_provider.github.arn]
        }
        condition {
            test     = "StringLike"
            variable = "token.actions.githubusercontent.com:sub"
            # test와 prod 환경 브랜치 모두 허용하도록 와일드카드 사용
            values   = ["repo:${var.github_repo}:*"]
        }
    }
}

resource "aws_iam_role" "github_actions" {
    name                 = "${var.project_name}-github-actions-role"
    assume_role_policy   = data.aws_iam_policy_document.github_actions_assume_role.json
    max_session_duration = 10800
}

resource "aws_iam_policy" "github_actions" {
    name   = "${var.project_name}-github-actions-policy"
    policy = jsonencode({
        Version = "2012-10-17",
        Statement = [
            # --- Terraform Backend 및 CI/CD 기본 권한 ---
            {
                Effect = "Allow",
                Action = [
                    # ECR 로그인 및 이미지 푸시에 필요한 전체 권한
                    "ecr:*"
                    # "ecr:GetAuthorizationToken",
                    # "ecr:BatchCheckLayerAvailability",
                    # "ecr:CompleteLayerUpload",
                    # "ecr:InitiateLayerUpload",
                    # "ecr:PutImage",
                    # "ecr:UploadLayerPart",
                    # "ecr:BatchGetImage",
                    # "ecr:PutImageManifest",
                    # "ecr:BatchDeleteImage",
                    # "ecr:DescribeRepositories", # 저장소 확인을 위해 누락된 권한 추가
                    # "ecr:DescribeImages"      # 이미지 메타데이터 확인을 위해 누락된 권한 추가
                ],
                Resource = "*"
            },
            { Effect = "Allow", Action = ["s3:PutObject"], Resource = ["arn:aws:s3:::${var.project_name}-test-bucket/*", "arn:aws:s3:::${var.project_name}-prod-bucket/*"] },
            { Effect = "Allow", Action = ["s3:GetObject", "s3:PutObject", "s3:DeleteObject", "s3:HeadObject"], Resource = "arn:aws:s3:::soomsoom-terraform-state-bucket/*" },
            { Effect = "Allow", Action = "s3:ListBucket", Resource = "arn:aws:s3:::soomsoom-terraform-state-bucket" },
            { Effect = "Allow", Action = ["dynamodb:GetItem", "dynamodb:PutItem", "dynamodb:DeleteItem"], Resource = "arn:aws:dynamodb:*:*:table/soomsoom-terraform-state-lock" },
            # --- Terraform Apply를 위한 전체 인프라 관리 권한 ---
            {
                Effect = "Allow",
                Action = [
                    "iam:*",
                    "ec2:*",
                    "ssm:*",
                    "rds:*",
                    "codedeploy:*",
                    "logs:*",
                    "elasticloadbalancing:*",
                    "autoscaling:*",
                    "route53:*",
                    "acm:*"
                ],
                Resource = "*"
            }
        ]
    })
}

resource "aws_iam_role_policy_attachment" "github_actions" {
    role       = aws_iam_role.github_actions.name
    policy_arn = aws_iam_policy.github_actions.arn
}
