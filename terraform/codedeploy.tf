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

    # Auto Scaling 그룹을 배포 대상으로 지정 (prod 환경)
    autoscaling_groups = var.environment == "prod" ? [aws_autoscaling_group.prod[0].name] : []

    # EC2 인스턴스를 태그 기준으로 타겟팅 (test 환경)
    ec2_tag_filter {
        key   = "Name"
        type  = "KEY_AND_VALUE"
        value = "${var.project_name}-ec2-${var.environment}"
    }

    # 배포 방식 설정 (블루/그린) - 'prod' 환경에서만 적용
    blue_green_deployment_config {
        deployment_ready_option {
            action_on_timeout = "CONTINUE_DEPLOYMENT"
        }
        terminate_blue_instances_on_deployment_success {
            action                           = "TERMINATE"
            termination_wait_time_in_minutes = 5 # 새 버전(Green)으로 전환 후 이전 버전(Blue) 인스턴스를 종료하기까지 대기 시간
        }
    }

    # 로드 밸런서 설정 (Blue/Green 배포에 필수)
    # 'prod' 환경에서 생성된 리소스들을 참조합니다.
    load_balancer_info {
        target_group_pair_info {
            prod_traffic_route {
                # main.tf에 정의된 HTTPS 리스너를 참조
                listener_arns = [aws_lb_listener.https[0].arn]
            }
            target_group {
                # main.tf에 정의된 기본 대상 그룹을 Blue로 사용
                name = aws_lb_target_group.main[0].name
            }
            target_group {
                # 이 파일에서 새로 정의한 대상 그룹을 Green으로 사용
                name = aws_lb_target_group.green[0].name
            }
        }
    }

    dynamic "load_balancer_info" {
        for_each = var.environment == "test" ? [] : [1]
        content {}
    }

    # 'test' 환경에서는 롤링 업데이트와 같은 기본 배포 전략을 사용합니다.
    deployment_style {
        deployment_option = "WITHOUT_TRAFFIC_CONTROL"
        deployment_type   = "IN_PLACE"
    }
}

