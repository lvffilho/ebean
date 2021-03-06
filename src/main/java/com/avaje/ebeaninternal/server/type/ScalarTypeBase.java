package com.avaje.ebeaninternal.server.type;

/**
 * Base ScalarType object.
 */
public abstract class ScalarTypeBase<T> implements ScalarType<T> {

  protected final Class<T> type;

  protected final boolean jdbcNative;

  protected final int jdbcType;

  public ScalarTypeBase(Class<T> type, boolean jdbcNative, int jdbcType) {
    this.type = type;
    this.jdbcNative = jdbcNative;
    this.jdbcType = jdbcType;
  }

  @Override
  public long asVersion(T value) {
    throw new RuntimeException("not supported");
  }

  @Override
  public boolean isBinaryType() {
    // override for binary/byte based types
    return false;
  }

  /**
   * Default implementation of mutable false.
   */
  @Override
  public boolean isMutable() {
    return false;
  }

  /**
   * Default to true.
   */
  @Override
  public boolean isDirty(Object value) {
    return true;
  }

  /**
   * Just return 0.
   */
  @Override
  public int getLength() {
    return 0;
  }

  public boolean isJdbcNative() {
    return jdbcNative;
  }

  public int getJdbcType() {
    return jdbcType;
  }

  public Class<T> getType() {
    return type;
  }

  @SuppressWarnings("unchecked")
  public String format(Object value) {
    return formatValue((T) value);
  }

  public void loadIgnore(DataReader reader) {
    reader.incrementPos(1);
  }

  public void accumulateScalarTypes(String propName, CtCompoundTypeScalarList list) {
    list.addScalarType(propName, this);
  }

}
