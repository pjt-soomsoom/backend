#!/bin/bash

# CodeDeploy 에이전트에 의해 생성된 환경 변수 파일을 로드합니다.
# 이 파일 안에는 우리가 GitHub Actions에서 전달한 IMAGE_TAG 값이 들어있습니다.
if [ -f /etc/codedeploy-agent/deployment-root/env.vars ]; then
    source /etc/codedeploy-agent/deployment-root/env.vars
fi

# AWS CLI를 통해 현재 EC2 인스턴스가 속한 AWS 계정 ID를 가져옵니다.
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
AWS_REGION="ap-northeast-2"
ECR_REPOSITORY="soomsoom-backend"

# AWS ECR(Elastic Container Registry)에 로그인합니다.
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

# 이전에 실행 중이거나 멈춰있는(죽은) 애플리케이션 컨테이너가 있다면 삭제합니다.
# docker ps -a : 실행 여부와 관계없이 모든 컨테이너를 찾습니다.
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

# CI/CD 파이프라인에서 빌드하고 푸시한 새로운 버전의 Docker 이미지를 pull 합니다.
docker pull $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG

# 새 Docker 컨테이너를 실행합니다.
# ✅ 핵심: CodeDeploy가 자동으로 주입해주는 환경 변수($DEPLOYMENT_GROUP_NAME)를 사용하여
#       스프링 부트 프로파일을 동적으로 활성화시킵니다.
#       (예: 배포 그룹 이름이 'soomsoom-prod-deploy-group'이면, 'prod' 프로파일이 활성화됨)
DEPLOYMENT_ENV=$(echo $DEPLOYMENT_GROUP_NAME | cut -d'-' -f2) # 'soomsoom-prod-deploy-group' -> 'prod' 추출

docker run -d --name soomsoom-backend -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=$DEPLOYMENT_ENV \
  $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG

