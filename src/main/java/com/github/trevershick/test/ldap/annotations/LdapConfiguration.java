package com.github.trevershick.test.ldap.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LdapConfiguration {
	public static final String DEFAULT_ROOT_OBJECT_DN = "dc=root";
	public static final String DEFAULT_ROOT_ENTRY_OBJECTCLASS = "domain";
	public static final int DEFAULT_PORT = 10389;
	public static final String DEFAULT_PASSWORD = "password";
	public static final String DEFAULT_BIND_DN = "cn=admin";
	
	boolean useRandomPortAsFallback() default false;
	int port() default DEFAULT_PORT;
	LdapEntry base() default @LdapEntry(objectclass=DEFAULT_ROOT_ENTRY_OBJECTCLASS,dn=DEFAULT_ROOT_OBJECT_DN);
	LdapEntry[] entries() default {};
	Ldif[] ldifs() default {};
	String bindDn() default DEFAULT_BIND_DN;
	String password() default DEFAULT_PASSWORD;
}
