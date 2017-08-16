package com.github.trevershick.test.ldap.junit4;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.github.trevershick.test.ldap.LdapServerResource;

public class LdapServerRule implements TestRule {
  private LdapServerResource ldapServer;

  private Object target;

  public LdapServerRule() {

  }

  public LdapServerRule(Object target) {
    this.target = target;
  }

  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        ldapServer = new LdapServerResource(target);
        try {
          ldapServer.start();
          base.evaluate();
        }
        finally {
          if (ldapServer.isStarted()) {
            ldapServer.stop();
          }
        }
        ldapServer = null;
      }
    };
  }

  public int port() {
    return ldapServer.port();
  }

  public boolean serverIsStarted() {
    return ldapServer != null && ldapServer.isStarted();
  }

  public boolean serverIsStopped() {
    return ldapServer != null && ldapServer.isStopped();
  }

  public LdapServerResource getServer() {
    return this.ldapServer;
  }
}
