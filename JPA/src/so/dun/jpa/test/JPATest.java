package so.dun.jpa.test;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hibernate.jpa.QueryHints;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import so.dun.jpa.helloword.Category;
import so.dun.jpa.helloword.Customer;
import so.dun.jpa.helloword.Department;
import so.dun.jpa.helloword.Item;
import so.dun.jpa.helloword.Manager;
import so.dun.jpa.helloword.Order;

public class JPATest {
	
	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	private EntityTransaction transaction;
	
	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory("JPA");
		entityManager = entityManagerFactory.createEntityManager();
		transaction = entityManager.getTransaction();
		transaction.begin();
	}
	
	@After
	public void destroy() {
		transaction.commit();
		entityManager.close();
		entityManagerFactory.close();
	}
	
	/**
	 * 类似于Hibernate中Session的get方法
	 */
	@Test
	public void testFind() {
		Customer customer = entityManager.find(Customer.class, 1);
		System.out.println("--------------------------------------------");
		System.out.println(customer);
	}
	
	/**
	 * 类似于Hibernate中Session的load方法
	 */
	@Test
	public void testGetReference() {
		Customer customer = entityManager.getReference(Customer.class, 1);
		System.out.println(customer.getClass().getName());
		System.out.println("--------------------------------------------");
		System.out.println(customer);
	}
	
	/**
	 * 类似于hibernate的save方法。使对象由临时状态变为持久化状态
	 * 和Hibernate的save方法的不同之处：若对象有id，则不能执行insert操作，而会抛出异常
	 */
	@Test
	public void testPersistence(){
		Customer customer = new Customer();
		customer.setAge(20);
		customer.setEmail("admin@dun.so");
		customer.setLastName("顿搜");
		customer.setCreatedTime(new Date());
		customer.setBirth(new Date());
		entityManager.persist(customer);
		System.out.println(customer.getId());
	}
	
	/**
	 * 类似于hibernate的delete方法，把对象对应的记录从数据库中移除
	 * 但注意：该方法只能移除持久化对象，而hibernate的delete方法实际上还可以移除游离对象。
	 */
	@Test
	public void testRemove() {
//		Customer customer= new Customer();
//		customer.setId(6);
		Customer customer = entityManager.find(Customer.class, 2);
		entityManager.remove(customer);
	}
	
	/**
	 * 若传入的是一个临时对象
	 * 会创建一个新的对象，把临时对象的属性复制到新的对象中，然后对新的对象执行持久化操作。
	 * 所以新的对象中有id，但以前的零食对象中没有id。
	 */
	@Test
	public void testMerge1() {
		Customer customer= new Customer();
		customer.setAge(21);
		customer.setEmail("admin@dun.so");
		customer.setLastName("顿搜");
		customer.setCreatedTime(new Date());
		customer.setBirth(new Date());
		Customer customer2 = entityManager.merge(customer);
		
		System.out.println("customer#id"+customer.getId());
		System.out.println("customer2#id"+customer2.getId());
	}
	
	/**
	 * 若传入的是一个游离对象，即传入的对象由OID
	 * 1、若在EntityManager缓存中没有该对象
	 * 2、若在数据库中也没有对应的记录
	 * 3、JPA会创建一个新对象，然后把当前游离对象的属性复制到新创建的对象中
	 */
	@Test
	public void testMerge2() {
		Customer customer= new Customer();
		customer.setAge(21);
		customer.setEmail("admin@dun.so");
		customer.setLastName("顿搜");
		customer.setCreatedTime(new Date());
		customer.setBirth(new Date());
		customer.setId(100);
		Customer customer2 = entityManager.merge(customer);
		System.out.println("customer#id: "+customer.getId());
		System.out.println("customer2#id: "+customer2.getId());
	}
	
	/**
	 * 若传入的是一个游离对象，即传入的对象由OID
	 * 1、若在EntityManager缓存中没有该对象
	 * 2、若在数据库中有对应的记录
	 * 3、JPA会查询对应记录，返回该记录对应的对象，然后会把游离对象的属性复制到查询到的对象中
	 * 4、对查询到的对象执行update操作
	 */
	@Test
	public void testMerge3() {
		Customer customer= new Customer();
		customer.setAge(21);
		customer.setEmail("admin@dun.so");
		customer.setLastName("顿搜");
		customer.setCreatedTime(new Date());
		customer.setBirth(new Date());
		customer.setId(1);
		Customer customer2 = entityManager.merge(customer);
		System.out.println(customer == customer2);
	}
	
	/**
	 * 若传入的是一个游离对象，即传入的对象由OID
	 * 1、若在EntityManager缓存中有相应对象
	 * 2、JPA会将游离对象的属性复制到EntityManager缓存中相应对象中
	 * 3、对EntityManager缓存中相应对象执行update
	 */
	@Test
	public void testMerge4() {
		Customer customer= new Customer();
		customer.setAge(21);
		customer.setEmail("admin@dun.so");
		customer.setLastName("顿搜");
		customer.setCreatedTime(new Date());
		customer.setBirth(new Date());
		customer.setId(1);
		Customer customer2 = entityManager.find(Customer.class, 1);
		entityManager.merge(customer);
		System.out.println(customer == customer2);
	}
	
	/**
	 * 同HIbernate中Session的flush方法
	 */
	@Test
	public void testFlust() {
		Customer customer = entityManager.find(Customer.class, 1);
		System.out.println(customer);
		customer.setLastName("dunsoo");
		entityManager.flush();
	}
	
	/**
	 * 同Hibernate中Session的refresh方法
	 */
	@Test
	public void testRefresh() {
		Customer customer = entityManager.find(Customer.class, 1);
		customer = entityManager.find(Customer.class, 1);
		entityManager.refresh(customer);
	}
	
	/**
	 * 保存多对一时，建议先保存1的一段，后保存n的一段，这样不会多出额外的update语句
	 */
/*	@Test
	public void testManyToOnePersist() {
		Customer customer= new Customer();
		customer.setAge(22);
		customer.setEmail("admin@dun.so");
		customer.setLastName("顿搜");
		customer.setCreatedTime(new Date());
		customer.setBirth(new Date());
		
		Order order1 = new Order();
		order1.setOrderName("order1");
		
		Order order2 = new Order();
		order2.setOrderName("order2");
		
		//设置关联关系
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//执行保存操作
		entityManager.persist(customer);
		entityManager.persist(order1);
		entityManager.persist(order2);
	}*/
	
	/**
	 * 默认情况下，使用左外连接的方式来获取n的一端的对象和其关联的1的一端的对象
	 */
/*	@Test
	public void testManyToOneFind() {
		Order order = entityManager.find(Order.class, 16);
		System.out.println(order.getOrderName());
		System.out.println(order.getCustomer().getLastName());
	}*/
	
	/**
	 * 不能直接删除1的一端，因为有外键约束
	 */
	@Test
	public void testManyToOneRemove() {
		Order order = entityManager.find(Order.class, 16);
		entityManager.remove(order);
	}
	
	/*@Test
	public void testManyToOneUpdate() {
		Order order = entityManager.find(Order.class, 16);
		order.getCustomer().setLastName("dunso");
	}*/
	
	
	/**
	 * 单向1-n关联关系执行保存时，一定会多出UPDATE语句，
	 * 因为n的一端在插入时不会同时插入外键列
	 */
	@Test
	public void oneToManyPerist() {
		Customer customer= new Customer();
		customer.setAge(22);
		customer.setEmail("admin@dun.so");
		customer.setLastName("顿搜");
		customer.setCreatedTime(new Date());
		customer.setBirth(new Date());
		
		Order order1 = new Order();
		order1.setOrderName("order1");
		
		Order order2 = new Order();
		order2.setOrderName("order2");
		
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		
		//执行保存操作
		entityManager.persist(customer);
		entityManager.persist(order1);
		entityManager.persist(order2);
	}
	
	/**
	 * 默认对关联的多的一方使用懒加载的加载策略。
	 */
	@Test
	public void testOneToManyFind() {
		Customer customer = entityManager.find(Customer.class,18);
		System.out.println(customer.getLastName());
		System.out.println(customer.getOrders().size());
	}
	
	/**
	 * 默认情况下，若删除1的一端，则会把n的一端的外键置空，然后进行删除
	 * 可以通过@OneToMany的cascade属性来修改默认的删除策略
	 */
	@Test
	public void testOneToManyRemove() {
		Customer customer = entityManager.find(Customer.class,18);
		entityManager.remove(customer);
	}
	
	@Test
	public void testOneToManyUpdate() {
		Customer customer = entityManager.find(Customer.class,18);
		customer.getOrders().iterator().next().setOrderName("dunso");
	}
	
	
	/**
	 * 若是双向1-n的关联关系，执行保存时
	 * 若先保存n的一端，再保存1的一端，默认情况下，会多出了2n条UPDATE语句。
	 * 若先保存1的一端，则会多出了n条UPDATE语句
	 * 在进行双线1-n的关联关系时，建议使用n的一方来维护关联关系，而1的一方不维护关联关系，这样会有效的较少SQL语句。
	 * 注意：若在1的一端的@OneToMany中使用mappedBy属性，则@OneToMany端就不能再使用@JoinColumn属性
	 */
	@Test
	public void TwoSideoneToManyPerist() {
		Customer customer= new Customer();
		customer.setAge(22);
		customer.setEmail("admin@dun.so");
		customer.setLastName("顿搜");
		customer.setCreatedTime(new Date());
		customer.setBirth(new Date());
		
		Order order1 = new Order();
		order1.setOrderName("order1");
		
		Order order2 = new Order();
		order2.setOrderName("order2");
		
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//执行保存操作
		entityManager.persist(customer);
		entityManager.persist(order1);
		entityManager.persist(order2);
	}
	//双向1-1的关联关系，建议先保存不维护关联关系的一方，即没有外键的一方，这样不会多出update语句
	@Test
	public void testOneToOnePersistence() {
		Manager mgr = new Manager();
		mgr.setMgrName("dunso");
		
		Department dept = new Department();
		dept.setDeptName("IT");
		
		mgr.setDept(dept);
		dept.setMgr(mgr);
		
		entityManager.persist(mgr);
		entityManager.persist(dept);
	}
	
	/**
	 * 默认情况下，若获取维护关联关系的一方，则会通过左外连接获取其关联的对象
	 * 但可以通过@OnetoOne的fetch属性来修改加载策略
	 */
	@Test
	public void testOneToOneFind1() {
		Department dept = entityManager.find(Department.class, 22);
		System.out.println(dept.getDeptName());
		System.out.println(dept.getMgr().getClass().getName());
	}
	
	/**
	 * 默认情况下，若获取不维护关联关系的一方，则也会通过左外连接获取其关联的对象
	 * 通过@OnetoOne的fetch属性来修改加载策略,但依然会再发送SQL语句来初始化其关联的对象
	 * 这说明在不维护关联关系的一方，不建议修改fetch属性
	 */
	@Test
	public void testOneToOneFind2() {
		Manager mgr = entityManager.find(Manager.class, 21);
		System.out.println(mgr.getMgrName());
		System.out.println(mgr.getDept().getClass().getName());
	}
	
	@Test
	public void testManyToManyPersist() {
		Item i1 = new Item();
		i1.setItemName("i-1");
		Item i2 = new Item();
		i2.setItemName("i-2");
		
		Category c1 =new Category();
		c1.setCategoryName("c-1");
		Category c2 =new Category();
		c2.setCategoryName("c-2");
		
		//设置关联关系
		i1.getCategories().add(c1);
		i1.getCategories().add(c2);
		i2.getCategories().add(c1);
		i2.getCategories().add(c2);
		
		c1.getItems().add(i1);
		c1.getItems().add(i2);
		c2.getItems().add(i1);
		c2.getItems().add(i2);
		
		//执行保存
		entityManager.persist(i1);
		entityManager.persist(i2);
		entityManager.persist(c1);
		entityManager.persist(c2);
	}
	
	/**
	 * 对于关联的集合对象，默认使用懒加载的策略
	 * 使用维护关联关系的一方获取，还是使用不维护关联关系的一方获取，SQL语句相同
	 */
	@Test
	public void testManyToManyFind() {
		/*Item item = entityManager.find(Item.class, 36);
		System.out.println(item.getItemName());
		System.out.println(item.getCategories().size());*/
		
		Category category = entityManager.find(Category.class, 38);
		System.out.println(category.getCategoryName());
		System.out.println(category.getItems().size());
	}
	
	@Test
	public void testSecondLevelCache(){
		Customer customer1 = entityManager.find(Customer.class, 1);
		transaction.commit();
		entityManager.close();
		
		entityManager = entityManagerFactory.createEntityManager();
		transaction = entityManager.getTransaction();
		transaction.begin();
		Customer customer2 = entityManager.find(Customer.class, 1);
		System.out.println(customer1);
		System.out.println(customer2);
	}
	
	@Test
	public void testHelloJPQL() {
		String jpql ="FROM Customer c WHERE c.age > ?";
		Query query =entityManager.createQuery(jpql);
		//占位符的索引是从1开始的
		query.setParameter(1, 1);
		@SuppressWarnings("unchecked")
		List<Customer> customers = query.getResultList();
		System.out.println(customers.size());
	}
	
	/**
	 * 默认情况下，若只查询部分属性，则将返回Object[]类型的结果，或者Object[]类型的List
	 * 也可以在实体类中创建对应的构造器，然后再JPQL语句中利用对应的构造器返回实体类的对象
	 */
	@Test
	public void testPartlyProperties() {
		String jpql = "SELECT new Customer(c.lastName,c.age) from Customer c WHERE c.id > ? ";
		@SuppressWarnings("rawtypes")
		List result = entityManager.createQuery(jpql).setParameter(1,1).getResultList();
		System.out.println(result);
	}
	
	/**
	 * createNamedQuery适用于在实体类前使用@NamedQuery标记的查询语句
	 */
	@Test
	public void testNamedQuery(){
		Query query = entityManager.createNamedQuery("testNamedQuery").setParameter(1, 3);
		Customer customer = (Customer) query.getSingleResult();
		System.out.println(customer);
	}
	
	
	/**
	 * createNativeQuery适用于本地SQL
	 */
	@Test
	public void testNativeQuery() {
		String sql = "SELECT age FROM jpa_customers WHERE id = ? ";
		Query query = entityManager.createNativeQuery(sql).setParameter(1, 3);
		Object result = query.getSingleResult();
		System.out.println(result);
	}
	
	/**
	 * 使用Hibernate的查询缓存（当然配置文件中要配置启用查询缓存）
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testQueryCache() {
		String jpql ="FROM Customer c WHERE c.age > ?";
		Query query =entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);
		//占位符的索引是从1开始的
		query.setParameter(1, 1);
		List<Customer> customers = query.getResultList();
		System.out.println(customers.size());
		
		
		query =entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);
		//占位符的索引是从1开始的
		query.setParameter(1, 1);
		customers = query.getResultList();
		System.out.println(customers.size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testOrderBy() {
		String jpql ="FROM Customer c WHERE c.age > ? ORDER BY c.age DESC";
		Query query =entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);
		//占位符的索引是从1开始的
		query.setParameter(1, 1);
		List<Customer> customers = query.getResultList();
		System.out.println(customers.size());
	}
	
	/**
	 * 查询order数量大于2的那些Customer
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGrpupBy() {
		String jpql = "SELECT o.customer FROM Order o GROUP BY o.customer HAVING count(o.id) > 1";
		List<Customer> customers = entityManager.createQuery(jpql).getResultList();
		System.out.println(customers);
	}
	
	/**
	 * JPQL的关联查询同HQL的关联查询
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testLeftOuterJoinFetch() {
		String jpql = "FROM Customer c LEFT OUTER JOIN FETCH c.orders WHERE c.id = ?";
		Customer customer = (Customer) entityManager.createQuery(jpql).setParameter(1, 27).getSingleResult();
		System.out.println(customer.getLastName());
		System.out.println(customer.getOrders().size());
		
		jpql = "FROM Customer c LEFT OUTER JOIN  c.orders WHERE c.id = ?";
		List<Object[]> result = entityManager.createQuery(jpql).setParameter(1, 27).getResultList();
		System.out.println(result);
	}
	
	/**
	 * 查询所有的Customer的lastname为dunso的Order
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSubQuery() {
		String jpql = "SELECT o FROM Order o WHERE o.customer = "
				+ "(SELECT c FROM Customer c WHERE c.lastName = ?) ";
		Query query = entityManager.createQuery(jpql).setParameter(1, "dunso");
		List<Order> orders = query.getResultList();
		System.out.println(orders.size());
	}
	
	/**
	 * 使用JPQL内建的函数
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testJpqlFunction() {
		String jpql = "SELECT upper(c.email) FROM Customer c";
		List<String> emails = entityManager.createQuery(jpql).getResultList();
		System.out.println(emails);
	}
	
	/**
	 * 可以使用JPQL完成UPDATE和DELETE操作
	 */
	@Test
	public void testExecuteUpdate() {
		String jpql ="UPDATE Customer c SET c.lastName = ? WHERE c.id = ?";
		Query query = entityManager.createQuery(jpql).setParameter(1, "dunso").setParameter(2, 27);
		query.executeUpdate();
	}
	
	
	
	
	
	
	
	
	
	
}
