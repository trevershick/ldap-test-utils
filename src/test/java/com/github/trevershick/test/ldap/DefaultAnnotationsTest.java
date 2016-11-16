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
import com.github.trevershick.test.ldap.junit4.LdapServerRule;

public class DefaultAnnotationsTest {

  @Rule
  public LdapServerRule rule = new LdapServerRule(this);

  @Test
  public void testStartsUpWithDefaults() throws Exception {
    LdapTemplate t = ldapTemplate(LdapConfiguration.DEFAULT_BIND_DN,
      LdapConfiguration.DEFAULT_PASSWORD,
      LdapConfiguration.DEFAULT_PORT);
    
    final List<String> dns = t.search("", OBJECTCLASS_PRESENT,DN_MAPPER);

    assertThat(dns, hasItems(LdapConfiguration.DEFAULT_ROOT_OBJECT_DN));
  }
}
