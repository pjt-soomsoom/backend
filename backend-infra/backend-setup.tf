resource "aws_s3_bucket" "terraform_state" {
    bucket = "soomsoom-terraform-state-bucket"

    # 실수로 상태 파일이 삭제되는 것을 방지
    lifecycle {
        prevent_destroy = true
    }
}

resource "aws_s3_bucket_versioning" "terraform_state" {
    bucket = aws_s3_bucket.terraform_state.id
    versioning_configuration {
        status = "Enabled"
    }
}

resource "aws_dynamodb_table" "terraform_state_lock" {
    name         = "soomsoom-terraform-state-lock"
    billing_mode = "PAY_PER_REQUEST"
    hash_key     = "LockID"

    attribute {
        name = "LockID"
        type = "S"
    }
}
