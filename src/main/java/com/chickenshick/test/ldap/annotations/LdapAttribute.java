package com.chickenshick.test.ldap.annotations;

public @interface LdapAttribute {
	String name();
	String[] value();
}
