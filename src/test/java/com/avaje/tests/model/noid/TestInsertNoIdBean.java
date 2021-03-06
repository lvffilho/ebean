package com.avaje.tests.model.noid;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestInsertNoIdBean extends BaseTestCase {

  @Test
  public void testInsert() {


    NoIdBean bean = new NoIdBean();
    bean.setName("Rocky");
    bean.setSubject("Blowing up stuff");

    Ebean.save(bean);

    int rowCount = Ebean.find(NoIdBean.class).findCount();

    assertTrue("rowCount:" + rowCount, rowCount > 0);

  }
}
