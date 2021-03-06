package com.avaje.ebeaninternal.api;

/**
 * A hash key for a query including both the query plan and bind values.
 */
public class HashQuery {

  private final CQueryPlanKey planHash;

  private final int bindHash;

  /**
   * Create the HashQuery.
   */
  public HashQuery(CQueryPlanKey planHash, int bindHash) {
    this.planHash = planHash;
    this.bindHash = bindHash;
  }

  public String toString() {
    return "HashQuery@" + Integer.toHexString(hashCode());
  }

  public int hashCode() {
    int hc = 31 * planHash.hashCode();
    hc = 31 * hc + bindHash;
    return hc;
  }

  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof HashQuery)) {
      return false;
    }

    HashQuery e = (HashQuery) obj;
    return e.bindHash == bindHash && e.planHash.equals(planHash);
  }
}
