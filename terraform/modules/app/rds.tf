resource "aws_db_subnet_group" "main" {
    name       = "${var.project_name}-db-subnet-group-${var.environment}"
    subnet_ids = aws_subnet.private[*].id

    tags = {
        Name = "${var.project_name}-db-subnet-group-${var.environment}"
    }
}

resource "aws_db_instance" "main" {
    identifier = "${var.project_name}-db-${var.environment}"

    engine         = "mysql"
    engine_version = "8.0"

    instance_class = var.environment == "prod" ? "db.t3.small" : "db.t3.micro"

    allocated_storage = 20
    storage_type      = "gp2"

    db_name  = "${var.project_name}db"
    username = "admin"
    password = var.db_password

    db_subnet_group_name   = aws_db_subnet_group.main.name
    vpc_security_group_ids = [aws_security_group.rds.id]

    skip_final_snapshot = var.environment == "prod" ? false : true
    publicly_accessible = false
}
