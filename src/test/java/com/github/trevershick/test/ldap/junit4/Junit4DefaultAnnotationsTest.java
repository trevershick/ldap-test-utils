package com.github.trevershick.test.ldap.junit4;

import com.github.trevershick.test.ldap.annotations.LdapConfiguration;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.Schema;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;

import java.util.List;

import static com.github.trevershick.test.ldap.Utils.Filters.OBJECTCLASS_PRESENT;
import static com.github.trevershick.test.ldap.Utils.Mappers.DN_MAPPER;
import static com.github.trevershick.test.ldap.Utils.Spring.ldapTemplate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@LdapConfiguration(useRandomPortAsFallback = true)
public class Junit4DefaultAnnotationsTest {

	/**
	 * This rule will start and stop the server 'around' the test
	 */
	@Rule
	public LdapServerRule rule = new LdapServerRule(this);

	/**
	 * You should not do this, but this is here for 'secondRuleStartsRandomPort'
	 */
	@Rule
	public LdapServerRule rule2 = new LdapServerRule(this);

	@Test
	public void secondRuleStartsRandomPort() {
		assertTrue(rule.serverIsStarted());
		assertTrue(rule2.serverIsStarted());
		assertTrue("One of the 'rules' is using the default port",
				LdapConfiguration.DEFAULT_PORT == rule.port() ||
						LdapConfiguration.DEFAULT_PORT == rule2.port());
	}

	@Test
	public void testStartsUpWithDefaults() throws Exception {
		final LdapTemplate t = ldapTemplate(LdapConfiguration.DEFAULT_BIND_DN,
				LdapConfiguration.DEFAULT_PASSWORD,
				LdapConfiguration.DEFAULT_PORT);

		@SuppressWarnings("unchecked")
		List<String> dns = t.search("", OBJECTCLASS_PRESENT, DN_MAPPER);

		assertEquals(1, dns.size());
		assertEquals(LdapConfiguration.DEFAULT_ROOT_OBJECT_DN, dns.get(0));
	}

}
