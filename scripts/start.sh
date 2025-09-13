#!/bin/bash
# scripts/start.sh

# --- CodeDeploy가 전달한 환경 변수 파일 로드 ---
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
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

# 2. 최신 Docker 이미지 PULL
docker pull "$ECR_URI:$IMAGE_TAG"

# 3. 기존 컨테이너 확인 및 삭제
EXISTING_CONTAINER_ID=$(docker ps -a -q -f name=$ECR_REPOSITORY)

if [ "$EXISTING_CONTAINER_ID" ]; then
    echo "Stopping and removing existing container: $EXISTING_CONTAINER_ID"
    docker stop $EXISTING_CONTAINER_ID
    docker rm $EXISTING_CONTAINER_ID
fi

# 4. 새 Docker 컨테이너 실행
echo "Starting new container..."
docker run -d --name $ECR_REPOSITORY -p 8080:8080 "$ECR_URI:$IMAGE_TAG"
