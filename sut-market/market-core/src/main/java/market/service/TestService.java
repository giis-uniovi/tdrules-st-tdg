package market.service;

import java.sql.SQLException;

// Add a service for test: clean the database (deleteAll) and get all data (getAll)

public interface TestService {
	public void deleteAll() throws SQLException;
	public Object getAll();
}
