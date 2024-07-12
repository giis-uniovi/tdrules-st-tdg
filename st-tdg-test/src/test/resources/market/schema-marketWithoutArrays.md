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