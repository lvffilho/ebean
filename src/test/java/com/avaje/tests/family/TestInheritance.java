package com.avaje.tests.family;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.ebeaninternal.server.deploy.BeanProperty;
import com.avaje.ebeaninternal.server.deploy.BeanPropertyAssocOne;
import com.avaje.tests.model.basic.EBasic;
import com.avaje.tests.model.family.ChildPerson;
import com.avaje.tests.model.family.GrandParentPerson;
import com.avaje.tests.model.family.ParentPerson;
import org.junit.Test;

import static org.junit.Assert.*;


public class TestInheritance extends BaseTestCase {


  @Test
  public void testDescriptor() {
    BeanDescriptor<GrandParentPerson> desc = spiEbeanServer().getBeanDescriptor(GrandParentPerson.class);
    BeanProperty prop1 = desc.getBeanProperty("someBean");
    BeanProperty prop2 = desc.getBeanProperty("effectiveBean");

    assertEquals(BeanPropertyAssocOne.class, prop1.getClass());
    assertEquals(BeanPropertyAssocOne.class, prop2.getClass());
  }

  /**
   * <pre>
   * grandparent1   Josef Foo(65) from Munich
   * +- parent1     Maria Bar (40) has someBean1
   * |  +- child1   Fred(13) has someBean2
   * |  '- child2   Julia Baz(8)
   * +- parent2     Sandra (50) from Berlin
   * |  '- child3   Roland(36)
   * +- parent3     Michael(60)
   * </pre>
   */
  @Test
  public void testInheritance() {
    // create some other beans
    EBasic someBean1 = new EBasic();
    someBean1.setName("A Bean");
    server().save(someBean1);

    EBasic someBean2 = new EBasic();
    someBean2.setName("An other Bean");
    server().save(someBean2);

    ChildPerson child1 = new ChildPerson();
    child1.setAge(13);
    child1.setName("Fred");

    ChildPerson child2 = new ChildPerson();
    child2.setAge(8);
    child2.setFamilyName("Baz");
    child2.setName("Julia");

    ChildPerson child3 = new ChildPerson();
    child3.setAge(36);
    child3.setName("Roland");

    ParentPerson parent1 = new ParentPerson();
    parent1.setAge(40);
    parent1.setName("Maria");
    parent1.setFamilyName("Bar");
    parent1.setSomeBean(someBean1);
    child1.setSomeBean(someBean2);
    parent1.getChildren().add(child1);
    parent1.getChildren().add(child2);

    ParentPerson parent2 = new ParentPerson();
    parent2.setAge(50);
    parent2.setName("Sandra");
    parent2.getChildren().add(child3);
    parent2.setAddress("Berlin");


    ParentPerson parent3 = new ParentPerson();
    parent3.setAge(60);
    parent3.setName("Michael");

    GrandParentPerson grandparent1 = new GrandParentPerson();
    grandparent1.setAge(65);
    grandparent1.setName("Josef");
    grandparent1.setFamilyName("Foo");
    grandparent1.setAddress("Munich");

    grandparent1.getChildren().add(parent1);
    grandparent1.getChildren().add(parent2);
    grandparent1.getChildren().add(parent3);


    server().save(grandparent1);
    // Test setup complete, so retrieve bean from db
    //grandparent1 = server().find(GrandParentPerson.class).setId(grandparent1.getIdentifier()).where()
    // .in("effectiveBean.id",2,null) // geht nicht!
    // .findUnique();
    parent1 = server().find(ParentPerson.class).setId(1).fetch("someBean").findUnique();
    assertEquals("A Bean", parent1.getSomeBean().getName());
    grandparent1 = server().find(GrandParentPerson.class)
      .fetch("children", "*") // FIXME If I do not fetch childrenBean, I will get an exception
      .fetch("children.effectiveBean", "*")
      .fetch("children.children", "*")
      .fetch("children.children.effectiveBean", "*")
      .setId(grandparent1.getIdentifier()).where()
      .or()
      .in("effectiveBean.id", 1)
      .isNull("effectiveBean.id")
      .endOr()
      .findUnique();
    assertNotNull(grandparent1);

    // check if aggregation works
    assertEquals(3, grandparent1.getChildCount().intValue());
    assertEquals(150, grandparent1.getTotalAge().intValue());

    // check normal properties
    assertEquals("Josef", grandparent1.getName());
    assertEquals("Foo", grandparent1.getFamilyName());
    assertEquals("Munich", grandparent1.getAddress());
    assertEquals(1, grandparent1.getEffectiveBean().getId().intValue());

    // now check children of grandparent
    parent1 = grandparent1.getChildren().get(0);
    parent2 = grandparent1.getChildren().get(1);
    parent3 = grandparent1.getChildren().get(2);

    assertNotNull(parent1);
    assertNotNull(parent2);
    assertNotNull(parent3);
    // parent1
    assertEquals("Maria", parent1.getName());
    assertEquals("Bar", parent1.getFamilyName()); // overwritten family name
    assertEquals("Bar", parent1.getEffectiveFamilyName()); // test inheritance
    assertNull(parent1.getAddress()); // no alternative address set
    assertEquals("Munich", parent1.getEffectiveAddress()); // -> inherit munich
    assertEquals(2, parent1.getChildCount().intValue());
    assertEquals(21, parent1.getTotalAge().intValue());
    assertEquals("A Bean", parent1.getEffectiveBean().getName());

    // parent2
    assertEquals("Sandra", parent2.getName());
    assertNull(parent2.getFamilyName());
    assertEquals("Foo", parent2.getEffectiveFamilyName());
    assertEquals("Berlin", parent2.getAddress());
    assertEquals("Berlin", parent2.getEffectiveAddress());
    assertEquals(1, parent2.getChildCount().intValue());
    assertEquals(36, parent2.getTotalAge().intValue());
    assertNull(parent2.getEffectiveBean());

    // parent3
    assertEquals("Michael", parent3.getName());
    assertNull(parent3.getFamilyName());
    assertEquals("Foo", parent3.getEffectiveFamilyName());
    assertNull(parent3.getAddress());
    assertEquals("Munich", parent3.getEffectiveAddress());
    assertEquals(0, parent3.getChildCount().intValue());
    assertEquals(0, parent3.getTotalAge().intValue());
    assertNull(parent3.getEffectiveBean());

    child1 = parent1.getChildren().get(0);
    child2 = parent1.getChildren().get(1);

    child3 = parent2.getChildren().get(0);

    assertNotNull(child1);
    assertNotNull(child2);
    assertNotNull(child3);

    assertEquals("Bar", child1.getEffectiveFamilyName());
    assertEquals("Munich", child1.getEffectiveAddress());
    assertEquals("An other Bean", child1.getSomeBean().getName());
    assertEquals("An other Bean", child1.getEffectiveBean().getName());

    assertTrue(child1.getSomeBean() == child1.getEffectiveBean());

    assertEquals("Baz", child2.getEffectiveFamilyName());
    assertEquals("Munich", child2.getEffectiveAddress());
    assertNull(child2.getSomeBean());
    assertEquals("A Bean", child2.getEffectiveBean().getName());

    assertEquals("Foo", child3.getEffectiveFamilyName());
    assertEquals("Berlin", child3.getEffectiveAddress());
    assertNull(child3.getEffectiveBean());

    // Now start from bottom up
    child2 = server().find(ChildPerson.class).where().eq("name", "Julia").select("parent.name").findUnique();
    assertEquals("Baz", child2.getEffectiveFamilyName());
    assertEquals("Munich", child2.getEffectiveAddress());
    assertNull(child2.getSomeBean());
    assertEquals("A Bean", child2.getEffectiveBean().getName());

    parent1 = child2.getParent();
    assertEquals("Maria", parent1.getName());
    assertEquals("Bar", parent1.getFamilyName()); // overwritten family name
    assertEquals("Bar", parent1.getEffectiveFamilyName()); // test inheritance
    assertNull(parent1.getAddress()); // no alternative address set
    assertEquals("Munich", parent1.getEffectiveAddress()); // -> inherit munich
    assertEquals(2, parent1.getChildCount().intValue());
    assertEquals(21, parent1.getTotalAge().intValue());
    assertEquals("A Bean", parent1.getEffectiveBean().getName());

    grandparent1 = parent1.getParent();
    // check if aggregation works
    assertEquals(3, grandparent1.getChildCount().intValue());
    assertEquals(150, grandparent1.getTotalAge().intValue());

    // check normal properties
    assertEquals("Josef", grandparent1.getName());
    assertEquals("Foo", grandparent1.getFamilyName());
    assertEquals("Munich", grandparent1.getAddress());
    assertEquals(1, grandparent1.getEffectiveBean().getId().intValue());
  }
}
