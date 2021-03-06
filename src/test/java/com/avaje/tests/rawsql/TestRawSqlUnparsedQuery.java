package com.avaje.tests.rawsql;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.tests.model.basic.Customer;
import com.avaje.tests.model.basic.ResetBasicData;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestRawSqlUnparsedQuery extends BaseTestCase {

  @Test
  public void testDoubleUnparsedQuery() {

    // Checks for RawSql caching issue https://github.com/ebean-orm/avaje-ebeanorm/issues/259
    ResetBasicData.reset();

    test();
    test();
  }

  private static void test() {
    RawSql rawSql = RawSqlBuilder
      .unparsed("select r.id, r.name from o_customer r where r.id >= :a and r.name like :b")
      .columnMapping("r.id", "id").columnMapping("r.name", "name").create();

    Query<Customer> query = Ebean.find(Customer.class);
    query.setRawSql(rawSql);
    query.setParameter("a", 1);
    query.setParameter("b", "R%");

    List<Customer> list = query.findList();
    Assert.assertNotNull(list);
  }

}
