data "aws_route53_zone" "main" {
    count = var.environment == "prod" ? 1 : 0
    name  = var.domain_name
}

resource "aws_acm_certificate" "main" {
    count                     = var.environment == "prod" ? 1 : 0
    domain_name               = var.domain_name
    subject_alternative_names = ["*.${var.domain_name}"]
    validation_method         = "DNS"
    lifecycle { create_before_destroy = true }
    tags = { Name = "${var.project_name}-cert" }
}

resource "aws_route53_record" "cert_validation" {
    for_each = var.environment == "prod" ? {
        for dvo in aws_acm_certificate.main[0].domain_validation_options : dvo.domain_name => {
            name   = dvo.resource_record_name
            record = dvo.resource_record_value
            type   = dvo.resource_record_type
        }
    } : {}

    allow_overwrite = true
    name            = each.value.name
    records         = [each.value.record]
    ttl             = 60
    type            = each.value.type
    zone_id         = data.aws_route53_zone.main[0].zone_id
}

resource "aws_acm_certificate_validation" "main" {
    count                   = var.environment == "prod" ? 1 : 0
    certificate_arn         = aws_acm_certificate.main[0].arn
    validation_record_fqdns = [for record in aws_route53_record.cert_validation : record.fqdn]
}

resource "aws_route53_record" "app" {
    count   = var.environment == "prod" ? 1 : 0
    zone_id = data.aws_route53_zone.main[0].zone_id
    name    = var.domain_name
    type    = "A"
    alias {
        name                   = aws_lb.main[0].dns_name
        zone_id                = aws_lb.main[0].zone_id
        evaluate_target_health = true
    }
}

resource "aws_route53_record" "www" {
    count   = var.environment == "prod" ? 1 : 0
    zone_id = data.aws_route53_zone.main[0].zone_id
    name    = "www.${var.domain_name}"
    type    = "A"
    alias {
        name                   = aws_lb.main[0].dns_name
        zone_id                = aws_lb.main[0].zone_id
        evaluate_target_health = true
    }
}
