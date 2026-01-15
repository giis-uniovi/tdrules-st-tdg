package market.dao;

import market.domain.Distillery;
import market.domain.Product;
import market.domain.Region;
import market.dto.ProductTotalDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProductDAO extends CrudRepository<Product, Long>, JpaRepository<Product, Long> {

	Page<Product> findByDistilleryOrderByName(Distillery distillery, Pageable request);

	@Query(value = "SELECT p FROM Product p WHERE p.distillery IN (SELECT d FROM Distillery d WHERE d.region = :region) order by p.name")
	Page<Product> findByRegionOrderByName(@Param("region") Region region, Pageable request);

	Page<Product> findByAvailableOrderByName(boolean available, Pageable request);
	
	// testing: add to get the total number of sold products
	@Query("SELECT p.id as id, p.name as name, SUM(op.quantity) as totalQuantity " +
	       "FROM OrderedProduct op JOIN op.product p " +
	       "GROUP BY p.id, p.name")
	List<ProductTotalDTO> findTotalSoldProducts();

	// testing: add to get the total sales grouped by region and distillery
	@Query("SELECT r.id as regionId, r.name as regionName, d.id as distilleryId, d.title as distilleryTitle, SUM(op.quantity) as totalSold " +
	       "FROM OrderedProduct op JOIN op.product p JOIN p.distillery d JOIN d.region r " +
	       "GROUP BY r.id, r.name, d.id, d.title")
	List<market.dto.SalesByRegionDistilleryDTO> findSalesByRegionDistillery();

}