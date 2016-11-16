package com.github.trevershick.test.ldap;

import static com.github.trevershick.test.ldap.Utils.Filters.OU_PRESENT;
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
  entries = {
    @LdapEntry(dn = "ou=Groups,dc=root", objectclass = "organizationalUnit", attributes = {
      @LdapAttribute(name = "ou", value = "Groups")})
  }
)
public class CustomEntriesAnnotationsTest {
  @Rule
  public LdapServerRule rule = new LdapServerRule(this);

  @Test
  public void testStartsUpWithMyEntries() throws Exception {
    LdapTemplate t = ldapTemplate(LdapConfiguration.DEFAULT_BIND_DN,
      LdapConfiguration.DEFAULT_PASSWORD,
      LdapConfiguration.DEFAULT_PORT);

    final List<String> dns = t.search("", OU_PRESENT, DN_MAPPER);

    assertThat("My OU should have been returned", dns, hasItems("ou=Groups,dc=root"));
  }
}
