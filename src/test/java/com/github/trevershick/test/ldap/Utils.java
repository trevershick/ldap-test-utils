package com.github.trevershick.test.ldap;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.PresentFilter;

public class Utils {
	public static class Spring {

		public static LdapTemplate ldapTemplate(String bindDn, String password, int port) {
			LdapTemplate t = new LdapTemplate();
			LdapContextSource s = new LdapContextSource();
			s.setPassword(password);
			s.setUserDn(bindDn);
			s.setUrl(String.format("ldap://localhost:%d", port));
			t.setContextSource(s);

			try {
				t.afterPropertiesSet();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			s.afterPropertiesSet();
			return t;
		}
	}

	public static class Mappers {

		public static final ContextMapper DN_MAPPER = new ContextMapper() {
			public Object mapFromContext(Object ctx) {
				DirContextAdapter context = (DirContextAdapter) ctx;
				return context.getDn().toString();
			}
		};
	}

	public static class Filters {
		public static final String OBJECTCLASS_PRESENT = new PresentFilter("objectclass").encode();

		public static final String DC_PRESENT = new PresentFilter("objectclass").encode();

		public static final String OU_PRESENT = new PresentFilter("ou").encode();
	}
}
