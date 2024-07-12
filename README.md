# Test Data Generation for APIs - System Tests

This project contains the system and integration tests of *Test Data Generation for APIs* (TDG).
Includes the required test classes, result files and the SUT projects,
along with an additional project (to convert into module) to evaluate the mutation
score of the tests for one of the SUTs.

## Systems Under Test (SUTs)

- Swagger Petstore (`swagger-petstore-main-fork`).
  This is a detached fork of https://github.com/swagger-api/swagger-petstore at v1.0.17 (2022-09-30) with some additions
- Market (`swagger-market-fork`).
  This is a detached fork of https://github.com/aleksey-lukyanets/market (2022-02-07) with some additions
- Gestao Hospital (`swagger-gestaoHospital-fork`). 
  This is a detached fork of https://github.com/ValchanOficial/GestaoHospital (2023-07-05) with some additions

## Structure of this project

- Module `st-tdg-test`:
  - Java tests (`src/test/java`) for each SUT.
  - Files for test result comparison (`src/test/resources`)
- A folder for each of the SUTs that are evaluated in this replication package.
- A folder `setup` with scripts to execute each SUT (backend and databases).
- Module `st-tdg-eval`: To evaluate the effectiveness of the test data my measuring
  the mutation score of the tests using PIT (pitest.org).

## How to execute the System Tests

All experiments are enclosed in the Java tests. 

To run the tests of a SUT in your local development environment:
  - Ensure that the port that uses the SUT is not used by other SUT
  - Run the server from the `setup` folder and wait until it is up
  - Execute the tests in the namespace that corresponds with the project
  - To execute from maven (e.g. for the petstore): `mvn -pl st-tdg-test` 

Port reservation and endpoints: To avoid conflicts when running in local,
different host port are reserved for each SUT.
Below are the ports and the main endpoint urls to check that SUTs are working:
  - Petstore: 8081:8080 (api) |
    [api example](http://localhost:8081/api/v3/pet/findByStatus?status=available) |
    [swagger-ui](http://localhost:8081/) |
    [api-docs](http://localhost:8081/api/v3/openapi.json)
  - Market: 8082 (web), 8083 (api), 8084 (db)
    [api example](http://localhost:8083/products) |
    [swagger-ui](http://localhost:8083/swagger-ui/index.html) |
    [api-docs](http://localhost:8083/v2/api-docs)
  - Gestao Hospital: 8085:8080 (api), 8086:27017 (default mongo) (db) |
    [api example](http://localhost:8085/v1/hospitais/) |
    [api-docs](http://localhost:8085/v2/api-docs)

The whole sequence of tests for all SUTs is run when executed in CI

## How to evaluate the mutation score

At `st-tdg-eval` run this maven command:
```
mvn test-compile org.pitest:pitest-maven:mutationCoverage
```

Test results are in the `target` folder, and mutation report in `target/pit-reports`

## Graphical TDM models

- Swagger Petstore (src/test/resources/petstore/schema-petstore.md)

```mermaid
classDiagram
  Pet <--"*" Order
  Customer <--"*" Order
  Customer *--"*" Customer_address_xa
  Category <--"*" Pet_category_xt
  Pet *--"*" Pet_photoUrls_xa
  Pet *--"*" Pet_tags_xa
  Pet *--"1" Pet_category_xt
  Pet0 <--"*" Order0
  Customer0 <--"*" Order0
  Pet0 *--"1" Pet0_category_xt
  Category <--"*" Pet1_category_xt
  Pet1 *--"1" Pet1_category_xt
  Customer_address_xa ..|> Address
  Pet_category_xt ..|> Category
  Pet_tags_xa ..|> Tag
  Pet1_category_xt ..|> Category
  class User
  class ApiResponse
  Order: +post(/store/order)
  Customer: +post(/store/customer)
  Category: +post(/category)
  Category: +post(/backid/category)
  User: +post(/user)
  User: +put(/user/{username})
  Pet: +post(/pet)
  Pet: +put(/pet)
  Pet: +post(/backid/pet)
  Customer0: +post(/store/customer0)
  Order0: +post(/store/order0)
  Pet0: +post(/pet0)
  Pet1: +post(/pet1)
```

- Market (/src/test/resources/market/schema-marketWithoutArrays.md)

```mermaid
classDiagram
  UserDTOReq <--"*" CartDTO
  CartDTO <--"*" CartItemDTOReq
  ProductDTOReq <--"*" CartItemDTOReq
  ProductDTORes <--"*" CartItemDTORes
  CartDTO <--"*" CartItemDTORes
  UserDTOReq <--"*" ContactsDTOReq
  UserDTORes <--"*" ContactsDTORes
  RegionDTOReq <--"*" DistilleryDTOReq
  RegionDTORes <--"*" DistilleryDTORes
  CreditCardDTO <--"*" OrderDTO
  UserDTOReq <--"*" OrderDTO
  DistilleryDTOReq <--"*" ProductDTOReq
  DistilleryDTORes <--"*" ProductDTORes
  CartItemDTOReq: +put(/customer/cart)
  ContactsDTOReq: +put(/customer/contacts)
  CreditCardDTO: +post(/customer/cart/pay)
  DistilleryDTOReq: +post(/distillerydto)
  ProductDTOReq: +post(/products/productdto)
  RegionDTOReq: +post(/regiondto)
  UserDTOReq: +post(/register)
```

- Gestao Hospital (src/test/resources/gestaoHospital/schema-hospital.md)

```mermaid
classDiagram
  GeoJsonPoint *--"*" GeoJsonPoint_coordinates_xa
  Location_location_xt *--"*" Location_location_xt_coordinates_xa
  GeoJsonPoint <--"*" Location_location_xt
  Location_position_xt *--"*" Location_position_xt_coordinates_xa
  GeoJsonPoint <--"*" Location_position_xt
  Patient_location_xt_location_xt *--"*" Patient_location_xt_location_xt_coordinates_xa
  GeoJsonPoint <--"*" Patient_location_xt_location_xt
  Patient_location_xt_position_xt *--"*" Patient_location_xt_position_xt_coordinates_xa
  GeoJsonPoint <--"*" Patient_location_xt_position_xt
  Location <--"*" Patient_location_xt
  Patient_location_xt *--"1" Patient_location_xt_location_xt
  Patient_location_xt *--"1" Patient_location_xt_position_xt
  HospitalDTO <--"*" Patient
  Patient *--"1" Patient_location_xt
  HospitalDTO <--"*" ProductDTO
  Location *--"1" Location_location_xt
  Location *--"1" Location_position_xt
  Location_location_xt ..|> GeoJsonPoint
  Location_position_xt ..|> GeoJsonPoint
  Patient_location_xt_location_xt ..|> GeoJsonPoint
  Patient_location_xt_position_xt ..|> GeoJsonPoint
  Patient_location_xt ..|> Location
  class LocationDTO
  HospitalDTO: +post(/v1/hospitais/)
  HospitalDTO: +put(/v1/hospitais/{hospital_id})
  Patient: +post(/v1/hospitais/{hospital_id}/pacientes/checkin)
  Patient: +put(/v1/hospitais/{hospital_id}/pacientes/{patientId})
  ProductDTO: +post(/v1/hospitais/{hospital_id}/estoque)
  ProductDTO: +put(/v1/hospitais/{hospital_id}/estoque/{produto_id})
```
