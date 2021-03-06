package com.avaje.ebean.common;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.bean.BeanCollection;
import com.avaje.ebean.bean.BeanCollectionLoader;
import com.avaje.ebean.bean.EntityBean;

import javax.persistence.PersistenceException;
import java.util.Set;

/**
 * Base class for List Set and Map implementations of BeanCollection.
 */
abstract class AbstractBeanCollection<E> implements BeanCollection<E> {

  private static final long serialVersionUID = 3365725236140187588L;

  protected boolean readOnly;

  protected boolean disableLazyLoad;

  /**
   * The EbeanServer this is associated with. (used for lazy fetch).
   */
  protected transient BeanCollectionLoader loader;

  protected transient ExpressionList<?> filterMany;

  /**
   * Flag set when registered with the batch loading context.
   */
  protected boolean registeredWithLoadContext;

  protected String ebeanServerName;

  /**
   * The owning bean (used for lazy fetch).
   */
  protected EntityBean ownerBean;

  /**
   * The name of this property in the owning bean (used for lazy fetch).
   */
  protected String propertyName;

  protected ModifyHolder<E> modifyHolder;

  protected ModifyListenMode modifyListenMode;
  protected boolean modifyAddListening;
  protected boolean modifyRemoveListening;
  protected boolean modifyListening;

  /**
   * Constructor not non-lazy loading collection.
   */
  AbstractBeanCollection() {
  }

  /**
   * Used to create deferred fetch proxy.
   */
  AbstractBeanCollection(BeanCollectionLoader loader, EntityBean ownerBean, String propertyName) {
    this.loader = loader;
    this.ebeanServerName = loader.getName();
    this.ownerBean = ownerBean;
    this.propertyName = propertyName;
    this.readOnly = ownerBean._ebean_getIntercept().isReadOnly();
  }

  public EntityBean getOwnerBean() {
    return ownerBean;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public ExpressionList<?> getFilterMany() {
    return filterMany;
  }

  public void setFilterMany(ExpressionList<?> filterMany) {
    this.filterMany = filterMany;
  }

  @Override
  public void setDisableLazyLoad(boolean disableLazyLoad) {
    this.disableLazyLoad = disableLazyLoad;
  }

  void lazyLoadCollection(boolean onlyIds) {
    if (loader == null) {
      loader = (BeanCollectionLoader) Ebean.getServer(ebeanServerName);
    }
    if (loader == null) {
      String msg = "Lazy loading but LazyLoadEbeanServer is null?"
        + " The LazyLoadEbeanServer needs to be set after deserialization"
        + " to support lazy loading.";
      throw new PersistenceException(msg);
    }

    loader.loadMany(this, onlyIds);
    checkEmptyLazyLoad();
  }

  public boolean isRegisteredWithLoadContext() {
    return registeredWithLoadContext;
  }

  public void setLoader(BeanCollectionLoader loader) {
    this.registeredWithLoadContext = true;
    this.loader = loader;
    this.ebeanServerName = loader.getName();
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  void checkReadOnly() {
    if (readOnly) {
      String msg = "This collection is in ReadOnly mode";
      throw new IllegalStateException(msg);
    }
  }

  // ---------------------------------------------------------
  // Support for modify additions deletions etc - ManyToMany
  // ---------------------------------------------------------

  /**
   * set modifyListening to be on or off.
   */
  public void setModifyListening(ModifyListenMode mode) {

    this.modifyListenMode = mode;
    this.modifyAddListening = ModifyListenMode.ALL.equals(mode);
    this.modifyRemoveListening = modifyAddListening || ModifyListenMode.REMOVALS.equals(mode);
    this.modifyListening = modifyRemoveListening || modifyAddListening;
    if (modifyListening) {
      // lose any existing modifications
      modifyHolder = null;
    }
  }

  /**
   * Return the modify listening mode this collection is using.
   */
  public ModifyListenMode getModifyListenMode() {
    return modifyListenMode;
  }

  ModifyHolder<E> getModifyHolder() {
    if (modifyHolder == null) {
      modifyHolder = new ModifyHolder<>();
    }
    return modifyHolder;
  }

  public void modifyAddition(E bean) {
    if (modifyAddListening) {
      getModifyHolder().modifyAddition(bean);
    }
  }

  public void modifyRemoval(Object bean) {
    if (modifyRemoveListening) {
      getModifyHolder().modifyRemoval(bean);
    }
  }

  public void modifyReset() {
    if (modifyHolder != null) {
      modifyHolder.reset();
    }
  }

  public Set<E> getModifyAdditions() {
    if (modifyHolder == null) {
      return null;
    } else {
      return modifyHolder.getModifyAdditions();
    }
  }

  public Set<E> getModifyRemovals() {
    if (modifyHolder == null) {
      return null;
    } else {
      return modifyHolder.getModifyRemovals();
    }
  }

  /**
   * Return true if there are underlying additions or removals.
   */
  boolean holdsModifications() {
    return modifyHolder != null && modifyHolder.hasModifications();
  }
}
