package com.github.trevershick.test.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import com.github.trevershick.test.ldap.annotations.LdapConfiguration;

@LdapConfiguration(useRandomPortAsFallback = true)
public class FallBackToRandomTest {

	private LdapServerResource s1;
	private LdapServerResource s2;

	@After
	public void cleanup1() {
		s1.stop();
	}

	@After
	public void cleanup2() {
		s2.stop();
	}

	@Test
	public void wontFallbackToRandom() throws Exception {
		s1 = new LdapServerResource().start();
		assertEquals(LdapConfiguration.DEFAULT_PORT, s1.port());

		s2 = new LdapServerResource(this).start();
		assertTrue(s2.isStarted());
		assertTrue(s2.port() != LdapConfiguration.DEFAULT_PORT);

		s1.stop();
		s2.stop();
	}
}
