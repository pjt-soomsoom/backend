resource "aws_iam_role" "app" {
    name = "${var.project_name}-ec2-role-${var.environment}"
    assume_role_policy = jsonencode({
        Version = "2012-10-17",
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

resource "aws_iam_role_policy_attachment" "app_ecr" {
    role       = aws_iam_role.app.name
    policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
}

resource "aws_iam_policy" "s3_write_policy" {
    name        = "${var.project_name}-s3-write-policy-${var.environment}"
    description = "Allow PutObject to a specific S3 bucket"

    policy = jsonencode({
        Version = "2012-10-17",
        Statement = [
            {
                Action   = [
                    "s3:GetObject",
                    "s3:PutObject",
                    "s3:DeleteObject"
                ]
                Effect   = "Allow",
                Resource = "arn:aws:s3:::soomsoom-${var.environment}-bucket/*" # <-- 여기에 실제 버킷 이름을 입력하세요.
            }
        ]
    })
}

# 2. 위에서 생성한 S3 쓰기 정책을 EC2 역할(aws_iam_role.app)에 연결합니다.
resource "aws_iam_role_policy_attachment" "app_s3_write" {
    role       = aws_iam_role.app.name
    policy_arn = aws_iam_policy.s3_write_policy.arn
}
