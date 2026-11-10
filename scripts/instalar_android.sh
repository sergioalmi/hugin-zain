#!/usr/bin/env bash
# Instala JDK 17, Android SDK 35 y Gradle 8.12 en una carpeta sin espacios (el SDK falla con ellos)
# y escribe local.properties de tools/prueba-despegue. Uso (Git Bash): bash scripts/instalar_android.sh
set -euo pipefail

T="${HUGIN_TOOLCHAIN:-$LOCALAPPDATA/hugin-zain-toolchain}"
T="$(cygpath -u "$T")"
REPO="$(cd "$(dirname "$0")/.." && pwd)"
mkdir -p "$T/dl"
cd "$T/dl"

if [ ! -d "$T/jdk" ]; then
    curl -sSfL -o jdk.zip "https://api.adoptium.net/v3/binary/latest/17/ga/windows/x64/jdk/hotspot/normal/eclipse"
    unzip -q jdk.zip -d "$T" && mv "$T"/jdk-17* "$T/jdk"
fi
if [ ! -d "$T/sdk/cmdline-tools/latest" ]; then
    curl -sSfL -o cmdline.zip "https://dl.google.com/android/repository/commandlinetools-win-15859902_latest.zip"
    mkdir -p "$T/sdk/cmdline-tools"
    unzip -q cmdline.zip -d "$T/sdk/cmdline-tools" && mv "$T/sdk/cmdline-tools/cmdline-tools" "$T/sdk/cmdline-tools/latest"
fi

export JAVA_HOME="$T/jdk"
SDKM="$T/sdk/cmdline-tools/latest/bin/sdkmanager.bat"
SDK_WIN="$(cygpath -w "$T/sdk")"
yes | "$SDKM" --sdk_root="$SDK_WIN" --licenses > /dev/null || true
"$SDKM" --sdk_root="$SDK_WIN" "platform-tools" "platforms;android-35" "build-tools;35.0.0" > /dev/null

# Conserva el resto de local.properties (la App Key) y solo reescribe sdk.dir.
LP="$REPO/tools/prueba-despegue/local.properties"
{ grep -v '^sdk.dir=' "$LP" 2>/dev/null || true; printf 'sdk.dir=%s\n' "$(cygpath -m "$T/sdk")"; } > "$LP.tmp"
mv "$LP.tmp" "$LP"
echo "Listo. JDK: $("$JAVA_HOME/bin/java" -version 2>&1 | head -1)"
echo "Antes de compilar: export JAVA_HOME=\"$JAVA_HOME\""
echo "Añade tu App Key a tools/prueba-despegue/local.properties: dji.appKey=..."
