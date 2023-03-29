# Explore with me
___

*Проект, выполненный в рамках Яндекс Практикума, состоящий из двух сервисов (сервис статистики, основной сервис).*
___

Это приложение-афиша, где можно предложить какое-либо событие 
от выставки до похода в кино и собрать компанию для участия в нём.
 ## Стек
<img src="https://img.shields.io/badge/Java-C71A36?style=for-the-badge&logo=Java&logoColor=white"/>
<img src="https://img.shields.io/badge/SPring boot-%236DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"/>
<img src="https://img.shields.io/badge/PostgreSQL-blue?style=for-the-badge&logo=PostgreSQL&logoColor=white"/>
<img src="https://img.shields.io/badge/H2-black?style=for-the-badge&logo=H2&logoColor=white"/>
<img src="https://img.shields.io/badge/Hibernate-006400?style=for-the-badge&logo=Hibernate&logoColor=white"/>
<img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white"/>
<img src="https://img.shields.io/badge/DOcker-blue?style=for-the-badge&logo=DOcker&logoColor=white"/>

### Функционал

Подразумевается, что оба сервиса ExploreWithMe работают внутри VPN.
С внешним миром сервисы связывает сетевой шлюз. Он контактирует с системой аутентификации и авторизации, а затем перенаправляет запрос в сервисы. То есть, если шлюз пропустил запрос к закрытой или административной части API, значит, этот запрос успешно прошел аутентификацию и авторизацию.

Взаимодействие между сервисами происходит через REST с использованием RestTemplate.

- **Основной сервис** содержит всё необходимое для работы продукта (API  разделен на Public, Private, Admin);
- **Сервис статистики** хранит количество просмотров и позволяет делать различные выборки для анализа работы приложения.

### Спецификация 
<a href="https://petstore.swagger.io/?url=https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json">
API main service
</a>
<br>
<a href="https://petstore.swagger.io/?url=https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-stats-service-spec.json">
API statistics service
</a>

---
### ERD

***main service***

![img.png](img.png)

***statistics service***

![img_1.png](img_1.png)

### Возможности по улучшению
Использовать WebClient вместо RestTemplate.

### Тестирование приложения
Предусмотрены postam-тесты в папке "Postman".