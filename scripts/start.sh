#!/bin/bash
# scripts/start.sh

# 변수 설정
AWS_REGION=$(aws ec2 get-instance-identity-document --query region --output text)
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ECR_REPOSITORY="soomsoom-backend"
IMAGE_TAG=${GITHUB_SHA:-"latest"} # GitHub Actions에서 주입될 커밋 해시 또는 기본값 latest

# ECR 리포지토리 주소
ECR_URI="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY"

# 1. AWS ECR에 로그인
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

# 2. 최신 Docker 이미지 PULL
docker pull "$ECR_URI:$IMAGE_TAG"

# 3. 기존에 실행 중인 컨테이너가 있다면 중지 및 삭제
if [ "$(docker ps -q -f name=$ECR_REPOSITORY)" ]; then
    docker stop $ECR_REPOSITORY
    docker rm $ECR_REPOSITORY
fi

# 4. 새 Docker 컨테이너 실행
# 환경 변수는 JAR 파일 안의 application.properties에 이미 포함되어 있으므로
# docker run 명령어에서는 포트만 설정해주면 됩니다.
docker run -d --name $ECR_REPOSITORY -p 8080:8080 "$ECR_URI:$IMAGE_TAG"
