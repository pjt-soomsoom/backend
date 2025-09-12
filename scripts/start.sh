#!/bin/bash
# scripts/start.sh

# --- CodeDeploy가 전달한 환경 변수 파일 로드 ---
# 이 파일을 통해 GitHub Actions의 변수들을 사용할 수 있습니다.
if [ -f /home/ec2-user/app/env.vars ]; then
    source /home/ec2-user/app/env.vars
else
    echo "Error: Environment variable file not found." >&2
    exit 1
fi
# ---------------------------------------------

# ECR 리포지토리 주소
ECR_URI="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY"

# 1. AWS ECR에 로그인
# EC2 인스턴스 프로파일(IAM 역할)의 권한으로 ECR에 접근하므로 안전합니다.
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

# 2. 최신 Docker 이미지 PULL
docker pull "$ECR_URI:$IMAGE_TAG"

# 3. 기존에 실행 중인 컨테이너가 있다면 중지 및 삭제
if [ "$(docker ps -q -f name=$ECR_REPOSITORY)" ]; then
    docker stop $ECR_REPOSITORY
    docker rm $ECR_REPOSITORY
fi

# 4. 새 Docker 컨테이너 실행
docker run -d --name $ECR_REPOSITORY -p 8080:8080 "$ECR_URI:$IMAGE_TAG"
