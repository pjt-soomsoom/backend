resource "aws_launch_template" "main" {
    name_prefix = "${var.project_name}-${var.environment}-"
    image_id = "ami-0c9c942bd7bf113a2" # Amazon Linux 2023 AMI(서울 리전 기준)
    instance_type = var.environment == "prod" ? "t4g.small" : "t2.micro"

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

# --- 로드 밸런서 ---
resource "aws_lb" "main" {
    count              = var.environment == "prod" ? 1 : 0
    name               = "${var.project_name}-alb-${var.environment}"
    internal           = false
    load_balancer_type = "application"
    security_groups    = [aws_security_group.alb[0].id]
    subnets            = aws_subnet.public[*].id
    tags               = { Name = "${var.project_name}-alb" }
}

resource "aws_lb_target_group" "main" {
    count    = var.environment == "prod" ? 1 : 0
    name     = "${var.project_name}-tg-${var.environment}"
    port     = 8080
    protocol = "HTTP"
    vpc_id   = aws_vpc.main.id

    health_check {
        path                = "/actuator/health"
        protocol            = "HTTP"
        matcher             = "200"
        interval            = 30
        timeout             = 5
        healthy_threshold   = 2
        unhealthy_threshold = 2
    }
}

resource "aws_lb_listener" "http" {
    count             = var.environment == "prod" ? 1 : 0
    load_balancer_arn = aws_lb.main[0].arn
    port              = "80"
    protocol          = "HTTP"

    default_action {
        type = "redirect"
        redirect {
            port        = "443"
            protocol    = "HTTPS"
            status_code = "HTTP_301"
        }
    }
}

resource "aws_lb_listener" "https" {
    count             = var.environment == "prod" ? 1 : 0
    load_balancer_arn = aws_lb.main[0].arn
    port              = "443"
    protocol          = "HTTPS"
    ssl_policy        = "ELBSecurityPolicy-2016-08"
    certificate_arn   = aws_acm_certificate.main[0].arn

    default_action {
        type             = "forward"
        target_group_arn = aws_lb_target_group.main[0].arn
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
