package so.dun.jpa.helloword;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@NamedQuery(name="testNamedQuery",query="FROM Customer c WHERE c.id = ?")
@Cacheable(true)
@Table(name="JPA_CUSTOMERS")
@Entity
public class Customer {
	
	private Integer id;
	private String lastName;
	private String email;
	private int age;
	private Date createdTime;
	private Date birth;
	
	private Set<Order> orders = new HashSet<Order>();
	
	
	public Customer() {}
	
	public Customer(String lastName, int age) {
		super();
		this.lastName = lastName;
		this.age = age;
	}
	
//	@TableGenerator(name="ID_GENERATOR",table="jpa_id_generators",pkColumnName="PK_NAME",pkColumnValue="CUSTOMER_ID",valueColumnName="PK_VALUE",allocationSize=1)
//	@GeneratedValue(strategy=GenerationType.TABLE, generator="ID_GENERATOR")
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="LAST_NAME",length=50,nullable=false)
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	//没加注解默认是@Basic
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	
	/**
	 * 使用@OneToMany来映射单向一对多的映射关系
	 * 使用@JoinColumn 来映射外检列的名称
	 * 可以使用fetch属性来修改默认的懒加载策略
	 * 注意：若在1的一端的@OneToMany中使用mappedBy属性，则@OneToMany端就不能再使用@JoinColumn属性
	 */
	//@JoinColumn(name="CUSTOMER_ID")
	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.REMOVE},mappedBy="customer")
	public Set<Order> getOrders() {
		return orders;
	}
	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}
	//工具方法，不需要映射为数据表的一列
	@Transient
	public String getInfo(){
		return "lastName: " + lastName +" , email: " + email;
	}
	
	@Transient
	@Override
	public String toString() {
		return "Customer [id=" + id + ", lastName=" + lastName + ", email="
				+ email + ", age=" + age + ", createdTime=" + createdTime
				+ ", birth=" + birth + "]";
	}
	
	

}
