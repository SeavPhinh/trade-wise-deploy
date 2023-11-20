<!-- <img width="200" src="themes/orderup-tower/login/assets/OrderUpLogo.png" alt="Material Bread logo"> -->

<h1 align="center">
  Trade Wise
</h1>


## To run project as manual

- ### first time must run config-server

- ### then run eureka-server
- ### then up container for zipkin

- #### server IP: 8.222.225.41
- ####  category-database (existing in server) 
```diff
    - POSTGRES_DB = category_db
    - POSTGRES_USER = postgres
    - POSTGRES_PASSWORD = tradewise
    - port : 1688
```

- ####  user-info-database (existing in server)  
```diff
    - POSTGRES_DB = user-info_db
    - POSTGRES_USER = postgres
    - POSTGRES_PASSWORD = tradewise
    - port : 1688
```

- ####  post-database (existing in server)  
```diff
    - POSTGRES_DB = post_db
    - POSTGRES_USER = postgres
    - POSTGRES_PASSWORD = tradewise
    - port : 1688
```
- ####  product-database (existing in server)  
```diff
    - POSTGRES_DB = product_db
    - POSTGRES_USER = postgres
    - POSTGRES_PASSWORD = tradewise
    - port : 1688
```
- ####  shop-database (existing in server)  
```diff
    - POSTGRES_DB = shop_db
    - POSTGRES_USER = postgres
    - POSTGRES_PASSWORD = tradewise
    - port : 1688
```
- ####  chat-database (existing in server)
```diff
    - POSTGRES_DB = chat_db
    - POSTGRES_USER = postgres
    - POSTGRES_PASSWORD = tradewise
    - port : 1688
```
- ####  notification-database (existing in server)
```diff
    - POSTGRES_DB = notification_db
    - POSTGRES_USER = postgres
    - POSTGRES_PASSWORD = tradewise
    - port : 1688
```
- ### then run user-service (connect to keycloak server)
- ### for any service that not user-service can run after above 5 steps run completed
- ### for api-gateway depend on every service it can starting with all of service if each service run completed


- ### user-info-service depend on user-service
- ### post-service and product-service and shop-service depend on user-service
- ### post-service and shop-service depend on category-service

[//]: # (<style>H1{color:Orange;}</style>)

[//]: # (<style>H4{color:Orange;text-align: center}   </style>)


 # access service by url of running  manually
 -  [config-server](http://localhost:8888) 
 -  [gateway-service swagger local](http://localhost:8080/webjars/swagger-ui/index.html) 
 -  [eureka server](http://localhost:8761) 
 - ### access service without api-gateway
 -  [category-service swagger local](http://localhost:8087/category-service/swagger-ui/index.html)
 -  [user-service swagger local](http://localhost:8081/user-service/swagger-ui/index.html)
 -  [user-info-service swagger local](http://localhost:8084/user-info-service/swagger-ui/index.html)
 -  [product-service swagger local](http://localhost:8089/product-service/swagger-ui/index.html)
 -  [shop-service swagger local](http://localhost:8088/shop-service/swagger-ui/index.html)
 -  [post-service swagger local](http://localhost:8083/post-service/swagger-ui/index.html) 
 -  [chat-service swagger local](http://localhost:8082/chat-service/swagger-ui/index.html) 
 -  [notification-service swagger local](http://localhost:8086/notification-service/swagger-ui/index.html) 
 - ### access service via server instance
 -  [category-service swagger local](http://8.222.225.41:8087/category-service/swagger-ui/index.html)
 -  [user-service swagger local](http://8.222.225.41:8081/user-service/swagger-ui/index.html)
 -  [user-info-service swagger local](http://8.222.225.41:8084/user-info-service/swagger-ui/index.html)
 -  [product-service swagger local](http://8.222.225.41:8089/product-service/swagger-ui/index.html)
 -  [shop-service swagger local](http://8.222.225.41:8088/shop-service/swagger-ui/index.html)
 -  [post-service swagger local](http://8.222.225.41:8083/post-service/swagger-ui/index.html)
 -  [chat-service swagger local](http://8.222.225.41:8082/chat-service/swagger-ui/index.html)
 -  [notification-service swagger local](http://8.222.225.41:8086/notification-service/swagger-ui/index.html) 


 

