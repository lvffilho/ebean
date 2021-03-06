package com.avaje.ebean;

import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.event.ServerConfigStartup;
import com.avaje.tests.model.basic.UTDetail;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EbeanServerFactory_ServerConfigStart_Test {

  @Test
  public void test() throws InterruptedException {

    System.setProperty("ebean.ignoreExtraDdl", "true");

    ServerConfig config = new ServerConfig();
    config.setName("h2");
    config.loadFromProperties();
    config.setName("h2other");
    config.setH2ProductionMode(true);
    config.setDdlGenerate(false);
    config.setDdlRun(false);
    config.setDefaultServer(false);
    config.setRegister(false);

    config.addClass(UTDetail.class);
    config.addClass(OnStartupViaClass.class);

    // act - register an instance
    OnStartup onStartup = new OnStartup();
    config.addServerConfigStartup(onStartup);

    EbeanServer ebeanServer = EbeanServerFactory.create(config);

    assertThat(onStartup.calledWithConfig).isSameAs(config);
    assertThat(OnStartupViaClass.calledWithConfig).isSameAs(config);

    assertThat(ebeanServer).isNotNull();

    // test server shutdown and restart using the same ServerConfig
    ebeanServer.shutdown(true, false);

    EbeanServer restartedServer = EbeanServerFactory.create(config);
    restartedServer.shutdown(true, false);
  }

  public static class OnStartup implements ServerConfigStartup {

    ServerConfig calledWithConfig;

    @Override
    public void onStart(ServerConfig serverConfig) {
      calledWithConfig = serverConfig;
    }
  }


  public static class OnStartupViaClass implements ServerConfigStartup {

    static ServerConfig calledWithConfig;

    @Override
    public void onStart(ServerConfig serverConfig) {
      calledWithConfig = serverConfig;
    }
  }
}