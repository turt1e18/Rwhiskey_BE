# R-Whiskey: Backend API Integration Guide (Environment & Security)

이 문서는 R-Whiskey 프론트엔드 프로젝트와 백엔드 API 서버 간의 통신 규격, 환경별 설정, 및 보안 정책에 대한 가이드입니다. **프론트엔드 Gemini CLI는 이 문서를 기반으로 API 요청 환경을 구성해야 합니다.**

## 1. 환경별 구성 (Environment Environments)

백엔드는 `Local` 개발 환경과 `Production` 운영 환경이 엄격히 분리되어 있으며, 보안 정책이 다르게 적용됩니다.

| 구분 | Local (개발) | Production (운영) |
| :--- | :--- | :--- |
| **API Base URL** | `http://localhost:8080` | `https://turt1e18.work` |
| **FE Origin (CORS)** | `http://localhost:3000` | `https://rwhiskey.turt1e18.work` |
| **CSRF 보호** | **Disabled** | **Enabled** (일부 제외) |
| **인증 방식** | Session (Cookie) | Session (Cookie) |
| **Spring Profile** | `default` or `local` | `prod` |

---

## 2. 보안 패치 및 통신 규격 (Security & Communication)

### 2.1 CORS (Cross-Origin Resource Sharing)
- **설정**: 모든 요청에 대해 `allowCredentials: true`가 설정되어 있습니다.
- **FE 필수 사항**: 모든 API 호출 시 `withCredentials: true` (axios) 또는 `credentials: 'include'` (fetch) 옵션을 반드시 포함해야 세션 유지가 가능합니다.
- **Allowed Origins**:
  - Local: `http://localhost:3000`
  - Prod: `https://rwhiskey.turt1e18.work`, `https://turt1e18.work`

### 2.2 CSRF (Cross-Site Request Forgery)
- **Local**: 개발 편의를 위해 비활성화되어 있습니다.
- **Production**: 활성화되어 있습니다.
  - **Exception**: `POST /api/auth/**` 경로는 인증 시작 단계이므로 CSRF 체크에서 제외됩니다.
  - **기타 POST/PUT/PATCH/DELETE**: 운영 환경에서는 CSRF 토큰 검증이 필요할 수 있습니다. (현재는 세션 기반으로 작동하며, 필요 시 프론트엔드에서 폼 데이터나 헤더에 토큰을 실어 보내야 함)

### 2.3 쿠키 및 세션 (Cookie & Session)
- **SameSite Policy**: 운영 환경에서는 `Lax` 또는 `None` (Secure 필수) 설정을 고려해야 합니다.
- **HTTPS**: 운영 환경(`https://turt1e18.work`)은 모든 통신이 암호화됩니다.

---

## 3. 데이터 모델 (Data Models - CamelCase 통일)

최근 리팩토링을 통해 백엔드 엔티티와 DTO는 **CamelCase**로 통일되었습니다.

### 3.1 추천 저장 (`RecommendationSaveRequest`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `weatherValue` | String | 날씨 (예: "맑음") |
| `moodValue` | String | 기분 (예: "우울함") |
| `abvValue` | String | 도수 (예: "40%") |
| `additionalValue` | String | 추가 요청 사항 |
| `flexFlag` | Boolean | 과시 여부 |
| `whiskeyName` | String | 위스키 한글명 |
| `whiskeyNameEn` | String | 위스키 영문명 |
| `whiskeyCategory` | String | 위스키 종류 |
| `featureTags` | List<String> | 태그 ID 또는 텍스트 리스트 |

---

## 4. 프론트엔드 대응 가이드 (FE Gemini CLI Checklist)

1. **Environment Variables**: `.env.local`과 `.env.production`을 생성하여 `NEXT_PUBLIC_API_BASE_URL`을 각각 설정하세요.
2. **API Client**: `credentials: 'include'`가 기본값으로 설정된 API 클라이언트를 사용하세요.
3. **Domain Sync**: 운영 서버 주소가 `turt1e18.work`임을 인지하고, CORS 에러 발생 시 해당 도메인이 백엔드 `allowedOrigins`에 등록되어 있는지 확인하세요.
4. **Auth Exception**: `POST /api/auth/**` 호출 시에는 CSRF 걱정 없이 요청을 보낼 수 있습니다.

---

## 5. API 상세 목록

(기존 API 리스트 유지 - `API_CONTRACT.md` 참조 권장)
- `POST /api/auth/signup`: 회원가입
- `POST /api/auth/login`: 로그인
- `GET /api/auth/me`: 내 정보 조회
- `POST /api/recommendations/{uid}`: 추천 결과 저장
- `GET /api/notes`: 테이스팅 노트 목록 조회
- `PATCH /api/notes/{noteId}`: 노트 수정 (Partial Update 지원)

