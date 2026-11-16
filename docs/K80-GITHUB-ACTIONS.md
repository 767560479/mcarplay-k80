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

1. 本仓库已准备独立签名密钥，并配置 KEYSTORE_BASE64、KEYSTORE_PASSWORD、KEY_ALIAS、KEY_PASSWORD 四个 Actions Secrets；私有备份保存在创建者本机的 Android 配置目录中，没有进入 Git。新建其他仓库时按 docs/GITHUB_SETUP.md 配置自己的 Secrets。密钥和密码不得进入公开仓库或聊天消息。
2. 在 Actions → Signed APK → Run workflow 选择分支，构建签名 APK；成功后的 mcarplay-signed-运行编号产物默认保留 30 天。
3. 每次版本更新调整 app/build.gradle.kts 中的 appVersionName，确保 versionCode 随之递增。始终使用同一签名，后续包才可正常覆盖升级。
4. 需要长期下载地址时，为版本补充 CHANGELOG.md 后推送匹配的 vX.Y.Z 或 vX.Y.Z-test.N 标签，现有流程会发布到 GitHub Releases。标签发布需遵循上游的版本与变更记录校验。

本地修改版停用了上游应用内更新入口，后续从自己的仓库下载 APK 手动安装。固定签名包与旧 debug 包签名不同，首次迁移应先备份配置。

本仓库固定签名证书 SHA-256：d4c91ca708a588638362d04ba71ce7d6dc82c6177f75c974ef3242e41bc1c7d5。此值是公开证书指纹，不包含私钥。

## 当前执行状态

2026-11-16 已创建公开仓库并上传源码，提交 a56dc474fcb8bc44a5a0b7e619dd4da90674b79b。首次云端验证结果：

1. [Android CI #1](https://github.com/767560479/mcarplay-k80/actions/runs/43870208972) 成功，测试 APK 与检查报告已上传为 Artifacts。
2. [Signed APK #1](https://github.com/767560479/mcarplay-k80/actions/runs/43870318080) 成功，固定签名 APK 已生成。下载 [mcarplay-signed-1](https://github.com/767560479/mcarplay-k80/actions/runs/43870318080/artifacts/14227737622)，登录 GitHub 后解压使用。
3. 已下载云端 APK 到本机，用 apksigner 验证 v2 签名有效，证书指纹与私有备份一致；SHA-256 与云端校验文件一致。
4. APK：mcarplay-0.1.7-k80.1.apk；包名 com.mcarplay.app；versionCode 1070000；大小 14,921,324 字节；SHA-256 为 4c36eccf40b61d0169fc064aa013021e60eaaf5f9a6a92001ed1ecef3b0baf7e。

本地原始 debug APK 的验证结果见 BUILD_VALIDATION.md。以上是构建与签名验证，没有新增手机或实车运行证据；本次没有创建 Release 标签。Artifacts 有保留期限，到期后可手动再次运行工作流，后续下载入口以最新成功运行记录为准。

## 官方说明

- [构建产物与保留时间](https://docs.github.com/en/actions/tutorials/store-and-share-data)
- [配置 Actions Secrets](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-secrets)
