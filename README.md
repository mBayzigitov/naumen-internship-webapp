# naumen-internship-webapp
Тестовое задание для Naumen Internship

Выполнил: Байзигитов Мурат Мусаевич

gmail: bayzigitov.murat@gmail.com

# Стек
- Java 17.0.1
- Spring Boot 3.0.6
- Angular 15.2.6
- Spring Data JPA
- ElephantSQL для БД postgres (ограничение 20 МБ, 5 подключений)

# Инструкция для локального запуска
1. Скачать .jar по <a href="https://drive.google.com/drive/folders/1ChVnM-ypGTBAlqvFI-YwgBp__STaGq7U?usp=sharing">ссылке</a>
2. Запустить приложение командой
```java -jar naumen-internship-webapp-0.0.1-SNAPSHOT.jar```
3. Открыть приложение на http://localhost:8080/

# Возможные проблемы
Из-за некоторых ограничений сервиса ElephantSQL запросы к БД могут выполняться дольше обычного. Также при запуске jar-файла возможно исключение PSQLException. В этом случае нужно запустить команду ещё раз.
