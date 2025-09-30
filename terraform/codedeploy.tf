# CodeDeploy 애플리케이션 생성 (EC2/온프레미스용)
resource "aws_codedeploy_app" "app" {
    compute_platform = "Server"
    name             = "${var.project_name}-backend-${var.environment}"
}

# Blue/Green 배포를 위한 두 번째 대상 그룹(Green)을 추가로 생성합니다.
# Blue 그룹 역할은 main.tf에 정의된 aws_lb_target_group.main이 담당합니다.
resource "aws_lb_target_group" "green" {
    # 'prod' 환경에서만 생성되도록 count 사용
    count = var.environment == "prod" ? 1 : 0

    name     = "${var.project_name}-green-tg-${var.environment}"
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

    tags = {
        Name = "${var.project_name}-green-tg-${var.environment}"
    }
}

# CodeDeploy 배포 그룹 생성
resource "aws_codedeploy_deployment_group" "group" {
    app_name               = aws_codedeploy_app.app.name
    deployment_group_name  = "${var.project_name}-${var.environment}-deploy-group"
    service_role_arn       = aws_iam_role.codedeploy.arn

    # 'prod' 환경에서는 Auto Scaling 그룹을 배포 대상으로 지정합니다.
    autoscaling_groups = var.environment == "prod" ? [aws_autoscaling_group.prod[0].name] : []

    # 'test' 환경에서는 EC2 인스턴스 태그를 배포 대상으로 지정합니다.
    dynamic "ec2_tag_filter" {
        for_each = var.environment == "test" ? [1] : []
        content {
            key   = "Name"
            type  = "KEY_AND_VALUE"
            value = "${var.project_name}-ec2-${var.environment}"
        }
    }

    # 'prod' 환경에만 Blue/Green 배포 방식을 설정합니다.
    dynamic "blue_green_deployment_config" {
        for_each = var.environment == "prod" ? [1] : []
        content {
            deployment_ready_option {
                action_on_timeout = "CONTINUE_DEPLOYMENT"
            }
            terminate_blue_instances_on_deployment_success {
                action                           = "TERMINATE"
                termination_wait_time_in_minutes = 5
            }
        }
    }

    # 'prod' 환경에만 로드 밸런서 설정을 추가합니다.
    dynamic "load_balancer_info" {
        for_each = var.environment == "prod" ? [1] : []
        content {
            target_group_pair_info {
                prod_traffic_route {
                    listener_arns = [aws_lb_listener.https[0].arn]
                }
                target_group {
                    name = aws_lb_target_group.main[0].name
                }
                target_group {
                    name = aws_lb_target_group.green[0].name
                }
            }
        }
    }

    # 'test' 환경에만 IN_PLACE 배포 방식을 설정합니다.
    dynamic "deployment_style" {
        for_each = var.environment == "test" ? [1] : []
        content {
            deployment_option = "WITHOUT_TRAFFIC_CONTROL"
            deployment_type   = "IN_PLACE"
        }
    }
}

