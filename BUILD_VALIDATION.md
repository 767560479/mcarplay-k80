# K80 本地构建验证记录

验证日期：2026-11-16。源码基线与许可证见 UPSTREAM.md；安装与停车测试步骤见 K80使用说明.md。

## 交付包

- 标准调试 APK：dist/mcarplay-k80-debug.apk。
- 应用名称：mcarplay K80；包名：com.mcarplay.app。
- versionName：0.1.7-k80.1-debug；versionCode：1070000。
- minSdk：31；targetSdk：34；compileSdk：35。
- APK SHA-256：45c1bfe7a84fbaf7f33a7e4dcf768381a7facf34d70013408136c0a0ebd88f16。
- 签名：Android 本机 debug 证书，APK Signature Scheme v2 校验通过。覆盖更新需要相同签名。
- 源码压缩包和 APK 的校验值统一记录在 dist/SHA256SUMS.txt。

## 已执行检查

1. JDK 21、Gradle 8.10.2、SDK Platform 35 / Build Tools 35.0.0；Gradle 分发包与上游固定 SHA-256 一致。
2. spotlessCheck 通过；standard 单元测试 1146 项，remote 调试桥单元测试 25 项，均为 0 失败、0 错误、0 跳过。新增 6 项就绪状态测试包含组件状态组合和精确版本比较。
3. lintStandardDebug 通过：0 错误、60 警告、2 信息。新增 DeviceReadiness、DeviceSetupCard、strings_setup 文件没有 lint 问题；保留上游警告，未更新 lint baseline。
4. assembleStandardDebug 通过；tools/build-k80.ps1 的 -Verify 流程和最终普通打包流程均实际执行成功。
5. APK 包名、版本、SDK 等字段由 aapt 实际读取；签名由 apksigner 实际验证；APK ZIP 完整性检查通过。
6. 标准包的 DEX 中未发现 remote flavor 的 BridgeApp、BridgeClient、BridgeSetupActivity 类标识，标准构建仍按上游源集隔离。
7. LICENSE、NOTICE、THIRD_PARTY_NOTICES.md 与导入的上游归档逐字节一致。

完整门禁命令：

    ./gradlew spotlessCheck :app:testStandardDebugUnitTest :app:lintStandardDebug :app:assembleStandardDebug :app:testRemoteDebugUnitTest --tests com.mcarplay.app.bridge.*

## 手机与实车边界

通过已授权 ADB 只读核对 REDMI K80、Android 16、HyperOS OS3.0.309.0.WOKCNXM、CarWith 4.0.21-20261016、小米 CarLife 8.8.5、高德 17.03.0.2008、网易云 9.6.22。详细脱敏记录见 docs/K80-DEVICE-BASELINE.md。

本次没有安装或启动 APK，没有修改手机权限或设置，没有做手机 UI 运行验证，也没有连接车机。2024 九代凯美瑞的车机固件、CarWith 出流、无线握手、画面、触控、导航、音乐及混音均待停车实测。组件检测和版本一致不能作为实车兼容结论。本轮没有实现 USB 有线 CarPlay 或有线 CarLife，也没有测量性能提升。
