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
