package market.service.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import market.dao.CartDAO;
import market.dao.ContactsDAO;
import market.dao.DistilleryDAO;
import market.dao.OrderDAO;
import market.dao.OrderedProductDAO;
import market.dao.ProductDAO;
import market.dao.RegionDAO;
import market.dao.UserAccountDAO;
import market.domain.Cart;
import market.domain.Contacts;
import market.domain.Distillery;
import market.domain.Order;
import market.domain.OrderedProduct;
import market.domain.Product;
import market.domain.Region;
import market.domain.UserAccount;
import market.service.TestService;

/* 
 * New class for test: clean the database (deleteAll) and get all data (getAll)
 */

@Service
public class TestServiceImpl implements TestService {
	
	@Autowired
    private ApplicationContext applicationContext;
	
	
	public class AllData implements Serializable {	
    	private static final long serialVersionUID = 1L;
    	public List<Cart> cart=new ArrayList<>();
    	public List<Contacts> contacts=new ArrayList<>();
    	public List<Distillery> distillery=new ArrayList<>();
    	public List<Order> order=new ArrayList<>();
    	public List<OrderedProduct> orderedProduct=new ArrayList<>();
    	public List<Product> product=new ArrayList<>();
    	public List<Region> region=new ArrayList<>();
    	public List<UserAccount> userAccount=new ArrayList<>();
    }
	
	private final CartDAO cartDAO;
	private final ContactsDAO contactsDAO;
	private final DistilleryDAO distilleryDAO;
	private final OrderDAO orderDAO;
	private final OrderedProductDAO orderedProductDAO;
	private final ProductDAO productDAO;
	private final RegionDAO regionDAO;
	private final UserAccountDAO userAccountDAO;
	
	public TestServiceImpl(CartDAO cartDAO, ContactsDAO contactsDAO, DistilleryDAO distilleryDAO, OrderDAO orderDAO, OrderedProductDAO orderedProductDAO, 
			ProductDAO productDAO,RegionDAO regionDAO, UserAccountDAO userAccountDAO) {
		this.cartDAO= cartDAO;
		this.contactsDAO= contactsDAO;
		this.distilleryDAO= distilleryDAO;
		this.orderDAO=orderDAO;
		this.orderedProductDAO=orderedProductDAO;
		this.productDAO=productDAO;
		this.regionDAO= regionDAO;
		this.userAccountDAO= userAccountDAO;
	}
	
	@Override
	public void deleteAll() throws SQLException {
		orderedProductDAO.deleteAll();
		orderDAO.deleteAll();
		cartDAO.deleteAll();
		contactsDAO.deleteAll();
		userAccountDAO.deleteAll();
		productDAO.deleteAll();
		distilleryDAO.deleteAll();
		regionDAO.deleteAll();
		DbTestUtil.resetAutoIncrementColumns(applicationContext, 
				"bill",
				"ordered_product",
				"customer_order",
				"cart_item",
				"cart", 
				"contacts", 
				"user_account", 
				"product", 
				"distillery", 
				"region");	
	}
	
	@Override
	public Object getAll() {
		AllData data = new AllData();
		
		data.region=regionDAO.findAll();
		data.distillery=distilleryDAO.findAll();
		data.product=productDAO.findAll();
		data.userAccount=userAccountDAO.findAll();
		data.contacts=contactsDAO.findAll();
		data.cart= cartDAO.findAll();
		data.order=orderDAO.findAll();
		data.orderedProduct=orderedProductDAO.findAll();

		return data;
	}
}
