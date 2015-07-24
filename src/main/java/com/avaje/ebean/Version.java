package com.avaje.ebean;

import java.sql.Timestamp;

/**
 * Wraps a version of a @History bean.
 */
public class Version<T> {

  /**
   * The version of the bean.
   */
  T bean;

  /**
   * The effective start date time of this version.
   */
  Timestamp start;

  /**
   * The effective end date time of this version.
   */
  Timestamp end;

  /**
   * Construct with bean and an effective date time range.
   */
  public Version(T bean, Timestamp start, Timestamp end) {
    this.bean = bean;
    this.start = start;
    this.end = end;
  }

  /**
   * Default constructor - useful for JSON tools such as Jackson.
   */
  public Version() {
  }

  /**
   * Return the bean instance for this version.
   */
  public T getBean() {
    return bean;
  }

  /**
   * Set the bean instance for this version.
   */
  public void setBean(T bean) {
    this.bean = bean;
  }

  /**
   * Return the effective start date time of this version.
   */
  public Timestamp getStart() {
    return start;
  }

  /**
   * Set the effective start date time of this version.
   */
  public void setStart(Timestamp start) {
    this.start = start;
  }

  /**
   * Return the effective end date time of this version.
   */
  public Timestamp getEnd() {
    return end;
  }

  /**
   * Set the effective end date time of this version.
   */
  public void setEnd(Timestamp end) {
    this.end = end;
  }
}
