<!-- <img width="200" src="themes/orderup-tower/login/assets/OrderUpLogo.png" alt="Material Bread logo"> -->

<h1 align="center">
  Trade Wise
</h1>


## To run project as manual

- ### first time must run config-server

- ### then run eureka-server

- #### server IP: 35.240.242.176
- ####  category-database (existing in server) 
```diff
    - POSTGRES_DB = category_db
    - POSTGRES_USER = postgres
    - POSTGRES_PASSWORD = tradewise321
    - port : 5432
```

- ####  user-info-database (existing in server)  
```diff
    - POSTGRES_DB = user-info_db
    - POSTGRES_USER = postgres
    - POSTGRES_PASSWORD = tradewise321
    - port : 5432
```

- ####  post-database (existing in server)  
```diff
    - POSTGRES_DB = post_db
    - POSTGRES_USER = postgres
    - POSTGRES_PASSWORD = tradewise321
    - port : 5432
```
- ####  product-database (existing in server)  
```diff
    - POSTGRES_DB = product_db
    - POSTGRES_USER = postgres
    - POSTGRES_PASSWORD = tradewise321
    - port : 5432
```
- ####  shop-database (existing in server)  
```diff
    - POSTGRES_DB = shop_db
    - POSTGRES_USER = postgres
    - POSTGRES_PASSWORD = tradewise321
    - port : 5432
```
- ### then run user-service (connect to keycloak server)
- ### for any service that not user-service can run after above 5 steps run completed
- ### for api-gateway depend on every service it can starting with all of service if each service run completed



- ### user-info-service depend on user-service
- ### post-service and product-service and shop-service depend on user-service
- ### post-service and shop-service depend on category-service

<style>H1{color:Orange;}</style>
<style>H4{color:Orange;text-align: center}   </style>


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
 - ### access service with api-gateway
 -  [category-service swagger local](http://localhost:8080/webjars/swagger-ui/index.html?urls.primaryName=Category%20Service#/)
 -  [user-service swagger local](http://localhost:8081/user-service/swagger-ui/index.html?urls.primaryName=User%20Service)
 -  [user-info-service swagger local](http://localhost:8084/user-info-service/swagger-ui/index.html?urls.primaryName=User%20Info%20Service)
 -  [product-service swagger local](http://localhost:8089/product-service/swagger-ui/index.html?urls.primaryName=Product%20Service)
 -  [shop-service swagger local](http://localhost:8088/shop-service/swagger-ui/index.html?urls.primaryName=Shop%20Service)
 -  [post-service swagger local](http://localhost:8083/post-service/swagger-ui/index.html?urls.primaryName=Post%20Service) 



 

