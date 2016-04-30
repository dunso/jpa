package so.dun.jpa.helloword;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name="JPA_MANAGERS")
@Entity
public class Manager {
	
	private Integer id;
	private String mgrName;
	private Department dept;
	
	@GeneratedValue
	@Id
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="MGR_NAME")
	public String getMgrName() {
		return mgrName;
	}
	public void setMgrName(String mgrName) {
		this.mgrName = mgrName;
	}
	
	/**
	 * 对于不维护关联往西，没有外键的一方，使用@OneToOne来进行映射，需要设置mapped=true
	 */
	@OneToOne(mappedBy="mgr")
	public Department getDept() {
		return dept;
	}
	public void setDept(Department dept) {
		this.dept = dept;
	}
	
	

}
