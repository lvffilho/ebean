package com.avaje.ebean.dbmigration.model;

import com.avaje.ebean.config.DbConstraintNaming;
import com.avaje.ebean.dbmigration.ddlgeneration.DdlHandler;
import com.avaje.ebean.dbmigration.ddlgeneration.DdlWrite;
import com.avaje.ebean.dbmigration.ddlgeneration.platform.DefaultConstraintMaxLength;
import com.avaje.ebean.dbmigration.migration.ChangeSet;
import com.avaje.ebean.dbmigration.model.build.ModelBuildBeanVisitor;
import com.avaje.ebean.dbmigration.model.build.ModelBuildContext;
import com.avaje.ebean.dbmigration.model.visitor.VisitAllUsing;
import com.avaje.ebeaninternal.api.SpiEbeanServer;

import java.io.IOException;

/**
 * Reads EbeanServer bean descriptors to build the current model.
 */
public class CurrentModel {

  private final SpiEbeanServer server;

  private final DbConstraintNaming constraintNaming;

  private final DbConstraintNaming.MaxLength maxLength;

  private final boolean platformTypes;

  private ModelContainer model;

  private ChangeSet changeSet;

  private DdlWrite write;

  /**
   * Construct with a given EbeanServer instance for DDL create all generation, not migration.
   */
  public CurrentModel(SpiEbeanServer server) {
    this(server, server.getServerConfig().getConstraintNaming(), true);
  }

  /**
   * Construct with a given EbeanServer, platformDdl and constraintNaming convention.
   * <p>
   * Note the EbeanServer is just used to read the BeanDescriptors and platformDdl supplies
   * the platform specific handling on
   * </p>
   */
  public CurrentModel(SpiEbeanServer server, DbConstraintNaming constraintNaming) {
    this(server, constraintNaming, false);
  }

  private CurrentModel(SpiEbeanServer server, DbConstraintNaming constraintNaming, boolean platformTypes) {
    this.server = server;
    this.constraintNaming = constraintNaming;
    this.maxLength = maxLength(server, constraintNaming);
    this.platformTypes = platformTypes;
  }

  private static DbConstraintNaming.MaxLength maxLength(SpiEbeanServer server, DbConstraintNaming naming) {

    if (naming.getMaxLength() != null) {
      return naming.getMaxLength();
    }

    int maxConstraintNameLength = server.getDatabasePlatform().getMaxConstraintNameLength();
    return new DefaultConstraintMaxLength(maxConstraintNameLength);
  }

  /**
   * Return the current model by reading all the bean descriptors and properties.
   */
  public ModelContainer read() {
    if (model == null) {
      model = new ModelContainer();

      ModelBuildContext context = new ModelBuildContext(model, constraintNaming, maxLength, platformTypes);
      ModelBuildBeanVisitor visitor = new ModelBuildBeanVisitor(context);
      VisitAllUsing visit = new VisitAllUsing(visitor, server);
      visit.visitAllBeans();

      // adjust the foreign keys on the 'draft' tables
      context.adjustDraftReferences();
    }
    return model;
  }

  public void setChangeSet(ChangeSet changeSet) {
    this.changeSet = changeSet;
  }

  /**
   * Return as a ChangeSet.
   */
  public ChangeSet getChangeSet() {
    read();
    if (changeSet == null) {
      changeSet = asChangeSet();
    }
    return changeSet;
  }

  /**
   * Return the 'Create' DDL.
   */
  public String getCreateDdl() throws IOException {

    createDdl();

    StringBuilder ddl = new StringBuilder(2000);
    ddl.append(write.apply().getBuffer());
    ddl.append(write.applyForeignKeys().getBuffer());
    ddl.append(write.applyHistory().getBuffer());

    return ddl.toString();
  }

  /**
   * Return the 'Drop' DDL.
   */
  public String getDropAllDdl() throws IOException {

    createDdl();

    StringBuilder ddl = new StringBuilder(2000);
    ddl.append(write.dropAllForeignKeys().getBuffer());
    ddl.append(write.dropAll().getBuffer());

    return ddl.toString();
  }

  /**
   * Create all the DDL based on the changeSet.
   */
  private void createDdl() throws IOException {

    if (write == null) {
      ChangeSet createChangeSet = getChangeSet();

      write = new DdlWrite(new MConfiguration(), model);

      DdlHandler handler = handler();
      handler.generate(write, createChangeSet);
    }
  }

  /**
   * Return the platform specific DdlHandler (to generate DDL).
   */
  private DdlHandler handler() {
    return server.getDatabasePlatform().createDdlHandler(server.getServerConfig());
  }

  /**
   * Convert the model into a ChangeSet.
   */
  private ChangeSet asChangeSet() {

    // empty diff so changes will effectively all be create
    ModelDiff diff = new ModelDiff();
    diff.compareTo(model);

    return diff.getApplyChangeSet();
  }

}
