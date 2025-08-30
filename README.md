# Backend
2025 신한은행 해커톤 알뜰한끼 팀 레포지토리입니다.

---
### 1. 개요

본 프로젝트는 다음과 같은 3개의 서버로 구성되어 있습니다.

- 헤이영 캠퍼스 서버 (Spring Boot + PostgreSQL)
- 쏠쏠한 한끼 서버 (Spring Boot + PostgreSQL)
- 땡겨요 서버 (Spring Boot + MongoDB)

배포는 Docker 컨테이너 기반으로 수행되며, GitHub Actions를 통한 CI/CD 자동화가 적용되어 있습니다. SSL 인증서는 Certbot을 이용하여 발급 및 적용하였고, 서버 라우팅은 Nginx Reverse Proxy를 통해 관리됩니다.

### 2. 개발환경
2. 개발 환경

- OS : Ubuntu 22.04 LTS
- Language : Java 17
- Framework : Spring Boot 3.x
- Build Tool : Gradle 8.x
- Database : PostgreSQL 15, MongoDB 6.x
- Container : Docker, Docker Compose
- Web Server : Nginx
- 인증서 : Certbot (Let’s Encrypt SSL)
- CI/CD : GitHub Actions
