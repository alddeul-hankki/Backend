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

### 3. 서버 구조
```
[Client] 
   ↓
[Nginx Reverse Proxy (443)]
   ├── /api         → 헤이영 캠퍼스 Server (Spring Boot + PostgreSQL)
   ├── /solsol      → 쏠쏠한 한끼 Server (Spring Boot + PostgreSQL)
   └── /ddangyo     → 땡겨요 Server (Spring Boot + MongoDB)

```


### 4. 빌드 및 실행 매뉴얼

**1. 프로젝트 클론 및 디렉토리 이동**
```bash
git clone <YOUR_REPOSITORY_URL>
cd <YOUR_REPOSITORY_DIRECTORY>
```
**2. Docker Compose 파일 준비**
```
heyyoung:
  image: {docker-username}/heyyoung
  container_name: heyyoung
  ports:
    - "8080:8080"
  environment:
    - POSTGRESQL_URL=********
    - POSTGRESQL_PORT=5432
    - POSTGRESQL_DB=heyyoung
    - POSTGRESQL_USER=postgres
    - POSTGRESQL_PASSWORD=********
    - FINANCIAL_BASE_URL=https://finopenapi.ssafy.io/ssafy
    - FINANCIAL_API_KEY=********
    - FINANCIAL_DEFAULT_ACCOUNT_TYPE=********
  networks:
    - sol-network
  restart: unless-stopped

nginx:
  image: nginx:alpine
  container_name: nginx
  volumes:
    - ./nginx.conf:/etc/nginx/nginx.conf
    - ./certbot/conf:/etc/letsencrypt
    - ./certbot/www:/var/www/certbot
  ports:
    - "80:80"
    - "443:443"
  networks:
    - sol-network
  depends_on:
    - heyyoung

certbot:
  image: certbot/certbot
  container_name: certbot
  volumes:
    - ./certbot/conf:/etc/letsencrypt
    - ./certbot/www:/var/www/certbot
  networks:
    - sol-network

ddangyo:
  image: {docker-username}/ddangyo
  container_name: ddangyo
  ports:
    - "8081:8081"
  environment:
    - MONGODB_USERNAME=********
    - MONGODB_PASSWORD=********

solsol:
  image: {docker-username}/solsol
  container_name: solsol
  ports:
    - "8080:8080"
  environment:
    - DEV_POSTGRES_URL=********
    - DEV_POSTGRES_USER=postgres
    - DEV_POSTGRES_PASSWORD=********
    - DEV_POSTGRES_DB=postgres
    - POSTGRES_PORT=5432
    - PROD_POSTGRES_URL=********
    - PROD_POSTGRES_DB=solsol
    - PROD_POSTGRES_USER=postgres
    - PROD_POSTGRES_PASSWORD=********
    - FINANCIAL_BASE_URL=https://finopenapi.ssafy.io/ssafy
    - FINANCIAL_API_KEY=********
    - FINANCIAL_DEFAULT_ACCOUNT_TYPE=********
    - FIREBASE_SERVICE_ACCOUNT_PATH=src/main/resources/firebase-service-account.json
    - DEV_DDANGYO_API_URL=http://localhost:8080
    - PROD_DDANGYO_API_URL=https://api.version-pulse.store/ddangyo
    - SOLSOL_CLIENT_URL=********
    - DEV_PAYMENT_API_URL=http://localhost:8081
    - PROD_PAYMENT_API_URL=https://api.version-pulse.store/
    - PAYMENT_CALLBACK_URL=https://api.version-pulse.store/sol/api/orders/payment/callback
    - PROD_CLUSTER_URL=********
  volumes:
    - ./fire-servcie-account.json:/src/main/resources/firebase-service-account.json
  networks:
    - sol-network
  restart: unless-stopped
```
**3. 네트워크 생성**
```
docker network create sol-network
```
**4. 빌드 및 실행**
```
docker compose up -d --build
```
