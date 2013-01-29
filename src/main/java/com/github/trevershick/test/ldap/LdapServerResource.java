package com.github.trevershick.test.ldap;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.trevershick.test.ldap.annotations.LdapAttribute;
import com.github.trevershick.test.ldap.annotations.LdapConfiguration;
import com.github.trevershick.test.ldap.annotations.LdapEntry;
import com.github.trevershick.test.ldap.annotations.Ldif;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFReader;

@LdapConfiguration
public class LdapServerResource {

	private InMemoryDirectoryServer server;
	private LdapConfiguration config;

	public LdapServerResource(Object annotated) {
		this.config = annotated.getClass().getAnnotation(LdapConfiguration.class);
		if (this.config == null) {
			throw new IllegalArgumentException("annotated MUST be annotated with LdapConfiguration");
		}
	}

	public LdapServerResource() {
		this.config = LdapServerResource.class.getAnnotation(LdapConfiguration.class);
	}

	public void stop() {
		if (server != null) {
			server.shutDown(true);
		}
		server = null;
	}

	public boolean isStarted() {
		return this.server != null;
	}
	
	public boolean isStopped() {
		return !isStarted();
	}
	
	public LdapServerResource start() throws Exception {
		if (server != null) {
			throw new IllegalStateException("server is already initialized");
		}
		InMemoryDirectoryServerConfig c = new InMemoryDirectoryServerConfig(new DN(config.base().dn()));
		c.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("default", config.port()));
		c.addAdditionalBindCredentials(config.bindDn(), config.password());
		server = new InMemoryDirectoryServer(c);
		server.startListening();
		// initialize the user store
		loadLdifFiles();
		loadRootEntry();
		loadEntries();
		return this;
	}
	
	protected void loadRootEntry() throws LDAPException {
		SearchResultEntry entry = this.server.getEntry(config.base().dn());
		if (entry == null) {
			this.server.add(entry(config.base()));
		}
	}

	protected void loadEntries() throws LDAPException {
		LdapEntry[] entries = config.entries();
		if (entries == null || entries.length == 0)
			return;
		// build an entry from the annotation
		for (int i = 0; i < entries.length; i++) {
			Entry entry = entry(entries[i]);
			this.server.add(entry);
		}
	}

	/**
	 * Build an LDAP entry from the @LdapEntry annotation
	 */
	private Entry entry(LdapEntry ldapEntry) {
		Entry e = new Entry(ldapEntry.dn());
		e.addAttribute("objectClass", ldapEntry.objectclass());
		LdapAttribute[] attrs = ldapEntry.attributes();
		for (int i = 0; attrs != null && i < attrs.length; i++) {
			e.addAttribute(attrs[i].name(), attrs[i].value());
		}
		return e;
	}
	/**
	 * Load any LDIF files identified within the annotation @Ldif
	 */
	protected void loadLdifFiles() throws Exception {
		Iterable<String> ldifResources = ldifResources();
		for (String ldif : ldifResources) {
			InputStream resourceAsStream = getClass().getResourceAsStream(ldif);
			if (resourceAsStream == null) {
				throw new FileNotFoundException("Should be able to load " + ldif);
			}
			LDIFReader r = new LDIFReader(resourceAsStream);
			LDIFChangeRecord readEntry = null;
			while ((readEntry = r.readChangeRecord()) != null) {
				readEntry.processChange(server);
			}
			resourceAsStream.close();
		}
	}

	protected Iterable<String> ldifResources() {
		Ldif[] annotation = config.ldifs();
		List<String> ldifs = new ArrayList<String>(0);
		for (int i = 0; annotation != null && i < annotation.length; i++) {
			ldifs.add(annotation[i].value());
		}
		return ldifs;
	}

}
