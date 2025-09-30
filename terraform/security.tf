# --- alb 보안 그룹 ---

resource "aws_security_group" "alb" {
    count = var.environment == "prod" ? 1 : 0
    name = "${var.project_name}-alb-sg-${var.environment}"
    description = "Security group for the Application Load Balancer"
    vpc_id = aws_vpc.main.id

    ingress {
        description = "Allow HTTP traffic from anywhere"
        from_port = 80
        to_port = 80
        protocol = "tcp"
        cidr_blocks = ["0.0.0.0/0"]
    }

    ingress {
        description = "Allow HTTPS traffic from anywhere"
        from_port = 443
        to_port = 443
        protocol = "tcp"
        cidr_blocks = ["0.0.0.0/0"]
    }

    egress {
        from_port = 0
        to_port = 0
        protocol = "-1"
        cidr_blocks = ["0.0.0.0/0"]
    }

    tags = {
        Name = "${var.project_name}-alb-sg"
    }
}

# --- ec2용 보안 그룹 ---

resource "aws_security_group" "app" {
    name = "${var.project_name}-app-sg-${var.environment}"
    description = "Security group for the Application EC2 instances"
    vpc_id = aws_vpc.main.id

    ingress {
        description     = "Allow Spring Boot App traffic"
        from_port       = 8080
        to_port         = 8080
        protocol        = "tcp"
        security_groups = var.environment == "prod" ? [aws_security_group.alb[0].id] : null
        cidr_blocks     = var.environment == "test" ? ["0.0.0.0/0"] : null
    }

    ingress {
        description = var.environment == "test" ? 1 : 0
        from_port = 22
        to_port = 22
        protocol = "tcp"
        cidr_blocks = ["0.0.0.0/0"]
    }

    egress {
        from_port = 0
        to_port = 0
        protocol = "-1"
        cidr_blocks = ["0.0.0.0/0"]
    }
}

# --- RDS 보안 그룹 ---
resource "aws_security_group" "rds" {
    name = "${var.project_name}-rds-sg-${var.environment}"
    description = "Security group from the RDS instance"
    vpc_id = aws_vpc.main.id

    ingress {
        description = "Allow traffic from the application servers"
        from_port = 3306
        to_port = 3306
        protocol = "tcp"
        security_groups = [aws_security_group.app.id]
    }

    tags = {
        Name = "${var.project_name}-rds-sg"
    }
}
