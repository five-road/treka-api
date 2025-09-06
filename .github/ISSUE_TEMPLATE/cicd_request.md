---
name: "⚙️ CI/CD 개선 요청"
about: "빌드, 테스트, 배포 등 CI/CD 파이프라인 관련 작업을 제안합니다."
title: "⚙️ CI: [개선할 CI/CD 작업에 대한 요약]"
labels: "ci/cd, infrastructure"
assignees: ''
---

### 🚀 이 작업의 목표는 무엇인가요?
<!-- **문제점:** [예: 현재 스테이징 서버 배포는 개발자가 수동으로 진행하고 있어 휴먼 에러 발생 가능성이 높습니다.] -->
<!-- **목표:** [예: PR이 `develop` 브랜치에 머지될 때마다 자동으로 스테이징 서버에 배포되도록 자동화합니다.] -->
- **문제점:** :
- **목표:** :

  
### 🎯 작업 대상
<!-- **대상 파일:** [예: `.github/workflows/deploy-staging.yml` (신규 생성)] -->
<!-- **관련 플랫폼:** [예: GitHub Actions, Jenkins, AWS CodeDeploy] --> 
- **대상 파일:** :
- **관련 플랫폼:** :

### ✨ 제안하는 변경 사항
- [ ] `develop` 브랜치 `push` 이벤트를 트리거로 설정



### ✅ 완료 조건 (Definition of Done)
- [ ] `develop` 브랜치에 PR이 머지된 후, 5분 이내에 스테이징 서버에 해당 변경사항이 반영된다.
- [ ] 배포 성공 또는 실패 시, 지정된 Discord 채널로 알림이 온다.
- [ ] GitHub Actions 워크플로우 실행 로그에서 에러가 발생하지 않는다.


### ⚠️ 고려할 점 및 리스크
- [ ] 배포 스크립트에 필요한 Secret Key(예: AWS 자격 증명)를 GitHub Secrets에 등록해야 합니다.
