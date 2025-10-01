#!/bin/bash

# [수정] GitHub Actions가 배포한 경로에서 env.vars 파일을 로드합니다.
if [ -f /home/ec2-user/app/env.vars ]; then
    source /home/ec2-user/app/env.vars
fi

# AWS CLI를 통해 현재 EC2 인스턴스가 속한 AWS 계정 ID를 가져옵니다.
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
AWS_REGION="ap-northeast-2"
# [제거] 하드코딩된 리포지토리 이름 대신 env.vars의 IMAGE_REPO_NAME을 사용합니다.
# ECR_REPOSITORY="soomsoom-backend"

# AWS ECR(Elastic Container Registry)에 로그인합니다.
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

# 이전에 실행 중이거나 멈춰있는(죽은) 애플리케이션 컨테이너가 있다면 삭제합니다.
EXISTING_CONTAINER_ID=$(docker ps -a -q -f name=soomsoom-backend)
if [ "$EXISTING_CONTAINER_ID" ]; then
    echo "Stopping and removing existing container: $EXISTING_CONTAINER_ID"
    docker stop $EXISTING_CONTAINER_ID
    docker rm $EXISTING_CONTAINER_ID
fi

echo "--- Docker Image Variables ---"
echo "AWS Account ID: ${AWS_ACCOUNT_ID}"
echo "AWS Region: ${AWS_REGION}"
echo "Image Repo Name: ${IMAGE_REPO_NAME}"
echo "Image Tag: ${IMAGE_TAG}"
echo "------------------------------"

# [수정] CI/CD에서 전달받은 $IMAGE_REPO_NAME 변수를 사용합니다.
docker pull $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$IMAGE_REPO_NAME:$IMAGE_TAG

# 새 Docker 컨테이너를 실행합니다.
DEPLOYMENT_ENV=$(echo $DEPLOYMENT_GROUP_NAME | cut -d'-' -f2)

docker run -d --name soomsoom-backend -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=$DEPLOYMENT_ENV \
  $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$IMAGE_REPO_NAME:$IMAGE_TAG # [수정] $IMAGE_REPO_NAME 사용
