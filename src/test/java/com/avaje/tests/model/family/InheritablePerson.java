package com.avaje.tests.model.family;

import com.avaje.tests.model.basic.EBasic;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class InheritablePerson {

  @Id
  private Integer identifier;

  private String name;

  private Integer age;

  @ManyToOne
  private EBasic someBean;

  public Integer getIdentifier() {
    return identifier;
  }

  public void setIdentifier(Integer identifier) {
    this.identifier = identifier;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public void setSomeBean(EBasic someBean) {
    this.someBean = someBean;
  }

  public EBasic getSomeBean() {
    return someBean;
  }

}
