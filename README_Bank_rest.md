## Инструкция по запуску проекта

### 1. С помощью Docker Compose (рекомендуется)

- Убедитесь, что у вас установлены Docker.
- В файле application-dev.yml настройте подключение к базе данных. Также установите "jwt.secret.access" для генерации JWT токена. Затем укажите "encryption.secret" для шифрования.
- В файле docker-compose.yml в секции postgres укажите имя пользователя и пароль к базе данных.
- Откройте терминал в корне проекта и выполните следующую команду: docker-compose up -d

- Приложение будет доступно по адресу `http://localhost:8010/bankcards`
- Сваггер доступен по адресу `http://localhost:8010/bankcards/swagger-ui/index.html`

### 2. С помощью Maven

- Убедитесь, что у вас установлены Java и Maven.
- Вам необходимо иметь запущенную базу данных PostgreSQL на `localhost:5432`.
- Создайте базу данных с именем `bankcards`.
- Создайте пользователя в базе данных или используйте существующего и предоставьте ему все права на базу данных `bankcards`.
- Для подключения к локальной базе данных используйте следующий URL: jdbc:postgresql://localhost:5432/bankcards?currentSchema=public
- В файле application-dev.yml настройте подключение к базе данных. Также установите "jwt.secret.access" для генерации JWT токена. Затем укажите "encryption.secret" для шифрования.
- Откройте терминал в корне проекта и выполните следующую команду: mvn spring-boot:run

- Приложение будет доступно по адресу `http://localhost:8010/bankcards`
- - Сваггер доступен по адресу `http://localhost:8010/bankcards/swagger-ui/index.html`