package com.github.trevershick.test.ldap;

import static org.junit.Assert.assertEquals;

import java.net.BindException;

import org.junit.After;
import org.junit.Test;

import com.github.trevershick.test.ldap.annotations.LdapConfiguration;

public class DontFallBackToRandomTest {

  private LdapServerResource s1;

  @After
  public void cleanup() {
    s1.stop();
  }

  @Test(expected = BindException.class)
  public void wontFallbackToRandom() throws Exception {
    s1 = new LdapServerResource().start();
    assertEquals(LdapConfiguration.DEFAULT_PORT, s1.port());

    new LdapServerResource().start();
  }
}
