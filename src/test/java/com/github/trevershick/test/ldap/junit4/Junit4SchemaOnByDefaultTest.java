package com.github.trevershick.test.ldap.junit4;

import com.github.trevershick.test.ldap.LdapServerResource;
import com.github.trevershick.test.ldap.annotations.LdapConfiguration;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;

import java.lang.reflect.Field;
import java.util.List;

import static com.github.trevershick.test.ldap.Utils.Filters.OBJECTCLASS_PRESENT;
import static com.github.trevershick.test.ldap.Utils.Mappers.DN_MAPPER;
import static com.github.trevershick.test.ldap.Utils.Spring.ldapTemplate;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@LdapConfiguration(useRandomPortAsFallback = true)
public class Junit4SchemaOnByDefaultTest {

	@Rule
	public LdapServerRule rule = new LdapServerRule(this);

	@Test
	public void testSchemaTurnedOff() throws Exception {
		Field serverField = LdapServerResource.class.getDeclaredField("server");
		// i do not expose the server to anyone outside the package so
		// we have to make this accessible
		serverField.setAccessible(true);
		InMemoryDirectoryServer imds = (InMemoryDirectoryServer) serverField.get(rule.getServer());
		assertThat("The schema in the underlying implementation should NOT be null",
				imds.getSchema(),
				is(not(nullValue())));
		assertThat(rule.isUsingSchema(), is(true));
	}

}
