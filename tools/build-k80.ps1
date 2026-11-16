#requires -Version 7.0
[CmdletBinding()]
param(
    [string]$JavaHome = $env:JAVA_HOME,
    [string]$AndroidSdk = $env:ANDROID_HOME,
    [switch]$Verify
)

$ErrorActionPreference = 'Stop'
$projectRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
if ([string]::IsNullOrWhiteSpace($JavaHome)) { throw 'Pass -JavaHome with a JDK 21 directory.' }
if ([string]::IsNullOrWhiteSpace($AndroidSdk)) {
    $AndroidSdk = Join-Path $env:LOCALAPPDATA 'Android/Sdk'
}
$java = Join-Path $JavaHome 'bin/java.exe'
if (-not (Test-Path -LiteralPath $java)) { throw 'JDK java.exe was not found.' }
$javaVersion = (& $java -version 2>&1 | Out-String)
if ($javaVersion -notmatch 'version "21[."]') { throw 'This project requires JDK 21.' }
foreach ($component in @('platforms/android-35/android.jar', 'build-tools/35.0.0/aapt2.exe')) {
    if (-not (Test-Path -LiteralPath (Join-Path $AndroidSdk $component))) {
        throw ('Missing Android SDK component: ' + $component)
    }
}
$previousJavaHome = $env:JAVA_HOME
$previousAndroidHome = $env:ANDROID_HOME
Push-Location -LiteralPath $projectRoot
try {
    $env:JAVA_HOME = [IO.Path]::GetFullPath($JavaHome)
    $env:ANDROID_HOME = [IO.Path]::GetFullPath($AndroidSdk)
    $tasks = @(':app:assembleStandardDebug')
    if ($Verify) {
        $tasks = @('spotlessCheck', ':app:testStandardDebugUnitTest', ':app:lintStandardDebug') + $tasks
    }
    & (Join-Path $projectRoot 'gradlew.bat') '--no-daemon' @tasks
    if ($LASTEXITCODE -ne 0) { throw ('Gradle failed with exit code ' + $LASTEXITCODE) }
    $apk = Join-Path $projectRoot 'app/build/outputs/apk/standard/debug/app-standard-debug.apk'
    $dist = Join-Path $projectRoot 'dist'
    New-Item -ItemType Directory -Path $dist -Force | Out-Null
    $target = Join-Path $dist 'mcarplay-k80-debug.apk'
    Copy-Item -LiteralPath $apk -Destination $target -Force
    $hash = (Get-FileHash -LiteralPath $target -Algorithm SHA256).Hash.ToLowerInvariant()
    Set-Content -LiteralPath (Join-Path $dist 'SHA256SUMS.txt') -Value ($hash + '  mcarplay-k80-debug.apk') -Encoding utf8
    Write-Output ('APK: ' + $target)
    Write-Output ('SHA-256: ' + $hash)
} finally {
    Pop-Location
    $env:JAVA_HOME = $previousJavaHome
    $env:ANDROID_HOME = $previousAndroidHome
}
