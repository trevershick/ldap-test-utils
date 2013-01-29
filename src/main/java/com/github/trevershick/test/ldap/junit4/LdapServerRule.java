package com.github.trevershick.test.ldap.junit4;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.github.trevershick.test.ldap.LdapServerResource;

public class LdapServerRule implements TestRule {

	private Object target;
	
	public LdapServerRule() {
		
	}
	public LdapServerRule(Object target) {
		this.target = target;
	}
	
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			LdapServerResource ldapServer;
			@Override
			public void evaluate() throws Throwable {
				ldapServer = new LdapServerResource(target);
				ldapServer.start();
				base.evaluate();
				ldapServer.stop();
			}
		};
	}

}
