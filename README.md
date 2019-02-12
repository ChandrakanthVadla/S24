# Scala Tech Task

RESTful web-service written in Scala. The service will allow users to place new car adverts and view, modify and delete existing car adverts.

Car adverts have the following fields:
* **id** (_required_): **int** 
* **title** (_required_): **string**, e.g. _"Audi A4 Avant"_;
* **fuel** (_required_): gasoline or diesel, additional fuel types can be added in future by appending the new type to Enumeration type Fuel in Base.scala file. 
* **price** (_required_): **integer**;
* **new** (_required_): **boolean**, indicates if car is new or used;
* **mileage** (_only for used cars_): **integer**;
* **first registration** (_only for used cars_): **date** without time.

Service should:
* have functionality to return list of all car adverts;
  * optional sorting by any field specified by query parameter, default sorting - by **id**;
* have functionality to return data for single car advert by id;
* have functionality to add car advert;
* have functionality to modify car advert by id;
* have functionality to delete car advert by id;
* have validation (see required fields and fields only for used cars);
* accept and return data in JSON format, use standard JSON date format for the **first registration** field.


## Getting Started

Download or clone this repository to your system.

### Prerequisites

sbt

```
Give examples
```

### Installing

From command prompt
```
cd LocalDirctory
sbt run
```

Using Intellij
```
load the project in intellij IDE
go to Run -> Edit Configurations
click + symbol
select sbt Task in the left panel and give some Name and put ~run in Tasks
Run -> run 'Name'
```

#####From browser:
```
http://localhost:9000/
```


##### using CURL for RESTful apis

######List all car advert
 ```
 curl http://localhost:9000/v1/adverts
```
######List all car advert with sort field
```
curl http://localhost:9000/v1/adverts?sort=fieldName
```

######Single car advert
```
curl http://localhost:9000/v1/adverts/id
```
######Add car advert
```
curl -X POST -i -d @request.json -H "Content-Type: application/json"  http://localhost:9000/v1/adverts/
curl -X POST -i -d '{"title": "MINI Cooper Clubman ","fuel": "Diesel","price": 26000,"new": true}' -H "Content-Type: application/json"  http://localhost:9000/v1/adverts/
```
######Modify car advert by id 
```
curl -X PUT -d @request.json -H "Content-Type: application/json"  http://localhost:9000/v1/adverts/id 
```
######Delete car advert by id 
```curl -X "DELETE"  http://localhost:9000/v1/adverts/id
```

request.json 
example 1
{
    "title": "MINI Cooper Countryman",
    "fuel": "Diesel",
    "price": 26000,
    "new": true
}

example 2
{
    "title": "MINI Cooper Countryman",
    "fuel": "Diesel",
    "price": 26000,
    "new": false,
    "mileage":2,
    "first_registration":"2001-01-01"
} 


## Running the tests
```
test : to run all tests

testOnly ModelSpec      
testOnly FunctionalSpec 
testOnly BrowserSpec    
```


## Built With

* [Play framework](https://www.playframework.com/documentation/2.7.x/Home) - The web framework for Scala and Java
* [Slick](http://slick.lightbend.com/docs/) - Functional Relational Mapping for Scala
* [ScalaTest](https://www.playframework.com/documentation/2.7.x/ScalaTestingWithScalaTest#Testing-your-application-with-ScalaTest) - for test cases
* [H2 database](https://www.playframework.com/documentation/2.7.x/Developing-with-the-H2-Database#H2-database) - for In memory SQL database


## Authors

* **ChandraKanth Vadla** - 

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
