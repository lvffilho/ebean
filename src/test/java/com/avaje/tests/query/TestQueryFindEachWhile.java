package com.avaje.tests.query;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Query;
import com.avaje.tests.model.basic.Customer;
import com.avaje.tests.model.basic.ResetBasicData;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class TestQueryFindEachWhile extends BaseTestCase {

  @Test
  public void test() {

    ResetBasicData.reset();

    EbeanServer server = Ebean.getServer(null);

    Query<Customer> query
      = server.find(Customer.class)
      .setAutoTune(false)
      .fetch("contacts", new FetchConfig().query(2)).where().gt("id", 0).orderBy("id")
      .setMaxRows(2);

    final AtomicInteger counter = new AtomicInteger(0);

    query.findEachWhile(customer -> {
      counter.incrementAndGet();
      customer.getName();
      return true;
    });

    Assert.assertEquals(2, counter.get());
  }

  /**
   * Test the behaviour when an exception is thrown inside the findVisit().
   */
  @Test(expected = IllegalStateException.class)
  public void testVisitThrowingException() {

    ResetBasicData.reset();

    EbeanServer server = Ebean.getServer(null);

    Query<Customer> query = server.find(Customer.class).setAutoTune(false)
      .fetch("contacts", new FetchConfig().query(2)).where().gt("id", 0).orderBy("id")
      .setMaxRows(2);

    final AtomicInteger counter = new AtomicInteger(0);

    query.findEachWhile(customer -> {
      counter.incrementAndGet();
      if (counter.intValue() > 0) {
        throw new IllegalStateException("cause a failure");
      }
      return true;
    });

    Assert.assertFalse("Never get here - exception thrown", true);
  }
}
