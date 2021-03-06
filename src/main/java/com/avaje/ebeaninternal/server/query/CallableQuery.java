package com.avaje.ebeaninternal.server.query;

import com.avaje.ebean.Transaction;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.api.SpiQuery;

/**
 * Base object for making query execution into Callable's.
 *
 * @param <T> the entity bean type
 * @author rbygrave
 */
abstract class CallableQuery<T> {

  protected final SpiQuery<T> query;

  protected final SpiEbeanServer server;

  protected final Transaction transaction;

  CallableQuery(SpiEbeanServer server, SpiQuery<T> query, Transaction t) {
    this.server = server;
    this.query = query;
    this.transaction = t;
  }

  public SpiQuery<T> getQuery() {
    return query;
  }

  public Transaction getTransaction() {
    return transaction;
  }

}
