# K80 device baseline

Date: 2026-11-16, Asia/Shanghai. Read-only queries used an already authorized ADB connection. Serial number, account information and network identifiers are excluded.

- Market name: REDMI K80
- Model: 24117RK2CC
- Android: 16 / SDK 36
- HyperOS: OS3.0.309.0.WOKCNXM
- CarWith: 4.0.21-20261016 / versionCode 104000021
- Xiaomi CarLife: 8.8.5 / versionCode 274
- Amap: 17.03.0.2008 / versionCode 170300
- NetEase Cloud Music: 9.6.22 / versionCode 9006022
- Local application package com.mcarplay.app: not installed at inspection time
- Vehicle reported by user: 2024 ninth-generation Camry, supports wireless CarPlay

Inspection commands: getprop for manufacturer/model/market name/Android/SDK/incremental/HyperOS and dumpsys package restricted to component version and user installation state. No APK installation, app launch, permission changes or phone configuration changes were performed.

These observations establish the phone environment only. CarWith stream self-test, vehicle connection, media, touch, screen-off behavior and reconnecting remain unverified. Vehicle firmware and receiver identity were not read.
