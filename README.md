# SoomSoom Backend
<div align="center">
  <img src="https://github.com/user-attachments/assets/4e48ff49-d117-4e47-b9f4-f12393618fa4" height="550">
</div>

<br> <br/>
## 📘 프로젝트 개요
#### 🚚 [App Stroe 에서 다운받기](https://apps.apple.com/kr/app/%EC%88%A8%EC%88%A8/id6752624555)
### 프로젝트 소개
**숨숨**은 사용자의 마음 챙김 활동을 돕는 모바일 애플리케이션의 백엔드 시스템입니다. 명상, 호흡 운동, 감정 일기 기록 등 다양한 기능을 통해 사용자의 꾸준한 활동을 유도하고, 게임화(Gamification) 요소를 도입하여 동기를 부여합니다.
- 주요 목표: 사용자가 지속적으로 마음 챙김 활동에 참여하도록 지원하고 긍정적인 경험을 제공합니다.
- 핵심 가치: 안정성, 확장성, 유지보수성을 고려한 설계 및 구현.

<br> <br/>
## 💡 기술 스택

##### Language & Frameworks
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=square&logo=kotlin&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring%20boot-%236DB33F.svg?style=square&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=square&logo=Spring&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-007396?style=square&logo=openjdk&logoColor=white)

##### Security & Auth
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=square&logo=Spring%20Security&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=square&logo=JSON%20web%20tokens)
![Google OAuth](https://img.shields.io/badge/Google-4285F4?style=square&logo=google&logoColor=white)
![Apple OAuth](https://img.shields.io/badge/Apple-%23000000?style=square&logo=apple&logoColor=white)

##### Database & Distributed System
![MySQL](https://img.shields.io/badge/MySQL-4479A1.svg?style=square&logo=mysql&logoColor=white)
![AWS RDS](https://img.shields.io/badge/AWS%20RDS-527fff.svg?style=square&logo=amazonrds&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200.svg?style=square&logo=Flyway&logoColor=white)
![ShedLock](https://img.shields.io/badge/ShedLock-E65100?style=square&logo=spring&logoColor=white)

##### Infrastructure & Cloud
![EC2](https://img.shields.io/badge/AWS%20EC2-FF9900.svg?style=square&logo=amazonec2&logoColor=white)
![S3](https://img.shields.io/badge/AWS%20S3-569A31.svg?style=square&logo=amazons3&logoColor=white)
![ALB](https://img.shields.io/badge/AWS%20ALB-8c4fff.svg?style=square&logo=awselasticloadbalancing&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=square&logo=firebase&logoColor=black)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=square&logo=docker&logoColor=white)
![Terraform](https://img.shields.io/badge/terraform-%235835CC.svg?style=square&logo=terraform&logoColor=white)

##### Tools & DevOps
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=square&logo=Gradle&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=square&logo=githubactions&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=square&logo=swagger&logoColor=white)
![Ktlint](https://img.shields.io/badge/Ktlint-6B5B95?style=square&logo=kotlin&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000.svg?style=square&logo=intellij-idea&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=square&logo=postman&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=square&logo=notion&logoColor=white)

<br> <br/>

## 🗺️  아키텍처 및 인프라 (Architecture & Infrastructure)

### 👉  &nbsp; 애플리케이션 아키텍처 (Application Architecture)

> 헥사고날 아키텍처 (Hexagonal Architecture): 비즈니스 로직(Domain, Application)과 외부 기술(Adapter)을 명확히 분리하여 유연성, 테스트 용이성, 유지보수성을 확보
- Domain: 순수 비즈니스 모델과 로직.
  
- Application: UseCase(Input Port), 외부 연동 인터페이스(Output Port).
  
- Adapter: Web API, Scheduler(Input), Persistence, S3, FCM(Output).

> 이벤트 기반 아키텍처 (Event-Driven Architecture): Spring Event를 활용하여 도메인 간 결합도를 낮추고 비동기 처리 및 트랜잭션 분리를 구현.
- @TransactionalEventListener: 트랜잭션 완료 후 이벤트 처리를 보장 (e.g., 업적 갱신).
  
- @Async 및 전용 스레드 풀 (AsyncConfig): 알림 발송 등 시간이 소요되는 작업을 비동기 처리하여 응답성을 개선하고, 스레드 수를 관리하여 안정성을 높임.
  
- Payload, NotificationPayload 인터페이스/추상 클래스를 활용하여 이벤트 페이로드 구조를 표준화하고, ResolvableTypeProvider로 제네릭 타입 소거 문제를 해결.


### 👉  &nbsp;인프라 설계도 (Infrastructure Diagram)

<img width="1297" height="826" alt="image" src="https://github.com/user-attachments/assets/19f2e8a5-e39a-4c29-91c8-b63b412bafce" />




* AWS Cloud: EC2 (ASG), RDS (MySQL Multi-AZ), S3, ALB, ECR, CodeDeploy 등을 활용하여 가용성과 확장성을 고려한 인프라를 구축.
  
* AWS Systems Manager (SSM) Parameter Store: DB 자격 증명, API 키 등 민감한 환경 설정 정보를 안전하게 관리하고 배포 시점에 주입.
  
* Terraform (IaC): 인프라 구성을 코드로 관리하여 환경별(prod/test) 분리 및 재사용성을 높였으며, S3 Backend로 상태(.tfstate)를 관리.
  
* CI/CD: GitHub Actions를 통해 코드 Push 시 자동 빌드, Docker 이미지 생성/ECR Push, AWS CodeDeploy를 이용한 Blue/Green 무중단 배포 파이프라인을 구축.
  
* Database Management (Flyway): DB 스키마 변경 이력을 코드로 관리하고, 배포 시 자동으로 마이그레이션을 수행하여 DB 버전 관리를 자동화.
  
* Scheduler Lock (ShedLock): 다중 인스턴스 환경에서 스케줄링 작업(e.g., 알림 발송)이 중복 실행되지 않도록 분산 락(Distributed Lock)을 적용.

<br> <br/>

## ✨ 주요 기능 및 구현 상세 (Key Features & Implementation Details)

> 1. 인증 및 인가 (Authentication & Authorization)

- 인증: JWT (Access/Refresh), 익명(Device ID), 소셜(Google/Apple OAuth 2.0), 관리자(ID/PW) 등 다양한 인증 방식을 통합적으로 지원.

- 인가: Spring Security 기반으로 URL 패턴 및 @PreAuthorize/@PostAuthorize를 활용한 메서드 레벨 권한 제어를 구현하여 보안을 강화.

  <br> <br/>

> 2. 이벤트 기반 시스템 (Event-Driven System)

- 설계: ApplicationEventPublisher, @EventListener를 사용하여 도메인 간 의존성을 제거

- 활용: 회원가입 후 초기 아이템 지급, 활동 완료/일기 작성 시 업적/미션 갱신, 알림 발송 등의 로직을 이벤트로 처리하여 서비스 응답 속도를 높이고 트랜잭션을 효율적으로 관리

- 안정성: NotificationPayload 등의 표준화된 DTO를 사용하고, 타입 추론 문제를 해결하여 런타임 오류를 방지

  <br> <br/>

> 3. 사용자 활동 추적 및 활용 (User Activity Tracking)

- 로그 수집: 사용자의 접속 기록(ConnectionLog), 화면별 체류 시간(ScreenTimeLog), 콘텐츠 소비 진행률(ActivityProgress, 음원 재생 위치)을 정밀하게 기록

- 이어보기: ActivityProgress를 기반으로 사용자가 중단한 지점부터 콘텐츠를 이어볼 수 있는 UX를 제공

- 보상 연동: 누적 체류 시간 및 활동 완료 기록을 실시간으로 집계하여 업적 달성 및 미션 완료 여부를 판단하고 보상을 지급

  <br> <br/>

> 4. 알림 서비스 (Notification Service)

- 구현: FCM을 이용한 Push Notification 시스템을 구축

- 전략 패턴: 알림 타입(업적, 공지, 리마인더 등)별 발송 로직을 NotificationStrategy로 분리하여 확장성을 확보

- 스케줄링: ShedLock을 적용한 NotificationScheduler를 통해 분산 서버 환경에서도 중복 발송 없이 안정적인 예약 알림을 수행

- A|B 테스트 : 알림 발송 시 여러 메시지를 등록하여 랜덤하게 발송. 이후 각 메시지마다 클릭 및 전환율 분석

  <br> <br/>
  
> 5. Google AdMob 보상 검증 (AdMob Verification)

- 중복 방지: AdMob SSV 콜백의 transactionId와 사용자 ID를 조합하여 AdRewardLog에 기록함으로써, 동일한 광고 시청에 대한 중복 보상 지급을 원천 차단

- 보안: Google 공개 키를 이용해 콜백 요청의 서명(Signature)을 검증하여 위변조된 요청을 필터링

  <br> <br/>

> 6. 비즈니스 시간 유틸리티 (Business Time Utility)

- 배경: 서비스 정책상 하루의 시작과 끝을 일반적인 자정(00:00)이 아닌 오전 06:00 기준으로 정의

- 구현: DateHelper 유틸리티 클래스를 개발하여 비즈니스 날짜 기준의 시간 계산을 일원화. 이를 DB 쿼리 조건(e.g., 일일 미션, 출석 체크)에 일관되게 적용하여 데이터 정확성을 높임

  <br> <br/>

> 7. 인프라 및 배포 (Infra & DevOps)

- IaC: Terraform을 통해 AWS 리소스를 모듈화하여 관리하고, 변경 사항을 추적

- CI/CD: GitHub Actions와 AWS CodeDeploy를 연동하여 커밋부터 배포까지의 과정을 자동화했으며, Blue/Green 전략으로 무중단 배포를 실현

- 버전 관리: Flyway를 통해 애플리케이션 코드와 DB 스키마 버전을 동기화하여 배포 안정성을 확보

  <br> <br/>

> 8. 파일 업로드 최적화 (File Upload)

- S3 Pre-signed URL: 클라이언트 직접 업로드 방식을 채택하여 서버 리소스를 절약하고 대용량 파일 처리 성능을 개선

- 정합성 검증: 업로드 완료 후 파일 존재 여부를 확인하는 로직을 추가하여 DB 데이터와 실제 파일 간의 정합성을 보장

  <br> <br/>

> 9. 모니터링 (Monitoring)
- Actuator : Spring Actuator를 사용하여 애플리케이션에 대한 health check 진행
  
- CloudWatch: AWS CloudWatch Agent를 통해 memory와 disk 사용량 수집 및 모니터링 진행 (비용 최적화)
