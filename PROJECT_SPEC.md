# 중고거래 플랫폼 프로젝트 기획서

## 1. 프로젝트 개요

### 1.1 프로젝트명
**Trade** - 중고거래 플랫폼

### 1.2 프로젝트 목표
사용자 간 중고 물품을 등록하고 거래할 수 있는 웹 기반 플랫폼 구축

### 1.3 기술 스택

| 구분 | 기술 |
|------|------|
| **Backend** | Spring Boot 3.5.7, Java 17+ |
| **Security** | Spring Security, JWT, OAuth2 |
| **Database** | H2 (개발), MySQL (운영 예정) |
| **ORM** | Spring Data JPA, Hibernate |
| **Build** | Gradle |
| **Frontend** | React (localhost:3000) |

---

## 2. 시스템 아키텍처

```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend (React)                        │
│                     http://localhost:3000                    │
└─────────────────────────┬───────────────────────────────────┘
                          │ REST API
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                   Backend (Spring Boot)                      │
│                     http://localhost:8081                    │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ Controller  │──│   Service   │──│    Repository       │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Security Layer (JWT Filter)             │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────┬───────────────────────────────────┘
                          │
          ┌───────────────┼───────────────┐
          ▼               ▼               ▼
    ┌──────────┐   ┌──────────┐   ┌──────────────┐
    │ Database │   │  OAuth2  │   │ File Storage │
    │   (H2)   │   │ Provider │   │  (uploads/)  │
    └──────────┘   └──────────┘   └──────────────┘
```

---

## 3. 데이터베이스 설계

### 3.1 ERD (Entity Relationship Diagram)

```
┌──────────────────────────┐
│          users           │
├──────────────────────────┤
│ PK  user_id    VARCHAR   │
│     password   VARCHAR   │
│     name       VARCHAR   │
│     tel        VARCHAR   │
│     email      VARCHAR   │◄──── UNIQUE
│     role       VARCHAR   │
│     provider   VARCHAR   │
│     provider_id VARCHAR  │
└──────────────────────────┘
            │
            │ 1:N
            ▼
┌──────────────────────────┐       ┌──────────────────────────┐
│          post            │       │          likes           │
├──────────────────────────┤       ├──────────────────────────┤
│ PK  id         BIGINT    │◄──────│ PK  id         BIGINT    │
│ FK  user_id    VARCHAR   │       │ FK  user_id    VARCHAR   │───┐
│     title      VARCHAR   │       │ FK  post_id    BIGINT    │   │
│     content    TEXT      │       └──────────────────────────┘   │
│     price      INT       │              UNIQUE(user_id, post_id)│
│     views      INT       │                                      │
│     created_at TIMESTAMP │◄─────────────────────────────────────┘
│     updated_at TIMESTAMP │
└──────────────────────────┘
```

### 3.2 테이블 명세

#### users (사용자)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| user_id | VARCHAR(100) | PK | 사용자 ID |
| password | VARCHAR(100) | NULL 허용 | 비밀번호 (소셜 로그인 시 NULL) |
| name | VARCHAR(50) | NOT NULL | 사용자 이름 |
| tel | VARCHAR(15) | NULL 허용 | 전화번호 |
| email | VARCHAR(100) | NOT NULL, UNIQUE | 이메일 |
| role | VARCHAR(10) | NOT NULL | 권한 (user/ADMIN) |
| provider | VARCHAR(20) | NULL 허용 | 로그인 제공자 (google/naver/local) |
| provider_id | VARCHAR(100) | NULL 허용 | 소셜 서비스 고유 ID |

#### post (게시글)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 게시글 ID |
| user_id | VARCHAR(100) | FK | 작성자 ID |
| title | VARCHAR(255) | | 제목 |
| content | TEXT | | 내용 |
| price | INT | | 가격 |
| views | INT | DEFAULT 0 | 조회수 |
| created_at | TIMESTAMP | | 생성일시 |
| updated_at | TIMESTAMP | | 수정일시 |

#### likes (좋아요)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 좋아요 ID |
| user_id | VARCHAR(100) | FK, NOT NULL | 사용자 ID |
| post_id | BIGINT | FK, NOT NULL | 게시글 ID |
| - | - | UNIQUE(user_id, post_id) | 중복 좋아요 방지 |

---

## 4. 기능 명세

### 4.1 회원 관리

#### 회원가입
- **기능**: 신규 사용자 등록
- **검증**: userId/email 중복 확인
- **보안**: BCrypt 비밀번호 암호화

#### 로그인
- **일반 로그인**: ID/비밀번호 인증 → JWT 토큰 발급
- **소셜 로그인**: Google, Naver OAuth2 지원
- **토큰 저장**: HttpOnly 쿠키 (1시간 유효)

#### 로그아웃
- **기능**: 토큰 쿠키 삭제

### 4.2 게시글 관리

| 기능 | 설명 | 인증 |
|------|------|------|
| 게시글 작성 | 제목, 내용, 가격, 이미지 등록 | 필수 |
| 게시글 목록 | 전체 게시글 조회 (좋아요 수 포함) | 불필요 |
| 게시글 상세 | 단건 조회 + 조회수 증가 | 불필요 |
| 게시글 수정 | 본인 게시글 수정 | 필수 |
| 게시글 삭제 | 본인 게시글 삭제 | 필수 |

#### 조회수 중복 방지
- 쿠키 기반으로 동일 사용자의 중복 조회 방지
- 자정 기준으로 쿠키 만료

### 4.3 좋아요 기능

| 기능 | 설명 | 인증 |
|------|------|------|
| 좋아요 토글 | 좋아요 추가/취소 | 필수 |
| 좋아요 상태 조회 | 현재 좋아요 여부 및 총 개수 | 선택적 |

### 4.4 이미지 업로드
- **저장 위치**: `C:/jinyong/project/uploads/`
- **파일명**: UUID 기반 고유 파일명 생성
- **최대 크기**: 30MB
- **접근 URL**: `/uploads/{filename}`

---

## 5. API 명세

### 5.1 인증 API

| Method | Endpoint | 설명 | Request | Response |
|--------|----------|------|---------|----------|
| POST | `/api/register` | 회원가입 | RegisterDto | 200/409 |
| POST | `/api/login` | 로그인 | LoginDto | JWT Cookie |
| POST | `/api/logout` | 로그아웃 | - | Cookie 삭제 |
| GET | `/api/auth/check` | 인증 상태 확인 | - | {authenticated: boolean} |

### 5.2 OAuth2 API

| Method | Endpoint | 설명 | Request | Response |
|--------|----------|------|---------|----------|
| POST | `/api/oauth2/google` | 구글 로그인 | {accessToken} | JWT Cookie |
| POST | `/api/oauth2/naver` | 네이버 로그인 | {accessToken} | JWT Cookie |

### 5.3 게시글 API

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| POST | `/api/posts` | 게시글 작성 | O |
| GET | `/api/posts` | 전체 조회 | X |
| GET | `/api/posts/{id}` | 상세 조회 | X |
| PUT | `/api/posts/{id}` | 수정 | O |
| DELETE | `/api/posts/{id}` | 삭제 | O |
| POST | `/api/posts/upload` | 이미지 업로드 | X |

### 5.4 좋아요 API

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| POST | `/api/likes/{postId}` | 좋아요 토글 | O |
| GET | `/api/likes/{postId}` | 좋아요 상태 | 선택 |

---

## 6. 보안 설계

### 6.1 인증 흐름

```
사용자 로그인 요청
        │
        ▼
┌───────────────────┐
│   ID/PW 검증      │
│   또는 OAuth2     │
└─────────┬─────────┘
          │
          ▼
┌───────────────────┐
│  JWT 토큰 생성    │
│  (1시간 유효)     │
└─────────┬─────────┘
          │
          ▼
┌───────────────────┐
│ HttpOnly Cookie   │
│ 에 토큰 저장      │
└─────────┬─────────┘
          │
          ▼
    매 요청 시
          │
          ▼
┌───────────────────┐
│ JwtAuthFilter     │
│ 토큰 검증         │
└─────────┬─────────┘
          │
          ▼
┌───────────────────┐
│ SecurityContext   │
│ Authentication    │
└───────────────────┘
```

### 6.2 보안 적용 항목

| 항목 | 적용 |
|------|------|
| 비밀번호 암호화 | BCrypt |
| 토큰 서명 | HMAC-SHA256 |
| XSS 방지 | HttpOnly Cookie |
| HTTPS 전용 | Secure Cookie |
| CORS | 특정 도메인만 허용 |

---

## 7. 프로젝트 구조

```
src/main/java/com/jinyong/trade/
├── TradeApplication.java          # 메인 애플리케이션
├── config/
│   ├── SecurityConfig.java        # Spring Security 설정
│   └── WebConfig.java             # 정적 리소스 설정
├── controller/
│   ├── PostController.java        # 게시글 API
│   ├── LikeController.java        # 좋아요 API
│   ├── RegisterController.java    # 회원가입 API
│   ├── LoginController.java       # 로그인 API
│   ├── LogoutController.java      # 로그아웃 API
│   ├── OAuth2Controller.java      # 소셜 로그인 API
│   └── AuthcheckController.java   # 인증 상태 API
├── entity/
│   ├── BaseEntity.java            # 공통 엔티티 (생성/수정일)
│   ├── User.java                  # 사용자 엔티티
│   ├── Post.java                  # 게시글 엔티티
│   └── Like.java                  # 좋아요 엔티티
├── dto/
│   ├── PostDto.java               # 게시글 요청 DTO
│   ├── PostResponseDto.java       # 게시글 응답 DTO
│   ├── RegisterDto.java           # 회원가입 DTO
│   ├── LoginDto.java              # 로그인 DTO
│   ├── LikeDto.java               # 좋아요 DTO
│   └── OAuth2RequestDto.java      # OAuth2 요청 DTO
├── service/
│   ├── PostService.java           # 게시글 비즈니스 로직
│   ├── LikeService.java           # 좋아요 비즈니스 로직
│   ├── UserService.java           # 사용자 비즈니스 로직
│   ├── OAuth2Service.java         # 소셜 로그인 처리
│   └── CustomUserDetailService.java # Spring Security 연동
├── repository/
│   ├── UserRepository.java        # User JPA Repository
│   ├── PostRepository.java        # Post JPA Repository
│   └── LikeRepository.java        # Like JPA Repository
├── jwt/
│   ├── JwtUtil.java               # JWT 생성/검증 유틸
│   └── JwtAuthFilter.java         # JWT 인증 필터
└── initializer/
    └── AdminInitializer.java      # 초기 관리자 계정 생성
```

---

## 8. 구현 현황

### 8.1 완료된 기능

| 기능 | 상태 | 비고 |
|------|:----:|------|
| 회원가입 | ✅ | 중복 검증 포함 |
| 로그인/로그아웃 | ✅ | JWT 쿠키 기반 |
| Google OAuth2 | ✅ | 소셜 로그인 |
| Naver OAuth2 | ✅ | 소셜 로그인 |
| 게시글 CRUD | ✅ | 전체 기능 완료 |
| 이미지 업로드 | ✅ | UUID 파일명 |
| 조회수 기능 | ✅ | 쿠키 기반 중복 방지 |
| 좋아요 기능 | ✅ | 토글 방식 |

### 8.2 향후 개발 예정 기능

| 기능 | 우선순위 | 설명 |
|------|:--------:|------|
| 검색 기능 | 높음 | 제목/내용 키워드 검색 |
| 카테고리 | 높음 | 물품 분류 체계 |
| 채팅 기능 | 중간 | 판매자-구매자 실시간 채팅 |
| 거래 상태 | 중간 | 판매중/예약중/거래완료 |
| 찜 목록 | 중간 | 사용자별 관심 상품 |
| 신고 기능 | 낮음 | 부적절한 게시물 신고 |
| 평점/리뷰 | 낮음 | 거래 후 평가 시스템 |

---

## 9. 테스트 계정

| ID | Password | 권한 | 용도 |
|----|----------|------|------|
| admin | admin1234 | ADMIN | 관리자 테스트 |
| qwer | qwer1234 | USER | 일반 사용자 테스트 |

---

## 10. 환경 설정

### 10.1 서버 포트
- Backend: `8081`
- Frontend: `3000`

### 10.2 데이터베이스
- 개발: H2 인메모리 (`jdbc:h2:mem:testdb`)
- H2 콘솔: `http://localhost:8081/h2-console`

### 10.3 파일 업로드
- 저장 경로: `C:/jinyong/project/uploads/`
- 최대 용량: 30MB

---

## 11. 실행 방법

```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 또는 JAR 실행
java -jar build/libs/trade-0.0.1-SNAPSHOT.jar
```

---

*문서 작성일: 2025-12-16*