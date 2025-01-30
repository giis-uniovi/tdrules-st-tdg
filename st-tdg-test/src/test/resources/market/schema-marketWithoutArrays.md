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
  RegionDTOReq: +post(/regiondto)
  RegionDTORes: +post(/regiondto)
  UserDTOReq: +post(/register)
  UserDTORes: +post(/register)