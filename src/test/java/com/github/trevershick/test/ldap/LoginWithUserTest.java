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

import com.github.trevershick.test.ldap.annotations.LdapAttribute;
import com.github.trevershick.test.ldap.annotations.LdapConfiguration;
import com.github.trevershick.test.ldap.annotations.LdapEntry;
import com.github.trevershick.test.ldap.junit4.LdapServerRule;

@LdapConfiguration(
		bindDn = "cn=Directory Manager",
		password = "mypass",
		port = 11111,
		base = @LdapEntry(dn = "dc=myroot", objectclass = {"top", "domain"}),
		entries = {
				@LdapEntry(dn = "cn=tshick,dc=myroot",
						objectclass = {"top", "person"},
						attributes = {
								@LdapAttribute(name = "userPassword", value = "thepassword"),
								@LdapAttribute(name = "sn", value = "Shick"),
								@LdapAttribute(name = "cn", value = "tshick")
						})
		}
)
public class LoginWithUserTest {

	@Rule
	public LdapServerRule rule = new LdapServerRule(this);

	@Test
	public void canLoginWithUserDefinedInAnnotation() throws Exception {
		LdapTemplate t = ldapTemplate("cn=tshick,dc=myroot",
				"thepassword",
				rule.port());

		final List<String> dns = t.search("", OBJECTCLASS_PRESENT, DN_MAPPER);

		assertThat(dns, hasItems("dc=myroot", "cn=tshick,dc=myroot"));
	}
}
