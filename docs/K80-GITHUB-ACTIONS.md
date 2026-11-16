# K80 GitHub 云端打包

## 仓库内容

本项目公开仓库：https://github.com/767560479/mcarplay-k80 。

使用独立公开仓库保存本地修改版，并保留 LICENSE、NOTICE、THIRD_PARTY_NOTICES.md 与 UPSTREAM.md。将 mcarplay-k80 目录里面的源码放到仓库根目录，不能再套一层 mcarplay-k80。不要只上传源码 ZIP；GitHub 不会自动解压 ZIP 执行工作流。

运行 python tools/prepare_upload.py 可生成 dist/github-upload 和 dist/mcarplay-source.zip，已包含 K80 文档、PowerShell 构建脚本、Gradle Wrapper 和 .github 工作流。生成目录已排除本机配置、私钥、APK、构建缓存和日志。使用 Git 上传可完整保留隐藏目录；不要提交整个父级调研目录。

## 测试包

1. 推送源码或构建配置后，Android CI 自动运行；只修改说明文档时默认跳过。也可在 Actions → Android CI → Run workflow 手动运行。
2. CI 安装 JDK 21 与 Android SDK 35，运行工具测试、格式检查、单元测试和 Lint，再编译 standardDebug。
3. 成功后在该次运行的 Artifacts 下载 mcarplay-debug-运行编号，解压获取 APK 与 SHA256SUMS.txt；测试产物默认保留 14 天。
4. 此流程不需要签名 Secrets，但临时 runner 的默认 debug 签名不保证每次相同。这类包适合单次验证，不能保证连续覆盖安装，也不能覆盖此前本机不同签名的 APK。不要为解决签名冲突自动卸载应用。

## 日常使用的固定签名包

1. 为本项目准备独立签名密钥并备份，按 docs/GITHUB_SETUP.md 配置 KEYSTORE_BASE64、KEYSTORE_PASSWORD、KEY_ALIAS、KEY_PASSWORD 四个 Actions Secrets。密钥和密码不得进入公开仓库或聊天消息。
2. 在 Actions → Signed APK → Run workflow 选择分支，构建签名 APK；成功后的 mcarplay-signed-运行编号产物默认保留 30 天。
3. 每次版本更新调整 app/build.gradle.kts 中的 appVersionName，确保 versionCode 随之递增。始终使用同一签名，后续包才可正常覆盖升级。
4. 需要长期下载地址时，为版本补充 CHANGELOG.md 后推送匹配的 vX.Y.Z 或 vX.Y.Z-test.N 标签，现有流程会发布到 GitHub Releases。标签发布需遵循上游的版本与变更记录校验。

本地修改版停用了上游应用内更新入口，后续从自己的仓库下载 APK 手动安装。固定签名包与旧 debug 包签名不同，首次迁移应先备份配置。

## 当前执行状态

本地 APK 的验证结果见 BUILD_VALIDATION.md。云端流程沿用上游的 Android CI 与 Signed APK，首次上传与运行正在执行；实际结果以本仓库 Actions 的对应提交记录为准。

## 官方说明

- [构建产物与保留时间](https://docs.github.com/en/actions/tutorials/store-and-share-data)
- [配置 Actions Secrets](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-secrets)
