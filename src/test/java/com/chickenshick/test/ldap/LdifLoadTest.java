package com.chickenshick.test.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import com.chickenshick.test.ldap.annotations.Ldif;

@LdapConfiguration(
		ldifs = @Ldif("/test.ldif")
)
public class LdifLoadTest {

	private LdapServerResource server;
	
	@Before
	public void startup() throws Exception {
		server = new LdapServerResource(this).start();
	}
	
	@After
	public void shutdown() {
		server.stop();
	}
	
	@Test
	public void testMyLdifFileWasLoaded() throws Exception {
		LdapTemplate t = new LdapTemplate();
		LdapContextSource s = new LdapContextSource();
		s.setPassword(LdapConfiguration.DEFAULT_PASSWORD);
		s.setUserDn(LdapConfiguration.DEFAULT_BIND_DN);
		s.setUrl(String.format("ldap://localhost:%d", LdapConfiguration.DEFAULT_PORT));
		t.setContextSource(s);
		t.afterPropertiesSet();
		s.afterPropertiesSet();
		
		PresentFilter filter = new PresentFilter("dc");
		
		@SuppressWarnings("unchecked")
		List<String> dns = t.search("",filter.encode(), new ContextMapper() {
			public Object mapFromContext(Object ctx) {
				DirContextAdapter context = (DirContextAdapter)ctx;
				return context.getDn().toString();
			}
		});
		
		assertEquals(2, dns.size());
		assertTrue(dns.contains("dc=root"));
		assertTrue(dns.contains("dc=child,dc=root"));
	}
}
