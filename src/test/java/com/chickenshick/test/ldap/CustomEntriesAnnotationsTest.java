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

import com.chickenshick.test.ldap.annotations.LdapAttribute;
import com.chickenshick.test.ldap.annotations.LdapConfiguration;
import com.chickenshick.test.ldap.annotations.LdapEntry;

@LdapConfiguration(
		entries={
			@LdapEntry(dn="ou=Groups,dc=root",objectclass="organizationalUnit",attributes={@LdapAttribute(name="ou",value="Groups")})	
		}
)
public class CustomEntriesAnnotationsTest {

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
	public void testStartsUpWithMyEntries() throws Exception {
		LdapTemplate t = new LdapTemplate();
		LdapContextSource s = new LdapContextSource();
		s.setPassword(LdapConfiguration.DEFAULT_PASSWORD);
		s.setUserDn(LdapConfiguration.DEFAULT_BIND_DN);
		s.setUrl(String.format("ldap://localhost:%d", LdapConfiguration.DEFAULT_PORT));
		t.setContextSource(s);
		t.afterPropertiesSet();
		s.afterPropertiesSet();
		
		PresentFilter filter = new PresentFilter("ou");
		
		@SuppressWarnings("unchecked")
		List<String> dns = t.search("",filter.encode(), new ContextMapper() {
			public Object mapFromContext(Object ctx) {
				DirContextAdapter context = (DirContextAdapter)ctx;
				return context.getDn().toString();
			}
		});
		
		assertEquals("My OU should have been returned", 1, dns.size());
		assertTrue(dns.contains("ou=Groups,dc=root"));
	}
}
