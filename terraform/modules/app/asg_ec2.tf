locals {
    instance_type = var.environment == "test" ? "t4g.micro" : "t4g.small"
}

resource "aws_launch_template" "main" {
    name_prefix   = "${var.project_name}-${var.environment}-"
    image_id      = "ami-024bf8a1313cb6b6e"
    instance_type = local.instance_type

    vpc_security_group_ids = [aws_security_group.app.id]

    iam_instance_profile {
        arn = aws_iam_instance_profile.app.arn
    }

    user_data = base64encode(<<-EOF
    #!/bin/bash
    yum update -y

    # CodeDeploy Agent 설치
    yum install -y ruby wget
    cd /home/ec2-user
    wget https://aws-codedeploy-ap-northeast-2.s3.ap-northeast-2.amazonaws.com/latest/install
    chmod +x ./install
    ./install auto
    systemctl start codedeploy-agent
    systemctl enable codedeploy-agent

    # Docker 설치
    yum install -y docker
    systemctl start docker
    systemctl enable docker
    usermod -a -G docker ec2-user

    cd /tmp
    wget https://s3.amazonaws.com/amazoncloudwatch-agent/amazon_linux/arm64/latest/amazon-cloudwatch-agent.rpm
    yum install -y ./amazon-cloudwatch-agent.rpm

    cat > /opt/aws/amazon-cloudwatch-agent/etc/config.json <<'CONFIGEOF'
    {
      "metrics": {
        "metrics_collected": {
          "mem": {
            "measurement": [
              "mem_used_percent"
            ],
            "metrics_collection_interval": 60
          }
        },
        "append_dimensions": {
          "AutoScalingGroupName": "$${aws:AutoScalingGroupName}",
          "InstanceId": "$${aws:InstanceId}",
          "InstanceType": "$${aws:InstanceType}"
        }
      }
    }
    CONFIGEOF

    /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
      -a fetch-config \
      -m ec2 \
      -c file:/opt/aws/amazon-cloudwatch-agent/etc/config.json \
      -s
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

    target_group_arns         = [aws_lb_target_group.main[0].arn]
    health_check_type         = "ELB"
    health_check_grace_period = 300

    tag {
        key                 = "Name"
        value               = "${var.project_name}-ec2-prod"
        propagate_at_launch = true
    }

    depends_on = [aws_internet_gateway.main]
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

    depends_on = [aws_internet_gateway.main]
}
