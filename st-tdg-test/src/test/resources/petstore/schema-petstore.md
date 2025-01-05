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
  Pet: +post(/backid/pet)
  Pet: +put(/pet)
  Customer0: +post(/store/customer0)
  Order0: +post(/store/order0)
  Pet0: +post(/pet0)
  Pet1: +post(/pet1)