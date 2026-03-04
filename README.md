# RoutineApp (Jetpack Compose)

Минимальный Android-проект на Kotlin с базовой архитектурой:
- MVVM
- Hilt
- Room
- DataStore
- WorkManager
- Jetpack Compose + Navigation

## Экраны
- Today
- Routines
- History
- Settings

`Today` показывает **список активных рутин**. У каждой рутины:
- собственный чеклист
- собственный процент выполнения

В демо-данных есть рутина **"Утренний протокол"**:
1. Зарядка (10)
2. Стакан воды
3. Контрастный душ
4. Дыхание

Также добавлена вторая активная рутина для демонстрации сценария нескольких активных рутин одновременно.

## Сборка и запуск

### Через Android Studio
1. Откройте папку проекта в Android Studio (Hedgehog/Iguana или новее).
2. Дождитесь Gradle Sync.
3. Выберите эмулятор или подключенное устройство.
4. Нажмите **Run**.

### Через командную строку
> Нужны установленный Android SDK, `ANDROID_HOME`/`ANDROID_SDK_ROOT`, и доступные platform/build-tools.

```bash
./gradlew assembleDebug
./gradlew installDebug
```

После установки запустите приложение на устройстве/эмуляторе.
