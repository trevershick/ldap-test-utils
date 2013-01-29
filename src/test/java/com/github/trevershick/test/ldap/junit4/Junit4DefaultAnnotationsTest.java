package com.github.trevershick.test.ldap.junit4;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.PresentFilter;

import com.github.trevershick.test.ldap.annotations.LdapConfiguration;

@LdapConfiguration(useRandomPortAsFallback=true)
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
		LdapTemplate t = new LdapTemplate();
		LdapContextSource s = new LdapContextSource();
		s.setPassword(LdapConfiguration.DEFAULT_PASSWORD);
		s.setUserDn(LdapConfiguration.DEFAULT_BIND_DN);
		s.setUrl(String.format("ldap://localhost:%d", LdapConfiguration.DEFAULT_PORT));
		t.setContextSource(s);
		t.afterPropertiesSet();
		s.afterPropertiesSet();
		
		PresentFilter filter = new PresentFilter("objectclass");
		
		@SuppressWarnings("unchecked")
		List<String> dns = t.search("",filter.encode(), new ContextMapper() {
			public Object mapFromContext(Object ctx) {
				DirContextAdapter context = (DirContextAdapter)ctx;
				return context.getDn().toString();
			}
		});
		
		assertEquals(1, dns.size());
		assertEquals(LdapConfiguration.DEFAULT_ROOT_OBJECT_DN, dns.get(0));
	}
}
