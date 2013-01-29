package com.github.trevershick.test.ldap.annotations;

public @interface LdapAttribute {
	String name();
	String[] value();
}
