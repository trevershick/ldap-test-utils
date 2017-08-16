package com.github.trevershick.test.ldap;

import static com.github.trevershick.test.ldap.Utils.Filters.OBJECTCLASS_PRESENT;
import static com.github.trevershick.test.ldap.Utils.Mappers.DN_MAPPER;
import static com.github.trevershick.test.ldap.Utils.Spring.ldapTemplate;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;

import com.github.trevershick.test.ldap.annotations.LdapConfiguration;
import com.github.trevershick.test.ldap.annotations.LdapEntry;
import com.github.trevershick.test.ldap.junit4.LdapServerRule;

@LdapConfiguration(
		bindDn = "cn=Directory Manager",
		password = "mypass",
		port = 11111,
		base = @LdapEntry(dn = "dc=myroot", objectclass = {"top", "domain"})
)
public class BasicCustomAnnotationsTest {

	@Rule
	public LdapServerRule rule = new LdapServerRule(this);

	@Test
	public void testStartsUpWithMyValues() throws Exception {
		final LdapTemplate t = ldapTemplate("cn=Directory Manager",
				"mypass",
				11111);

		final List<String> dns = t.search("", OBJECTCLASS_PRESENT, DN_MAPPER);

		assertThat(dns, hasItems("dc=myroot"));
	}
}
