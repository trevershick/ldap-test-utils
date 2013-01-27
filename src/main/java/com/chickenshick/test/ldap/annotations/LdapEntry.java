package com.chickenshick.test.ldap.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public abstract @interface LdapEntry {
	String dn();
	String[] objectclass();
	LdapAttribute[] attributes() default {};
}
