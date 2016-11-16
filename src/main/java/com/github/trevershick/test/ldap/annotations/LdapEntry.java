package com.github.trevershick.test.ldap.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public abstract @interface LdapEntry {
  String dn();

  String[] objectclass();

  LdapAttribute[] attributes() default {};
}
