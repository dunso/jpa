package so.dun.jpa.helloword;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;


public class Main {

	public static void main(String[] args) {
		
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("hibernate.show_sql", false);
		
		//1、创建EntitymanagerFactory
		String persistenceUnitName = "JPA";
		//EntityManagerFactory entitymanagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName); 
		EntityManagerFactory entitymanagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName,properties); 
		
		//2、创建EntityManager,类似于Hibernate的SessionFactory
		EntityManager entityManager = entitymanagerFactory.createEntityManager();
			
		//3、开启事务
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		//4、进行持久化操作
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setEmail("admin@dun.so");
		customer.setLastName("顿搜");
		customer.setCreatedTime(new Date());
		customer.setBirth(new Date());
		
		entityManager.persist(customer);
		
		//5、提交事务
		transaction.commit();
		
		//6、关闭EntityManager
		entityManager.close();
		
		//7、关闭EntityManagerFactory
		entitymanagerFactory.close();

	}

}
