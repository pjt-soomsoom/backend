data "aws_availability_zones" "available" {}

resource "aws_vpc" "main" {
    cidr_block = "10.0.0.0/16"
    enable_dns_support = true
    enable_dns_hostnames = true

    tags = {
        Name = "${var.project_name}-vpc-${var.environment}"
    }
}

resource "aws_internet_gateway" "main" {
    vpc_id = aws_vpc.main.id
    tags = {
        Name = "${var.project_name}-igw-${var.environment}"
    }
}

resource "aws_subnet" "public" {
    count = 2
    vpc_id = aws_vpc.main.id
    cidr_block = cidrsubnet(aws_vpc.main.cidr_block, 8, 10 + count.index)
    availability_zone = data.aws_availability_zones.available.names[count.index]
    map_public_ip_on_launch = true

    tags = {
        Name = "${var.project_name}-public-subnet-${data.aws_availability_zones.available.names[count.index]}"
    }
}

resource "aws_subnet" "private" {
    count = 2
    vpc_id = aws_vpc.main.id
    cidr_block = cidrsubnet(aws_vpc.main.cidr_block, 8, 20 + count.index)
    availability_zone = data.aws_availability_zones.available.names[count.index]

    tags = {
        Name = "${var.project_name}-private-subnet-${data.aws_availability_zones.available.names[count.index]}"
    }
}

# 라우팅 테이블
resource "aws_route_table" "public" {
    vpc_id = aws_vpc.main.id

    route {
        cidr_block ="0.0.0.0/0"
        gateway_id = aws_internet_gateway.main.id
    }

    tags = {
        Name = "${var.project_name}-puiblic-rt-${var.environment}"
    }
}

resource "aws_route_table_association" "public" {
    count = length(aws_subnet.public)
    subnet_id = aws_subnet.public[count.index].id
    route_table_id = aws_route_table.public.id
}


# RDS

resource "aws_db_subnet_group" "main" {
    name = "${var.project_name}-db-subnet-group-${var.environment}"
    subnet_ids = aws_subnet.private[*].id

    tags = {
        Name = "${var.project_name}-db-subnet-group-${var.environment}"
    }
}

resource "aws_db_instance" "main" {
    identifier = "${var.project_name}-db-${var.environment}"

    engine = "mysql"
    engine_version = "8.0"

    instance_class = var.environment == "prod" ? "db.t3.small" : "db.t3.micro"

    allocated_storage = 20
    storage_type = "gp2"

    db_name = "${var.project_name}db"
    username = "admin"
    password = var.db_password

    db_subnet_group_name = aws_db_subnet_group.main.id
    vpc_security_group_ids = [aws_security_group.rds.id]

    skip_final_snapshot = var.environment == "prod" ? false : true
    publicly_accessible = false
}
