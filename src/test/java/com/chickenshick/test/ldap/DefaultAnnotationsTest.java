package com.chickenshick.test.ldap;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.PresentFilter;

import com.chickenshick.test.ldap.annotations.LdapConfiguration;


public class DefaultAnnotationsTest {

	private LdapServerResource server;

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

	@After
	public void shutdown() {
		server.stop();
	}

	@Before
	public void startup() throws Exception {
		server = new LdapServerResource().start();
	}
}
