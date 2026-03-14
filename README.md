# DocVault

Aplicación Android para **gestión segura de documentos** desarrollada con tecnologías modernas del ecosistema Android y arquitectura limpia.

## Características
- Gestión local de documentos
- Almacenamiento cifrado
- Autenticación biométrica
- UI moderna con Jetpack Compose
- Arquitectura limpia (Clean Architecture)

## Stack tecnológico
- Kotlin
- Jetpack Compose
- Hilt (inyección de dependencias)
- Room (persistencia local)
- Coroutines (concurrencia)
- Coil (carga de imágenes)
- Android Security Crypto + Keystore
- Biometric Authentication

## Arquitectura
El proyecto sigue **Clean Architecture** separando responsabilidades en capas:

```
presentation/  -> UI y ViewModels
domain/        -> modelos, repositorios y casos de uso
data/          -> base de datos, implementaciones y modelos
di/            -> módulos de inyección de dependencias (Hilt)
```

## Requisitos
- Android Studio
- JDK 11
- Android SDK 26+
- Gradle 8+
- Kotlin 2+

## Build

Debug

```
./gradlew assembleDebug
```

Release

```
./gradlew assembleRelease
```

Instalar build release (para validar R8)

```
./gradlew installRelease
```

## ProGuard / R8

La build release utiliza:

```
isMinifyEnabled = true
isShrinkResources = true
```

Esto permite:
- ofuscación de código
- eliminación de código no usado
- reducción del tamaño del APK

### Nota importante

Para evitar problemas con R8/ProGuard se deben **preservar las anotaciones y modelos usados por reflexión**, especialmente para:

- entidades y DAOs de Room
- modelos utilizados en la capa `data` y `domain`
- clases que dependan de anotaciones

Las reglas se encuentran en:

```
app/proguard-rules.pro
```

## Seguridad
- Cifrado AES mediante Android Keystore
- Almacenamiento seguro con Security Crypto
- Autenticación biométrica del dispositivo