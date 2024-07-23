# Тестовое задание по нагрузочному тестированию

## Блок 1

1. **Создание тестового окружения**
   - Окружение  со связкой Prometheus-Grafana. В нем настроены дашборды для Kafka Exporter, Spring Boot, Node Exporter и PostgreSQL.

2. **Парсер для логов**
   - Написан парсер на Java. Парсер обрабатывает логи для расчета профиля нагрузки. Применены следующие технологии:
     - `Parser.java`
     - `RequestStatistics.java`
     - Использованы библиотеки: `java.util`, `java.io`, `java.nio`

3. **Скрипты для нагрузочного тестирования**
   - Скрипты написаны в JMeter, включают следующие запросы:
     - POST /api/signDoc
     - GET /api/sendMessage
     - GET /api/getMessage
     - POST /api/addDoc
     - GET /api/getDocByName

## Блок 2

1. **Настройка мониторинга**
   - Мониторинг настроен с помощью Grafana и Prometheus для наблюдения за тестовым окружением.

2. **Генератор нагрузки**
   - Создан генератор нагрузки через Telegram-bot с использованием JMeter.

3. **Тест поиска максимума**
   - Выполнен тест для поиска максимальной пропускной способности. По результатам составлен отчет.

4. **Тест базового профиля**
   - Проведено тестирование базового профиля нагрузки. Составлены отчеты по результатам тестов.

## Блок 3

1. **Изменение режима работы приложения**
   - Изменен режим работы приложения для тестирования различных сценариев.

2. **Тест базового профиля**
   - Повторное тестирование с использованием базового профиля нагрузки.

3. **Тест стабильности (12 часов)**
   - Проведен тест стабильности с использованием ручки Leak. Составлен отчет по результатам теста.

4. **Возврат к дефолтному состоянию**
   - Приложение возвращено к изначальному состоянию.

## Блок 4

1. **Изменение режима работы приложения**
   - Снова изменен режим работы приложения для дополнительных тестов.

2. **Тест базового профиля**
   - Проведено тестирование с использованием базового профиля нагрузки.

3. **Тест стабильности (CPU Load)**
   - Проведен тест стабильности с использованием ручки CPU Load. Составлен отчет по результатам теста.

4. **Возврат к дефолтному состоянию**
   - Приложение возвращено к изначальному состоянию.
