package com.avaje.tests.model.prnt;

import com.avaje.ebean.event.BeanPersistAdapter;
import com.avaje.ebean.event.BeanPersistRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MPrinterListener extends BeanPersistAdapter {

  Logger logger = LoggerFactory.getLogger(MPrinterListener.class);

  @Override
  public boolean isRegisterFor(Class<?> cls) {
    return cls.isAssignableFrom(MPrinter.class);
  }

  @Override
  public boolean preUpdate(BeanPersistRequest<?> request) {

    logger.info("preUpdate ...");
    logger.info("preUpdate ..." + request.getUpdatedValues());
    request.getUpdatedProperties();
    return super.preUpdate(request);
  }
}
