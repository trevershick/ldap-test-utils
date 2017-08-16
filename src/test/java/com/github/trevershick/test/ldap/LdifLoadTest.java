package com.github.trevershick.test.ldap;

import static com.github.trevershick.test.ldap.Utils.Filters.DC_PRESENT;
import static com.github.trevershick.test.ldap.Utils.Mappers.DN_MAPPER;
import static com.github.trevershick.test.ldap.Utils.Spring.ldapTemplate;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;

import com.github.trevershick.test.ldap.annotations.LdapConfiguration;
import com.github.trevershick.test.ldap.annotations.Ldif;
import com.github.trevershick.test.ldap.junit4.LdapServerRule;

@LdapConfiguration(ldifs = @Ldif("/test.ldif"))
public class LdifLoadTest {

	@Rule
	public LdapServerRule rule = new LdapServerRule(this);

	@Test
	public void testMyLdifFileWasLoaded() throws Exception {
		LdapTemplate t = ldapTemplate(LdapConfiguration.DEFAULT_BIND_DN,
				LdapConfiguration.DEFAULT_PASSWORD,
				rule.port());

		final List<String> dns = t.search("", DC_PRESENT, DN_MAPPER);

		assertThat(dns, hasItems("dc=root", "dc=child,dc=root"));
	}

	@Test
	public void canLoginWithLdifDefinedUser() throws Exception {

		LdapTemplate t = ldapTemplate("cn=tshick2,dc=root",
				"thepassword2",
				rule.port());

		final List<String> dns = t.search("", DC_PRESENT, DN_MAPPER);

		assertThat(dns, hasItems("dc=root", "dc=child,dc=root"));
	}
}
