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
