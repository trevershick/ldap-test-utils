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
import com.chickenshick.test.ldap.annotations.LdapEntry;

@LdapConfiguration(
		bindDn = "cn=Directory Manager",
		password = "mypass",
		port = 11111,
		base = @LdapEntry(dn = "dc=myroot", objectclass = { "top", "domain" })
)
public class BasicCustomAnnotationsTest {

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
	public void testStartsUpWithMyValues() throws Exception {
		LdapTemplate t = new LdapTemplate();
		LdapContextSource s = new LdapContextSource();
		s.setPassword("mypass");
		s.setUserDn("cn=Directory Manager");
		s.setUrl(String.format("ldap://localhost:%d", 11111));
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
		assertEquals("dc=myroot", dns.get(0));
	}
}
