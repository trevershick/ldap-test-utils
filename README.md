Test Utilities for working with LDAP 
====================================
[![Maven Status](https://maven-badges.herokuapp.com/maven-central/com.github.trevershick/ldap-test-utils/badge.svg?style=flat)](http://mvnrepository.com/artifact/com.github.trevershick/ldap-test-utils)
[![Build Status](https://travis-ci.org/trevershick/ldap-test-utils.png?branch=master)](https://travis-ci.org/trevershick/ldap-test-utils)
[![Coverage Status](https://coveralls.io/repos/github/trevershick/ldap-test-utils/badge.svg?branch=master)](https://coveralls.io/github/trevershick/ldap-test-utils?branch=master)
[![Dependency Status](https://www.versioneye.com/user/projects/582bad90c8dd330040426f8d/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/5827c5f22f4754004186d0b3)
[![License](http://img.shields.io/:license-mit-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)


This is a simple library that you can use which will make setting up
unboundid's embedded LDAP server a snap for your unit tests.  

I credit [this Stack Overflow answer](http://stackoverflow.com/questions/7269697/embedded-memory-ldap-server-solution/7274215), as well as [this blog post](http://ff1959.wordpress.com/2011/11/01/ldap-in-memory-directory-server-using-unboundid-ldap-sdk/) for getting me pointed in the right direction.

After playing around with the LDAP SDK and writing some tests, I found myself writing
a lot of boilerplate code.  I figured i'd extract out the details and make it REALLY 
easy to write an LDAP backed test.

Getting Started
---------------

*Note* - I don't have this code deployed in a Maven repository anywhere.  Thus, you'll have to download it and build it yourself.  If you have an internal repository yourself, you can deploy it there.

*JUnit users can use the supplied JUnit ``Rule`` (see further down)*

1. Import the dependency into your POM.
2. Create your test class
3. Annotate your test class (optional)
4. Create an instance of ``LdapServerResource`` in your setup code
5. Tear down the instance of ``LdapServerResource`` in your tear down code.
6. Run your test

The Dependency
--------------

	<dependency>
		<groupId>com.trevershick.test</groupId>
		<artifactId>ldap-test-utils</artifactId>
		<version>1.0.0-SNAPSHOT</version>
		<scope>test</scope>
	</dependency>

## Example Tests


### Default Configuration (no annotations)

Calling ``new LdapServerResource()`` with no constructor arguments forces the use of defaults.  All default values can be found in the ``LdapConfiguration`` annotation.

	public class DefaultAnnotationsTest {
		private LdapServerResource server;

		@Before
		public void startup() throws Exception { 
			server = new LdapServerResource().start();
		}

		@After
		public void shutdown() { server.stop(); }


		@Test
		public void testStartsUpWithDefaults() throws Exception {
			LdapContextSource s = new LdapContextSource();
			s.setPassword(LdapConfiguration.DEFAULT_PASSWORD);
			...
		}
	}

### Configuration via Annotations

The following example shows how to use annotations to alter the default configuration.


	package com.chickenshick.test.ldap;
	@LdapConfiguration(
		bindDn = "cn=Directory Manager",
		password = "mypass",
		port = 11111,
		base = @LdapEntry(dn = "dc=myroot", 
			objectclass = { "top", "domain" })
	)
	public class BasicCustomAnnotationsTest {
	
	private LdapServerResource server;
	
	@Before
	public void startup() throws Exception {
		server = new LdapServerResource(this).start();
	}
	
	@After
	public void shutdown() {
		server.stop();
	}

### Creating LDAP Entries via Annotations

By using the ``LdapEntry`` annotation, you can add entries to the LDAP server upon startup.  The example test below will add an *organizationalUnit* with the DN *ou=Groups,dc=root* to the LDAP DIT.

	@LdapConfiguration(
			entries={
				@LdapEntry(dn="ou=Groups,dc=root",objectclass="organizationalUnit",attributes={@LdapAttribute(name="ou",value="Groups")})	
			}
	)
	public class CustomEntriesAnnotationsTest {
		...
	}

### Creating LDAP Entries via LDIF Files

Alternately, you can use LDIF files to add entries (or schema changes).  The example below shows a test that will use the ``test.ldif`` file in the root of the classpath.

	@LdapConfiguration(
			ldifs = @Ldif("/test.ldif")
	)
	public class LdifLoadTest {
		...
	}

### Updating the LDAP Schema via LDIF Files

The in memory ldap server's schema can be altered via LDIF files.  The following stanza
 comes from schematest.ldif in the src/test folder of the project.  This file can be loaded
 just like any other LDIF file. 

    dn: cn=schema
    changetype: modify
    add: attributetypes
    attributetypes: ( 1.2.3.4.5.6.7 NAME 'userPrincipalName' DESC 'userPrincipalName as per Active Directory' EQUALITY caseIgnoreMatch SYNTAX '1.3.6.1.4.1.1466.115.121.1.15' )


## Example JUnit Test

	package com.github.trevershick.test.ldap.junit4;


	@LdapConfiguration(useRandomPortAsFallback=true)
	public class Junit4DefaultAnnotationsTest {

		/**
		 * Initializes the server
		 */
		@Rule
		public LdapServerRule rule = new LdapServerRule(this);

		@Test
		public void testStartsUpWithDefaults() throws Exception {
			LdapTemplate t = new LdapTemplate();
			...
		}
	}
 
## Useful Configuration Attributes
### useRandomPortAsFallback
By default, the LdapServer is configured to bind to port 10389.  This is fine if you're running the tasks on your machine manually.  However, if running in a CI environment with multiple LDAP tests running concurrently you could run into a BindException.  By default, ``LdapServerResource`` does NOT fallback to a random port.  If it can't bind to 10389 (or your configured port #) then it throws a ``BindException``.

You can alter this configuration option by specifying ``useRandomPortAsFallback=true`` on the ``@LdapConfiguration`` annotation.

The following test illustrates this feature.

	package com.github.trevershick.test.ldap;


	@LdapConfiguration(useRandomPortAsFallback=true)
	public class FallBackToRandomTest {
	
		@Test
		public void wontFallbackToRandom() throws Exception {
			LdapServerResource s1 = new LdapServerResource().start();
			assertEquals(LdapConfiguration.DEFAULT_PORT, s1.port());
		
			LdapServerResource s2 = new LdapServerResource(this).start();
			assertTrue(s2.isStarted());
			assertTrue(s2.port() != LdapConfiguration.DEFAULT_PORT);
		
			s1.stop();
			s2.stop();
		}
	}

### useSchema
By default, the LDAP server will use schema.
To disable this (particularly useful when you're doing TDD and building 
things up gradually) you can disable schema validation completely.


    @LdapConfiguration(useSchema=false)
    public class DisableSchemaTest {
    
        @Test
        public void wontValidateSchema() throws Exception {
            LdapServerResource s1 = new LdapServerResource(this);
            assertEquals("Schema should be null", null, s1.getServer().getConfig().getSchema());
        }
    }