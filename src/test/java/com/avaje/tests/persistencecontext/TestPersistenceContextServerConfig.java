package com.avaje.tests.persistencecontext;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.PersistenceContextScope;
import com.avaje.ebean.Query;
import com.avaje.ebean.config.ContainerConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.api.SpiQuery;
import com.avaje.tests.model.basic.EBasicVer;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class TestPersistenceContextServerConfig extends BaseTestCase {

  @Test
  public void test_config() {

    SpiEbeanServer ebeanServer = (SpiEbeanServer) create();

    Query<EBasicVer> query = ebeanServer.find(EBasicVer.class);

    PersistenceContextScope scope = ebeanServer.getPersistenceContextScope((SpiQuery<?>) query);

    assertEquals(PersistenceContextScope.QUERY, scope);
  }

  static EbeanServer create() {

    System.setProperty("ebean.ignoreExtraDdl", "true");

    ServerConfig config = new ServerConfig();
    config.setName("withPCQuery");

    Properties properties = new Properties();
    properties.setProperty("datasource.withPCQuery.username", "sa");
    properties.setProperty("datasource.withPCQuery.password", "");
    properties.setProperty("datasource.withPCQuery.databaseUrl", "jdbc:h2:mem:withPCQuery;");
    properties.setProperty("datasource.withPCQuery.databaseDriver", "org.h2.Driver");

    config.loadFromProperties(properties);
    config.setPersistenceContextScope(PersistenceContextScope.QUERY);
    config.setContainerConfig(new ContainerConfig());
    config.setDefaultServer(false);
    config.setRegister(false);

//    DataSourceConfig dataSourceConfig = new DataSourceConfig();
//    dataSourceConfig.setUsername("sa");
//    dataSourceConfig.setPassword("");
//    dataSourceConfig.setUrl("jdbc:h2:mem:withPCQuery;");
//    dataSourceConfig.setDriver("org.h2.Driver");
//    config.setDataSourceConfig(dataSourceConfig);

    config.addClass(EBasicVer.class);

    return EbeanServerFactory.create(config);
  }
}
