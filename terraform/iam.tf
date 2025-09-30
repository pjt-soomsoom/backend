resource "aws_iam_role" "app" {
    name = "${var.project_name}-ec2-role-${var.environment}"
    assume_role_policy = jsonencode({
        version = "2012-10-17",
        Statement = [{
            Action = "sts:AssumeRole",
            Effect = "Allow",
            Principal = { Service = "ec2.amazonaws.com" }
        }]
    })
    tags = { Name = "${var.project_name}-ec2-role" }
}

resource "aws_iam_role_policy_attachment" "app_ssm" {
    role = aws_iam_role.app.name
    policy_arn = "arn:aws:iam::aws:policy/AmazonSSMReadOnlyAccess"
}

resource "aws_iam_role_policy_attachment" "app_cloudwatch" {
    role       = aws_iam_role.app.name
    policy_arn = "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"
}

resource "aws_iam_instance_profile" "app" {
    name = "${var.project_name}-ec2-profile-${var.environment}"
    role = aws_iam_role.app.name
}

resource "aws_iam_role" "codedeploy" {
    name = "${var.project_name}-codedeploy-role-${var.environment}"
    assume_role_policy = jsonencode({
        Version   = "2012-10-17",
        Statement = [{
            Action    = "sts:AssumeRole",
            Effect    = "Allow",
            Principal = { Service = "codedeploy.amazonaws.com" }
        }]
    })
}

resource "aws_iam_role_policy_attachment" "codedeploy" {
    role       = aws_iam_role.codedeploy.name
    policy_arn = "arn:aws:iam::aws:policy/service-role/AWSCodeDeployRole"
}

# --- Github Actions OIDC용 IAM 역할

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
            values   = ["repo:${var.github_repo}:environment:prod", "repo:${var.github_repo}:environment:test"]
        }
    }
}

resource "aws_iam_role" "github_actions" {
    name               = "${var.project_name}-github-actions-role"
    assume_role_policy = data.aws_iam_policy_document.github_actions_assume_role.json
    max_session_duration = 10800
}

resource "aws_iam_policy" "github_actions" {
    name   = "${var.project_name}-github-actions-policy"
    policy = jsonencode({
        Version = "2012-10-17",
        Statement = [
            { Effect = "Allow", Action = ["ecr:GetAuthorizationToken", "ecr:BatchCheckLayerAvailability", "ecr:CompleteLayerUpload", "ecr:InitiateLayerUpload", "ecr:PutImage", "ecr:UploadLayerPart"], Resource = "*" },
            { Effect = "Allow", Action = ["s3:PutObject"], Resource = ["arn:aws:s3:::${var.project_name}-test-bucket/*", "arn:aws:s3:::${var.project_name}-prod-bucket/*"] },
            { Effect = "Allow", Action = ["codedeploy:CreateDeployment"], Resource = "*" },
            {
                Effect = "Allow",
                Action = [
                    "s3:GetObject",
                    "s3:PutObject",
                    "s3:DeleteObject"
                ],
                Resource = "arn:aws:s3:::soomsoom-terraform-state-bucket/*"
            }
        ]
    })
}

resource "aws_iam_role_policy_attachment" "github_actions" {
    role       = aws_iam_role.github_actions.name
    policy_arn = aws_iam_policy.github_actions.arn
}
