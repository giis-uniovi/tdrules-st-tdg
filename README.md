[![Status](https://github.com/giis-uniovi/tdrules-st-tdg/actions/workflows/test.yml/badge.svg)](https://github.com/giis-uniovi/tdrules-st-tdg/actions)
[![Test Reports](https://img.shields.io/badge/%20-Test_Reports-orange)](https://giis-uniovi.github.io/tdrules-st-tdg)

# Test Data Generation for APIs - System Tests

This project contains the system and integration tests of *Test Data Generation for APIs* (TDG).
Includes the required test classes, result files and the SUT projects,
along with an additional module to evaluate the mutation
score of the tests for one of the SUTs.

## Systems Under Test (SUTs)

- Swagger Petstore (`sut-petstore` folder).
  This is a detached fork of https://github.com/swagger-api/swagger-petstore at v1.0.17 (2022-09-30) with some additions
- Market (`sut-market` folder).
  This is a detached fork of https://github.com/aleksey-lukyanets/market (2022-02-07) with some additions
- Gestao Hospital (`sut-gestaoHospital` folder). 
  This is a detached fork of https://github.com/ValchanOficial/GestaoHospital (2023-07-05) with some additions

## Structure of this project

- Module `st-tdg-test`:
  - Java tests (`src/test/java`) for each SUT.
  - Files for test result comparison (`src/test/resources`)
  - Clasess `*Petstore0*` contain some commented examples to illustrate
    some details of the TDG approach.
  - There is a common base class for all tests (`BaseAll`) and 
    Each namespace has an additional base class
    with the particular configuration of each SUT.
- A folder for each of the SUTs that are tested in this projects.
- A folder `setup` with scripts to run each SUT (backend and databases).
- Module `st-tdg-eval`: To evaluate the effectiveness of the test data my measuring
  the mutation score of the tests using PIT (https://pitest.org/).

## How to execute the System Tests

The tests are in the `st-tdg-test` module.

To run the tests of a SUT in your local development environment:
  - Ensure that the port bindings of each SUT are not used by other application
  - Run the server from the `setup` folder and wait until it is up
  - Execute the tests in the namespace that corresponds with the SUT
  - To execute this module from maven: `mvn -pl st-tdg-test test` 

Port reservation and endpoints: To avoid conflicts when running in local,
different host port are reserved for each SUT.
Below are the ports and the main endpoint urls to check that SUTs are working:
  - Petstore: 8081:8080 (api) |
    [api example](http://localhost:8081/api/v3/pet/findByStatus?status=available) |
    [swagger-ui](http://localhost:8081/) |
    [api-docs](http://localhost:8081/api/v3/openapi.json)
  - Market: 8082 (web), 8083 (api), 8087 (db)
    [api example](http://localhost:8083/products) |
    [swagger-ui](http://localhost:8083/swagger-ui/index.html) |
    [api-docs](http://localhost:8083/v2/api-docs)
  - Gestao Hospital: 8085:8080 (api), 8086:27017 (default mongo) (db) |
    [api example](http://localhost:8085/v1/hospitais/) |
    [api-docs](http://localhost:8085/v2/api-docs)

The whole sequence of tests for all SUTs is run when executed in CI

## How to evaluate the Mutation Score

The tests are located in the `st-tdg-eval` module.
Run this maven command:
```
mvn -pl st-tdg-eval test-compile org.pitest:pitest-maven:mutationCoverage
```

Test results of the module are in the `target` folder, 
and the mutation report in `target/pit-reports`

## Graphical TDM models

- Swagger Petstore 
([open in mermaid.live](https://mermaid.live/edit/?code=https://github.com/giis-uniovi/tdrules-st-tdg/blob/main/st-tdg-test/src/test/resources/petstore/schema-petstore.md))

```mermaid
---
  config:
    class:
      hideEmptyMembersBox: true
---
classDiagram
  Pet <--"*" Order
  Customer <--"*" Order
  Customer *--"*" Customer_address_xa
  Category <--"*" Pet_category_xt
  Pet *--"1" Pet_category_xt
  Pet *--"*" Pet_photoUrls_xa
  Pet *--"*" Pet_tags_xa
  Pet0 <--"*" Order0
  Customer0 <--"*" Order0
  Pet0 *--"1" Pet0_category_xt
  Category <--"*" Pet1_category_xt
  Pet1 *--"1" Pet1_category_xt
  Pet <--"*" ApiResponse
  Customer_address_xa ..|> Address
  Pet_category_xt ..|> Category
  Pet_tags_xa ..|> Tag
  Pet1_category_xt ..|> Category
  class User
  Order: +post(/store/order)
  Customer: +post(/store/customer)
  Category: +post(/category)
  Category: +post(/backid/category)
  User: +post(/user)
  User: +post(/user/createWithList)
  User: +put(/user/{username})
  Pet: +post(/pet)
  Pet: +post(/backid/pet)
  Pet: +put(/pet)
  Customer0: +post(/store/customer0)
  Order0: +post(/store/order0)
  Pet0: +post(/pet0)
  Pet1: +post(/pet1)
  ApiResponse: +post(/pet/{petId}/uploadImage)
```

- Market 
([open in mermaid.live](https://mermaid.live/edit/?code=https://github.com/giis-uniovi/tdrules-st-tdg/blob/main/st-tdg-test/src/test/resources/market/schema-testMarket.md))

```mermaid
---
  config:
    class:
      hideEmptyMembersBox: true
---
classDiagram
  namespace post__customer_cart_pay {
    class CreditCardDTO
    class OrderDTO
  }
  namespace post__distillerydto {
    class DistilleryDTOReq
    class DistilleryDTORes
  }
  namespace post__products_productdto {
    class ProductDTOReq
    class ProductDTORes
  }
  namespace post__regiondto {
    class RegionDTOReq
    class RegionDTORes
  }
  namespace post__register {
    class UserDTOReq
    class UserDTORes
  }
  namespace put__customer_cart {
    class CartDTO
    class CartItemDTOReq
  }
  namespace put__customer_cart_delivery {
    class CartDTO_r1
  }
  style CartDTO_r1 fill:#fff,stroke:#333,stroke-width:1px,stroke-dasharray: 5 5;
  namespace put__customer_contacts {
    class ContactsDTOReq
    class ContactsDTORes
  }
  UserDTORes <--"*" CartDTO
  CartDTO <--"*" CartItemDTOReq
  ProductDTOReq <--"*" CartItemDTOReq
  ProductDTORes <--"*" CartItemDTORes
  CartDTO <--"*" CartItemDTORes
  UserDTOReq <--"*" ContactsDTOReq
  UserDTORes <--"*" ContactsDTORes
  RegionDTOReq <--"*" DistilleryDTOReq
  RegionDTORes <--"*" DistilleryDTORes
  CreditCardDTO <--"*" OrderDTO
  UserDTORes <--"*" OrderDTO
  OrderDTO <--"*" OrderedProductDTO
  ProductDTORes <--"*" OrderedProductDTO
  DistilleryDTOReq <--"*" ProductDTOReq
  DistilleryDTORes <--"*" ProductDTORes
  CartDTO: +put(/customer/cart)
  CartDTO: +put(/customer/cart/delivery)
  CartItemDTOReq: +put(/customer/cart)
  ContactsDTOReq: +put(/customer/contacts)
  ContactsDTORes: +put(/customer/contacts)
  CreditCardDTO: +post(/customer/cart/pay)
  DistilleryDTOReq: +post(/distillerydto)
  DistilleryDTORes: +post(/distillerydto)
  OrderDTO: +post(/customer/cart/pay)
  ProductDTOReq: +post(/products/productdto)
  ProductDTORes: +post(/products/productdto)
  RegionDTOReq: +post(/regiondto)
  RegionDTORes: +post(/regiondto)
  UserDTOReq: +post(/register)
  UserDTORes: +post(/register)
```

- Gestao Hospital 
([open in mermaid.live](https://mermaid.live/edit/?code=https://github.com/giis-uniovi/tdrules-st-tdg/blob/main/st-tdg-test/src/test/resources/gestaoHospital/schema-hospital.md))

```mermaid
---
  config:
    class:
      hideEmptyMembersBox: true
---
classDiagram
  GeoJsonPoint <--"*" Location_location_xt
  Location_location_xt *--"*" Location_location_xt_coordinates_xa
  GeoJsonPoint <--"*" Location_position_xt
  Location_position_xt *--"*" Location_position_xt_coordinates_xa
  GeoJsonPoint <--"*" Patient_location_xt_location_xt
  Patient_location_xt_location_xt *--"*" Patient_location_xt_location_xt_coordinates_xa
  GeoJsonPoint <--"*" Patient_location_xt_position_xt
  Patient_location_xt_position_xt *--"*" Patient_location_xt_position_xt_coordinates_xa
  Location <--"*" Patient_location_xt
  Patient_location_xt *--"1" Patient_location_xt_location_xt
  Patient_location_xt *--"1" Patient_location_xt_position_xt
  HospitalDTO <--"*" Patient
  Patient *--"1" Patient_location_xt
  GeoJsonPoint *--"*" GeoJsonPoint_coordinates_xa
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
  Patient: +post(/v1/hospitais/{hospital_id}/pacientes/checkout)
  Patient: +put(/v1/hospitais/{hospital_id}/pacientes/{patientId})
  ProductDTO: +post(/v1/hospitais/estoque)
  ProductDTO: +post(/v1/hospitais/{hospital_id}/estoque)
  ProductDTO: +put(/v1/hospitais/{hospital_id}/estoque/{produto_id})
```
