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
  namespace post__customer_orders_orderdto {
    class OrderDTOReq
    class OrderDTORes
  }
  namespace post__orderedproductdto {
    class OrderedProductDTOReq
    class OrderedProductDTORes
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
  CreditCardDTO <--"*" OrderDTOReq
  UserDTORes <--"*" OrderDTOReq
  CreditCardDTO <--"*" OrderDTORes
  UserDTORes <--"*" OrderDTORes
  OrderDTO <--"*" OrderedProductDTO
  ProductDTORes <--"*" OrderedProductDTO
  OrderDTOReq <--"*" OrderedProductDTOReq
  ProductDTOReq <--"*" OrderedProductDTOReq
  OrderDTORes <--"*" OrderedProductDTORes
  ProductDTORes <--"*" OrderedProductDTORes
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
  OrderDTOReq: +post(/customer/orders/orderdto)
  OrderDTORes: +post(/customer/orders/orderdto)
  OrderedProductDTOReq: +post(/orderedproductdto)
  OrderedProductDTORes: +post(/orderedproductdto)
  ProductDTOReq: +post(/products/productdto)
  ProductDTORes: +post(/products/productdto)
  RegionDTOReq: +post(/regiondto)
  RegionDTORes: +post(/regiondto)
  UserDTOReq: +post(/register)
  UserDTORes: +post(/register)