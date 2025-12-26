# 중고마켓 - Backend

중고거래 플랫폼의 백엔드 저장소입니다.

## 배포 사이트

- **Frontend**: [https://main.d32flqff9mmre3.amplifyapp.com](https://main.d32flqff9mmre3.amplifyapp.com)
- **Backend API**: [https://trade-jinyong.duckdns.org](https://trade-jinyong.duckdns.org)

## 기술 스택

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Security** - 인증/인가
- **Spring Data JPA** - ORM
- **Spring WebSocket** - 실시간 채팅
- **OAuth2 Client** - 소셜 로그인
- **MySQL** - 데이터베이스 (AWS RDS)
- **AWS S3** - 이미지 저장소
- **AWS EC2** - 서버 호스팅

## 주요 기능

### 인증 시스템
- 세션 기반 로그인/로그아웃
- 소셜 로그인 (Google, Naver)
- Spring Security를 통한 보안 처리

### 게시글 (중고거래)
- CRUD 기능
- 이미지 업로드 (S3)
- 페이징 및 정렬
- 조회수 관리

### 댓글 시스템
- 게시글 댓글 CRUD
- 작성자 정보 연동

### 좋아요 기능
- 게시글 좋아요/취소
- 좋아요 수 조회

### 실시간 채팅
- WebSocket + STOMP 프로토콜
- 1:1 채팅방 생성
- 메시지 저장 및 조회

### 지갑 시스템
- 사용자 잔액 관리
- 충전 (토스페이먼츠 연동)
- 출금 신청
- 거래 내역 관리

## 프로젝트 구조

```
src/main/java/com/jinyong/trade/
├── config/                 # 설정 클래스
├── controller/
│   ├── AuthcheckController.java
│   ├── ChatController.java
│   ├── ChatMessageController.java
│   ├── CommentController.java
│   ├── LikeController.java
│   ├── LoginController.java
│   ├── LogoutController.java
│   ├── OAuth2Controller.java
│   ├── PaymentController.java
│   ├── PostController.java
│   ├── RegisterController.java
│   └── WalletController.java
├── dto/                    # 데이터 전송 객체
├── entity/
│   ├── User.java
│   ├── Post.java
│   ├── Comment.java
│   ├── Like.java
│   ├── ChatRoom.java
│   ├── ChatMessage.java
│   ├── Wallet.java
│   └── Transaction.java
├── jwt/                    # JWT 관련
├── repository/             # JPA Repository
├── service/                # 비즈니스 로직
└── TradeApplication.java
```

## API 엔드포인트

### 인증
- `POST /api/login` - 로그인
- `POST /api/logout` - 로그아웃
- `GET /api/auth/check` - 인증 상태 확인
- `POST /api/register` - 회원가입

### 게시글
- `GET /api/posts` - 목록 조회 (페이징)
- `GET /api/posts/{id}` - 상세 조회
- `POST /api/posts` - 등록
- `PUT /api/posts/{id}` - 수정
- `DELETE /api/posts/{id}` - 삭제
- `POST /api/posts/upload` - 이미지 업로드

### 댓글
- `GET /api/posts/{id}/comments` - 댓글 목록
- `POST /api/posts/{id}/comments` - 댓글 등록
- `DELETE /api/comments/{id}` - 댓글 삭제

### 좋아요
- `POST /api/posts/{id}/like` - 좋아요 토글
- `GET /api/posts/{id}/like` - 좋아요 상태 조회

### 채팅
- `GET /api/chat/rooms` - 채팅방 목록
- `POST /api/chat/rooms` - 채팅방 생성
- `GET /api/chat/rooms/{id}/messages` - 메시지 조회
- `WebSocket /ws` - 실시간 메시지

### 지갑
- `GET /api/wallet` - 잔액 조회
- `GET /api/wallet/transactions` - 거래 내역
- `POST /api/payment/prepare` - 결제 준비
- `POST /api/payment/confirm` - 결제 승인
- `POST /api/wallet/withdraw` - 출금 신청

## 설치 및 실행

### 요구사항
- Java 17+
- MySQL 8.0+

### 실행 방법

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun

# 또는 JAR 실행
java -jar build/libs/trade-0.0.1-SNAPSHOT.jar
```

### 환경 변수

다음 환경 변수를 설정해야 합니다:

```bash
# Server
PORT=8081

# Database (MySQL)
DATABASE_URL=jdbc:mysql://your-rds-endpoint:3306/trade
DATABASE_USERNAME=your-username
DATABASE_PASSWORD=your-password

# JWT
JWT_SECRET=your-jwt-secret-key

# Frontend URL (CORS)
FRONTEND_URL=https://main.d32flqff9mmre3.amplifyapp.com

# Google OAuth2
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# Naver OAuth2
NAVER_CLIENT_ID=your-naver-client-id
NAVER_CLIENT_SECRET=your-naver-client-secret

# Toss Payments
TOSS_CLIENT_KEY=your-toss-client-key
TOSS_SECRET_KEY=your-toss-secret-key
TOSS_DEV_MODE=true

# AWS S3
AWS_S3_ACCESS_KEY=your-s3-access-key
AWS_S3_SECRET_KEY=your-s3-secret-key
AWS_S3_BUCKET=your-bucket-name
AWS_S3_REGION=ap-northeast-2
```

## 배포

AWS EC2에서 운영 중입니다.
- EC2 인스턴스에서 JAR 파일 실행
- RDS MySQL 연동
- S3 이미지 저장소 연동

## 관련 저장소

- **Frontend**: [trade-frontend](https://github.com/2jinyong/trade-frontend)
