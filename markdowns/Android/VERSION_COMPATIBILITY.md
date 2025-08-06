# Android Gradle Plugin and Gradle Version Compatibility Reference

# This file documents the correct version combinations for this project

# ✅ CONFIGURACIÓN ACTUAL QUE FUNCIONA (2025-08-03):

# Build SUCCESSFUL en ~30s con cache habilitado

# - AGP: 8.6.1 (Stable - FUNCIONA PERFECTAMENTE)

# - Gradle: 8.9 (Compatible con AGP 8.6.x)

# - Kotlin: 1.9.25 (Compatible con AGP 8.6.x)

# - Compile SDK: 34 (Android 14)

# - Target SDK: 34 (Android 14)

# - Min SDK: 24 (Android 7.0)

# - Configuration Cache: HABILITADO (mejora velocidad de build)

# ❌ VERSIONES QUE FALLAN (probadas):

# - AGP: 8.7.2 con Gradle 8.10.2 y Android 35 = BUILD FAILED

# 💾 BACKUP DISPONIBLE:

# - libs.versions.toml.backup

# - gradle-wrapper.properties.backup

# - app/build.gradle.kts.backup

# AGP to Gradle Compatibility:

# AGP 8.6.x requires Gradle 8.7+

# AGP 8.5.x requires Gradle 8.7+

# If you encounter sync issues:

# 1. Check libs.versions.toml for correct versions

# 2. Ensure gradle-wrapper.properties points to compatible Gradle version

# 3. Run ./gradlew clean before sync

# 4. Invalidate caches in Android Studio if needed

# Common Issues and Solutions:

# - "AGP requires Java 17": Update to Java 17+ in project structure

# - "Gradle sync failed": Check version compatibility above

# - "Plugin not found": Ensure proper repository configuration in settings.gradle.kts
