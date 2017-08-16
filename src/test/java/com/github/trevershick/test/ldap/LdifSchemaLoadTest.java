package com.github.trevershick.test.ldap;

import com.github.trevershick.test.ldap.annotations.LdapConfiguration;
import com.github.trevershick.test.ldap.annotations.Ldif;
import com.github.trevershick.test.ldap.junit4.LdapServerRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;

import java.util.List;

import static com.github.trevershick.test.ldap.Utils.Filters.DC_PRESENT;
import static com.github.trevershick.test.ldap.Utils.Filters.USERPRINCIPAL_PRESENT;
import static com.github.trevershick.test.ldap.Utils.Mappers.DN_MAPPER;
import static com.github.trevershick.test.ldap.Utils.Spring.ldapTemplate;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

@LdapConfiguration(ldifs = @Ldif("/schematest.ldif"))
public class LdifSchemaLoadTest {

	@Rule
	public LdapServerRule rule = new LdapServerRule(this);

	@Test
	public void testMyLdifFileWasLoaded() throws Exception {
		LdapTemplate t = ldapTemplate(LdapConfiguration.DEFAULT_BIND_DN,
				LdapConfiguration.DEFAULT_PASSWORD,
				rule.port());

		// our schematest.ldif file altered the schema and imported a user
		// with 'userPrincipal' that was not there prior to the schema alteration
		// we pull the entry here and verify that it came back (which illustrates we
		// can modify the schema via ldif files)
		final List<String> dns = t.search("", USERPRINCIPAL_PRESENT, DN_MAPPER);
		assertThat(dns, hasItems("cn=tshick2,dc=root"));
	}

}
