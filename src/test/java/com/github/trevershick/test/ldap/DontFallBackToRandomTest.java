package com.github.trevershick.test.ldap;

import static org.junit.Assert.*;

import java.net.BindException;

import org.junit.Test;

import com.github.trevershick.test.ldap.annotations.LdapConfiguration;

public class DontFallBackToRandomTest {
	
	@Test(expected=BindException.class)
	public void wontFallbackToRandom() throws Exception {
		LdapServerResource s1 = new LdapServerResource().start();
		assertEquals(LdapConfiguration.DEFAULT_PORT, s1.port());
		
		new LdapServerResource().start();
	}

}
