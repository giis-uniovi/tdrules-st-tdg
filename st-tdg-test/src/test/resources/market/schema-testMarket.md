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