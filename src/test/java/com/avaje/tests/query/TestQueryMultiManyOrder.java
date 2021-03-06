package com.avaje.tests.query;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.tests.model.basic.Order;
import com.avaje.tests.model.basic.ResetBasicData;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestQueryMultiManyOrder extends BaseTestCase {

  @Test
  public void test() {

    ResetBasicData.reset();

    Query<Order> q = Ebean.find(Order.class).fetch("shipments").fetch("details")
      .fetch("details.product").fetch("customer").where().gt("id", 0).query();

    List<Order> list = q.findList();
    String sql = q.getGeneratedSql();

    Assert.assertTrue(!list.isEmpty());
    Assert.assertTrue(sql.contains("join o_customer "));

    Assert.assertFalse(sql.contains("left join contact "));
    Assert.assertFalse(sql.contains("left join o_order_detail "));
    Assert.assertFalse(sql.contains("left join o_product "));

  }
}
