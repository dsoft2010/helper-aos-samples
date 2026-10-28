# GitHub Packages 연동 가이드

이 문서는 DS Helper Android 라이브러리를 GitHub Packages를 통해 연동하는 상세 가이드입니다.

## 1. 저장소 등록

앱 루트의 `settings.gradle.kts`에 DS Helper GitHub Packages 저장소를 등록합니다.

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
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

## 2. 인증 환경변수 설정

인증 정보는 `USERNAME`, `TOKEN` 환경변수로 공급합니다.

```bash
export USERNAME="your-github-username"
export TOKEN="your-personal-access-token"
```

`TOKEN`은 `read:packages` 스코프(읽기 전용)가 있는 Personal Access Token(classic)을 사용하세요.

> **보안**: 토큰을 소스 코드에 하드코딩하지 마세요. 환경변수 또는 CI 시크릿(GitHub Actions Secrets 등)으로 관리하세요.

## 3. 모듈별 의존성 추가

앱 모듈의 `build.gradle.kts`에 사용하려는 라이브러리만 추가합니다.

### web (웹뷰 핵심 기능)

```kotlin
implementation("kr.ds.helper:web:1.5.5")
```

### hybrid-app-activity-base (Activity 기반 하이브리드 앱)

```kotlin
implementation("kr.ds.helper:hybrid-app-activity-base:1.0.0")
```

### hybrid-app-fragment-base (Fragment 기반 하이브리드 앱)

```kotlin
implementation("kr.ds.helper:hybrid-app-fragment-base:1.4.0")
```

### api-client-retrofit (Retrofit API 클라이언트)

```kotlin
implementation("kr.ds.network:api-client-retrofit:1.1.1")
```

## 4. 사용 가능한 모듈 및 버전

| 모듈 | 그룹:아티팩트 | 버전 | 상태 |
|------|---------------|------|------|
| web | kr.ds.helper:web | 1.5.5 | 안정 |
| hybrid-app-core | kr.ds.helper:hybrid-app-core | 1.2.0 | 안정 |
| hybrid-app-activity-base | kr.ds.helper:hybrid-app-activity-base | 1.0.0 | 안정 |
| hybrid-app-fragment-base | kr.ds.helper:hybrid-app-fragment-base | 1.4.0 | 안정 |
| hybrid-app-push | kr.ds.helper:hybrid-app-push | 1.0.1 | 안정 (push flavor 전용) |
| location | kr.ds.helper:location | 1.2.1 | 안정 |
| web-location | kr.ds.helper:web-location | 1.2.0 | 안정 |
| web-download | kr.ds.helper:web-download | 1.0.0 | 안정 |
| util | kr.ds.helper:util | 1.1.1 | 안정 |
| api-client-retrofit | kr.ds.network:api-client-retrofit | 1.1.1 | 안정 |
| api-client-retrofit-rxjava3 | kr.ds.network:api-client-retrofit-rxjava3 | 1.0.0 | 안정 |

## 5. FAQ / 문제 해결

**Q. GitHub Packages 인증이 실패합니다.**  
A. 소스 코드에 토큰을 하드코딩하지 마시고, `USERNAME`/`TOKEN` 환경변수를 설정하세요. CI 시크릿 사용을 권장합니다.

**Q. 의존성을 해결할 수 없습니다.**  
A. `settings.gradle.kts`에 GitHub Packages 저장소가 등록되어 있는지, `USERNAME`/`TOKEN` 환경변수가 올바르게 설정되어 있는지 확인하세요.

**Q. `read:packages` 스코프가 필요합니까?**  
A. 네, GitHub Packages에서 패키지를 읽으려면 읽기 전용 PAT가 필요합니다.
