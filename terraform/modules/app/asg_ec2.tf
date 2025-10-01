resource "aws_launch_template" "main" {
    name_prefix   = "${var.project_name}-${var.environment}-"
    # arm64 아키텍처용 최신 Amazon Linux 2023 AMI (서울 리전 기준)
    image_id      = "ami-02cf627e019f25aeb"
    # test와 prod 환경 모두 t4g.small 인스턴스 타입을 사용하도록 통일합니다.
    instance_type = "t4g.small"

    vpc_security_group_ids = [aws_security_group.app.id]

    iam_instance_profile {
        arn = aws_iam_instance_profile.app.arn
    }

    user_data = base64encode(<<-EOF
              #!/bin/bash
              yum update -y
              yum install -y ruby wget
              cd /home/ec2-user
              wget https://aws-codedeploy-ap-northeast-2.s3.ap-northeast-2.amazonaws.com/latest/install
              chmod +x ./install
              ./install auto
              systemctl start codedeploy-agent
              systemctl enable codedeploy-agent
              yum install -y docker
              systemctl start docker
              systemctl enable docker
              usermod -a -G docker ec2-user
              EOF
    )


    tags = {
        Name = "${var.project_name}-lt-${var.environment}"
    }
}

resource "aws_autoscaling_group" "prod" {
    count = var.environment == "prod" ? 1 : 0

    name                = "${var.project_name}-asg-prod"
    desired_capacity    = 2
    min_size            = 2
    max_size            = 4
    vpc_zone_identifier = aws_subnet.private[*].id

    launch_template {
        id      = aws_launch_template.main.id
        version = "$Latest"
    }

    target_group_arns = [aws_lb_target_group.main[0].arn]
    health_check_type         = "ELB"
    health_check_grace_period = 300

    tag {
        key                 = "Name"
        value               = "${var.project_name}-ec2-prod"
        propagate_at_launch = true
    }
}

resource "aws_instance" "test" {
    count = var.environment == "test" ? 1 : 0

    launch_template {
        id      = aws_launch_template.main.id
        version = "$Latest"
    }
    subnet_id = aws_subnet.public[0].id
    tags = {
        Name = "${var.project_name}-ec2-test"
    }
}

