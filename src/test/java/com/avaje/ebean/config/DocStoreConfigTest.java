package com.avaje.ebean.config;

import com.avaje.ebean.annotation.DocStoreMode;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class DocStoreConfigTest {

  @Test
  public void loadSettings() throws Exception {

    DocStoreConfig config = new DocStoreConfig();

    Properties properties = new Properties();
    properties.setProperty("ebean.docstore.active", "true");
    properties.setProperty("ebean.docstore.bulkBatchSize", "99");
    properties.setProperty("ebean.docstore.url", "http://foo:9800");
    properties.setProperty("ebean.docstore.persist", "IGNORE");

    PropertiesWrapper wrapper = new PropertiesWrapper("ebean", null, properties);

    config.loadSettings(wrapper);

    assertTrue(config.isActive());
    assertFalse(config.isGenerateMapping());
    assertFalse(config.isDropCreate());
    assertEquals("http://foo:9800", config.getUrl());
    assertEquals(DocStoreMode.IGNORE, config.getPersist());
    assertEquals(99, config.getBulkBatchSize());
  }

  @Test
  public void loadSettings_generateMapping_dropCreate() throws Exception {

    DocStoreConfig config = new DocStoreConfig();

    Properties properties = new Properties();
    properties.setProperty("ebean.docstore.generateMapping", "true");
    properties.setProperty("ebean.docstore.dropCreate", "true");

    PropertiesWrapper wrapper = new PropertiesWrapper("ebean", null, properties);
    config.loadSettings(wrapper);

    assertTrue(config.isGenerateMapping());
    assertTrue(config.isDropCreate());
    assertFalse(config.isCreate());
  }

  @Test
  public void loadSettings_generateMapping_create() throws Exception {

    DocStoreConfig config = new DocStoreConfig();

    Properties properties = new Properties();
    properties.setProperty("ebean.docstore.generateMapping", "true");
    properties.setProperty("ebean.docstore.create", "true");

    PropertiesWrapper wrapper = new PropertiesWrapper("ebean", null, properties);
    config.loadSettings(wrapper);

    assertTrue(config.isGenerateMapping());
    assertTrue(config.isCreate());
    assertFalse(config.isDropCreate());
  }
}