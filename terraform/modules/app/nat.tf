
resource "aws_eip" "nat" {
    # NAT Gateway는 VPC 안에 있어야 하므로 count로 prod 환경에서만 생성되도록 제어
    count = var.environment == "prod" ? 1 : 0

    # depends_on을 통해 인터넷 게이트웨이가 먼저 생성되도록 순서를 보장
    depends_on = [aws_internet_gateway.main]

    tags = {
        Name = "${var.project_name}-nat-eip-${var.environment}"
    }
}

# --- NAT Gateway ---
# Private Subnet의 인스턴스들이 외부와 통신할 수 있는 통로 역할을 합니다.
resource "aws_nat_gateway" "main" {
    # NAT Gateway는 EIP와 Public Subnet이 필요하므로 prod 환경에서만 생성
    count = var.environment == "prod" ? 1 : 0

    allocation_id = aws_eip.nat[0].id
    subnet_id     = aws_subnet.public[0].id # Public Subnet 중 첫 번째에 생성합니다.

    tags = {
        Name = "${var.project_name}-nat-gw-${var.environment}"
    }
}

# --- Private Route Table ---
# Private Subnet을 위한 전용 라우팅 테이블을 생성
resource "aws_route_table" "private" {
    # Private Subnet이 있는 prod 환경에서만 생성
    count = var.environment == "prod" ? 1 : 0

    vpc_id = aws_vpc.main.id

    route {
        cidr_block     = "0.0.0.0/0"
        nat_gateway_id = aws_nat_gateway.main[0].id
    }

    tags = {
        Name = "${var.project_name}-private-rt-${var.environment}"
    }
}

# --- Associate Private Route Table with Private Subnets ---
# 생성한 Private 라우팅 테이블을 모든 Private Subnet에 연결
resource "aws_route_table_association" "private" {
    count = var.environment == "prod" ? length(aws_subnet.private) : 0

    subnet_id      = aws_subnet.private[count.index].id
    route_table_id = aws_route_table.private[0].id
}
