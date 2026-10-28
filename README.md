# helper-aos-samples — DS Helper Android 라이브러리 사용자 가이드 및 샘플 앱

이 저장소는 **DS Helper Android 라이브러리(`helper_aos`)**의 사용자 가이드 원본과, 배포 좌표(GitHub Packages)를 사용한 실행 가능한 샘플 앱을 모아 둔 저장소입니다.

> 라이브러리 소스 자체는 별도 저장소 [`helper_aos`](https://github.com/dsoft2010/helper_aos) (private)에서 관리됩니다.  
> 이 저장소는 앱 개발자가 라이브러리를 연동하는 데 필요한 **가이드와 샘플 앱**만 포함합니다.

---

## 1. 시작하기

### 1.1 이 저장소란

`helper_aos`는 안드로이드 앱 개발 시 반복적으로 구현하게 되는 기능을 라이브러리로 모아 둔 저장소입니다. 두 가지 패키지로 구분됩니다.

| 패키지 | 역할 | 주요 모듈 예시 |
|--------|------|---------------|
| **helper** | 웹뷰, 하이브리드 앱 구성, 위치, 유틸리티 등 클라이언트 사이드 기능 | `web`, `hybrid-app-core`, `hybrid-app-activity-base`, `hybrid-app-fragment-base`, `location`, `util`, … |
| **network** | Retrofit 기반 API 클라이언트, 통신 인터셉터/인증기 | `api-client-retrofit`, `api-client-retrofit-rxjava3` |

기본 하이브리드 앱 조합은 다음과 같습니다.

- **Activity 기반**: `hybrid-app-activity-base` + `helper:web` + `network:api-client-retrofit`
- **Fragment 기반**: `hybrid-app-fragment-base` + `helper:web` + `network:api-client-retrofit`

### 1.2 지원 환경

| 항목 | 권장 값 |
|------|---------|
| compileSdk | 35 |
| minSdk | API 24 이상 |
| 언어 | Kotlin |
| 빌드 도구 | Gradle 8.7 (Wrapper 포함) |
| JDK | 17 (AGP 8.6.1 권장) |

### 1.3 빌드 및 실행

```bash
# 환경변수 설정 (GitHub Packages 인증)
export USERNAME="your-github-username"
export TOKEN="your-personal-access-token"

# 샘플 앱 빌드
./gradlew :sample-web:assembleDebug
./gradlew :sample-network:assembleDebug
./gradlew :sample-hybrid-activity:assembleDebug
./gradlew :sample-hybrid-fragment:assembleDebug
```

### 1.4 라이브러리 연동 방법 (GitHub Packages)

이 저장소의 샘플 앱은 `project(...)` 로컬 모듈 참조 대신 **GitHub Packages 배포 좌표**를 사용합니다. 라이브러리 소스를 직접 수정할 필요 없이 버전 관리된 AAR을 가져와 사용할 수 있습니다.

#### 1) GitHub Packages 저장소 등록

앱 루트의 `settings.gradle.kts`에 저장소와 인증 정보를 등록합니다.

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            name = "DS Helper GitHubPackages"
            url = uri("https://maven.pkg.github.com/dsoft2010/helper_aos")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }
}
```

인증 정보는 `USERNAME`, `TOKEN` 환경변수로 공급합니다.

```bash
export USERNAME="your-username"
export TOKEN="your-personal-access-token"
```

> **보안**: 개인 토큰을 소스 코드에 하드코딩하지 마세요. `USERNAME`/`TOKEN`은 환경변수 또는 CI 시크릿(GitHub Actions Secrets 등)으로 관리하세요.

#### 2) 의존성 추가

앱 모듈의 `build.gradle.kts`에 사용하려는 라이브러리만 추가합니다.

```kotlin
implementation("kr.ds.helper:web:1.5.5")
implementation("kr.ds.network:api-client-retrofit:1.1.1")
```

자세한 연동 가이드는 [`docs/integration-guide.md`](docs/integration-guide.md)를 참조하세요.

---

## 2. 모듈 가이드

| 모듈 | 그룹:아티팩트 | 버전 | 상태 | 주요 용도 | 샘플 앱 |
|------|---------------|------|------|-----------|---------|
| web | kr.ds.helper:web | 1.5.5 | 안정 | 웹뷰 기본 설정, 백 키, 파일 피커, JS 인터페이스, 커스텀 스킴 처리 | sample-web |
| hybrid-app-core | kr.ds.helper:hybrid-app-core | 1.2.0 | 안정 | 하이브리드 앱 공통 코어 (AppConfiguration, BaseActivity, 딥링크, 스플래시) | sample-hybrid-*/fragment |
| hybrid-app-activity-base | kr.ds.helper:hybrid-app-activity-base | 1.0.0 | 안정 | Activity 기반 하이브리드 앱 기본 구성 (권장) | sample-hybrid-activity |
| hybrid-app-fragment-base | kr.ds.helper:hybrid-app-fragment-base | 1.4.0 | 안정 | Fragment 기반 하이브리드 앱 기본 구성 | sample-hybrid-fragment |
| api-client-retrofit | kr.ds.network:api-client-retrofit | 1.1.1 | 안정 | Retrofit 클라이언트 생성, 인터셉터·인증기·타임아웃·헤더·쿠키 커스터마이징 | sample-network |

각 모듈의 상세 기능·설치 방법·사용 예시는 [`helper_aos`](https://github.com/dsoft2010/helper_aos) 저장소의 `README.md`를 참조하세요.

---

## 3. 샘플 앱 목록

| 샘플 앱 | 경로 | 설명 |
|---------|------|------|
| sample-web | `sample-web/` | `helper:web` 모듈만 사용한 최소 웹뷰 샘플 |
| sample-hybrid-activity | `sample-hybrid-activity/` | `hybrid-app-activity-base` + `web` + `network` (Activity 기반) |
| sample-hybrid-fragment | `sample-hybrid-fragment/` | `hybrid-app-fragment-base` + `web` + `network` (Fragment 기반) |
| sample-network | `sample-network/` | `api-client-retrofit` 샘플 (실제 API 호출 예시) |

각 샘플 앱은 배포 좌표(`implementation("kr.ds.helper:web:1.5.5")` 등)를 사용하며, 로컬 `project(...)` 참조는 하지 않습니다.

---

## 4. 웹-네이티브 브리지

웹과 네이티브 간 통신은 `window.AppInterface.postMessage`를 사용하며, `WebBridge`/`DefaultWebBridge`가 `group`/`function`/`args` 구조로 메시지를 처리합니다.

상세 가이드는 [`docs/web-bridge-guide.md`](docs/web-bridge-guide.md)를 참조하세요. 원본 문서는 [`helper_aos/docs/web-bridge-guide.md`](https://github.com/dsoft2010/helper_aos/blob/develop/docs/web-bridge-guide.md) 에서 확인할 수 있습니다.

---

## 5. 푸시 알림 (참조)

푸시 알림은 `hybrid-app-push` 모듈을 사용하며, `push` flavor에만 적용됩니다. 상세 가이드는 [`helper_aos/docs/hybrid-app-push-guide.md`](https://github.com/dsoft2010/helper_aos/blob/develop/docs/hybrid-app-push-guide.md) 에서 확인할 수 있습니다.

이 저장소의 샘플 앱은 푸시 기능을 포함하지 않습니다(`noPush` 기준).

---

## 6. GitHub Packages 인증 설정

GitHub Packages는 공개 패키지라도 읽기 시 인증이 필요할 수 있습니다. 샘플 앱을 빌드하려면 다음 환경변수를 설정하세요.

```bash
export USERNAME="your-github-username"
export TOKEN="your-personal-access-token-with-read-packages"
```

`TOKEN`은 `read:packages` 스코프가 있는 Personal Access Token(classic)을 사용하세요.

CI 환경(GitHub Actions 등)에서는 Secrets로 `USERNAME`과 `TOKEN`을 등록 후 사용합니다.

---

## 7. 보안 주의사항

- 기존 `helper_aos` 샘플 앱에 하드코딩된 토큰이 있을 수 있으나, **이 저장소에는 포함하지 않습니다.**
- `google-services.json`, `jks` 파일 등은 공개 저장소에 커밋하지 않습니다(`.gitignore` 참고).
- 개인 토큰을 소스 코드에 하드코딩하지 마세요.

---

## 8. 관련 저장소 및 링크

| 항목 | 링크 |
|------|------|
| 라이브러리 소스 (private) | [dsoft2010/helper_aos](https://github.com/dsoft2010/helper_aos) |
| 라이브러리 README | [helper_aos/README.md](https://github.com/dsoft2010/helper_aos/blob/develop/README.md) |
| 웹-네이티브 브리지 가이드 (원본) | [helper_aos/docs/web-bridge-guide.md](https://github.com/dsoft2010/helper_aos/blob/develop/docs/web-bridge-guide.md) |
| 푸시 가이드 (원본) | [helper_aos/docs/hybrid-app-push-guide.md](https://github.com/dsoft2010/helper_aos/blob/develop/docs/hybrid-app-push-guide.md) |

---

## 9. 라이선스

[LICENSE](./LICENSE) 참조.
