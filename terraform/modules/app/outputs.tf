output "vpc_id" {
    description = "The ID of the VPC"
    value       = aws_vpc.main.id
}

output "app_security_group_id" {
    description = "The ID of the application security group"
    value       = aws_security_group.app.id
}

output "alb_dns_name" {
    description = "The DNS name of the ALB"
    value       = var.environment == "prod" ? aws_lb.main[0].dns_name : ""
}

output "alb_zone_id" {
    description = "The zone ID of the ALB"
    value       = var.environment == "prod" ? aws_lb.main[0].zone_id : ""
}
