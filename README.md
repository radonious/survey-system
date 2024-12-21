# Система создания и прохождения опросов

![Java](https://img.shields.io/badge/Java-ED6B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)
![Postgres](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Vue JS](https://img.shields.io/badge/Vue%20js-35495E?style=for-the-badge&logo=vuedotjs&logoColor=4FC08D)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white)
![HTML](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85CAD?style=for-the-badge&logo=Swagger&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-333333?style=for-the-badge&logo=vite&logoColor=FFD62E)

## Оглавление

1. [Цель проекта](#Цель-проекта)
2. [Задачи и требования проекта](#Задачи-и-требования-проекта)
3. [Критерии приёма](#Критерии-приёма)
4. [Ожидаемые сложности и риски](#Ожидаемые-сложности-и-риски)
5. [Дополнительная функциональность](#Дополнительная-функциональность)
6. [Используемые зависимости](#Используемые-зависимости)


## Цель проекта

> Разработать интерактивную систему создания и прохождения опросов

## Задачи и требования проекта

- Создание и настройка проекта
- Разработка структуры базы данных
  - Наличие ролей CREATOR и USER
- Разработка REST контроллеров
  - Возможность создавать, просматривать и проходить опросы, созданные CREATOR
  - Просмотр статистики опроса
- Наличие UI необязательно

## Критерии приёма

- Работоспособность всей системы
- Наличие тестов
- Реализация взаимодействия по REST и(или) UI

## Ожидаемые сложности и риски

- Реализация UI значительно повышает сложность разработки

## Дополнительная функциональность

В ходе разработки для придания полноценности проекту были выполнены следующие задачи:
- Использование Docker для подключения Postgres
- Полный Deploy приложения и базы данных в Docker контейнер
- Подключение и настройка Liquibase
- Использование MapStruct для маппинга сущностей между слоями приложения
- Разработан UI (web-интерфейс) с использованием Vue.js и Vite
- Подключен и настроен Spring Security, использующий JWT и Refresh токены
- Дополнительная валидация DTO с помощью jakarta.validation
- Возможность вставки поясняющих картинок в вопросы формы
- Выбраны настройки выполнения сервисных транзакций
- Добавлен Swagger и соответствующая ему документация
- Установлена и настроена система логирования

## Необходимые компоненты

- Java 21 или выше
- Grade 8 и выше
#### Или
- Docker Engine 24 и выше

## Сборка проекта

```bash
# 0. Clone
git clone <this repository>
cd 2024-6-eltex-forms
git checkout master

# 1. Set Up .env
cp .env.example .env

# 2.1 Run locally 
gradle clean bootRun

# 2.2. Run fully in Docker 
docker compose up -d

# 2.3. Build locally & Run in Docker
gradle clean bootBuildImage
docker compose up -d --no-build
```

По умолчанию приложение работает на **localhost:8080**.<br>
Для входа извне необходимо открыть порт **8080**.<br>
Адресом для доступа будет внешний адрес машины запуска.<br>
Настройки портов можно изменять в файле **.env**<br>

## Используемые зависимости

- Application version: 1.0
- Java: 21</br>
- Postgres: 17</br>
- Gradle: 8</br>
- Spring Boot: 3.4</br>
- Starter Security: 3.2.8</br>
- Starter WEB: 3.3.1</br>
- Starter Data: 3.3.1</br>
- Starter Validation: 3.3.1</br>
- Starter Test: 3.3.1</br>
- Lombok: 1.18.33</br>
- Liquibase: 4.29</br>
- JWT: 0.11.5</br>
- Mapstruct: 1.5.5</br>
