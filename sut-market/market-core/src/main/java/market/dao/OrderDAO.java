package market.dao;

import market.domain.Order;
import market.domain.Product;
import market.domain.Region;
import market.domain.UserAccount;
import market.dto.AddressOrderDTO;
import market.dto.OrderDTO;
import market.dto.UserOrderTotalDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrderDAO extends CrudRepository<Order, Long>, JpaRepository<Order, Long> {

	List<Order> findByUserAccountOrderByDateCreatedDesc(UserAccount userAccount);

	Page<Order> findByExecuted(boolean stored, Pageable pageable);

	Page<Order> findByDateCreatedGreaterThan(Date created, Pageable pageable);

	Page<Order> findByExecutedAndDateCreatedGreaterThan(boolean executed, Date created, Pageable pageable);
	
	// testing: add to get the total costs of an user's order
	@Query("SELECT u.email as email, COUNT(o.id) as count, SUM(o.productsCost) as totalAmount " +
           "FROM Order o JOIN o.userAccount u " +
           "GROUP BY u.email")
    List<UserOrderTotalDTO> findTotalAmountByUser();
	
	// testing: add to get the total costs of non executed orders of users
	@Query("SELECT u.email as email, COUNT(o.id) as count, SUM(o.productsCost) as totalAmount " +
	       "FROM Order o JOIN o.userAccount u "+ 
	       "WHERE o.executed = '0' " +
	       "GROUP BY u.email")
	List<UserOrderTotalDTO> findTotalNonExecutedOrdersByUser();
		
	
	// testing: add to get orders with delivery included/not included executed/not executed
	@Query("SELECT c.address as address, COUNT(o.id) as count " +
           "FROM Order o JOIN o.userAccount u JOIN u.contacts c " +
           "WHERE o.deliveryIncluded = :included AND o.executed = :executed " +
           "GROUP BY c.address")
    List<AddressOrderDTO> findOrdersSameAddress(@Param("included") boolean included, @Param("executed") boolean executed);
	

}