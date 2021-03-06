package springacltutorial.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import springacltutorial.model.Record;
import springacltutorial.model.Report;
import springacltutorial.model.User;

/**
 * The simplest possible implementation of AclService interface. Uses in-memory collection of ACLs, providing fast and easy access to them.
 * 
 */
@Service
public class InMemoryAclServiceImpl implements AclService {
	static Logger logger = LoggerFactory.getLogger(InMemoryAclServiceImpl.class);

	Map<ObjectIdentity, Acl> acls = new HashMap<ObjectIdentity, Acl>();

	@PostConstruct
	public void initializeACLs() {
		// create ACLs according to requirements of tutorial application
		ObjectIdentity user1 = new ObjectIdentityImpl(User.class, "empl1");
		ObjectIdentity user2 = new ObjectIdentityImpl(User.class, "empl2");
		ObjectIdentity user3 = new ObjectIdentityImpl(User.class, "empl3");
		ObjectIdentity user4 = new ObjectIdentityImpl(User.class, "empl4");

		ObjectIdentity report1 = new ObjectIdentityImpl(Report.class, 1);
		ObjectIdentity report2 = new ObjectIdentityImpl(Report.class, 2);

		ObjectIdentity record1 = new ObjectIdentityImpl(Record.class, 1);
		ObjectIdentity record2 = new ObjectIdentityImpl(Record.class, 2);

		ObjectIdentity classRecordServices = new ObjectIdentityImpl(Class.class, "class springacltutorial.services.RecordServices");
		ObjectIdentity methodCreateRecord = new ObjectIdentityImpl(MethodInvocation.class,
				"public java.lang.Long springacltutorial.services.RecordServices.createRecord(springacltutorial.model.User,java.lang.String)");
		ObjectIdentity methodGetRecord = new ObjectIdentityImpl(MethodInvocation.class,
				"public springacltutorial.model.Record springacltutorial.services.RecordServices.getRecord(springacltutorial.model.User,java.lang.Long)");
		ObjectIdentity methodGetRecords = new ObjectIdentityImpl(MethodInvocation.class,
				"public java.util.Collection springacltutorial.services.RecordServices.getRecords(springacltutorial.model.User)");

		// Object에 대해 entry list 를 정의
		Acl acl1 = new SimpleAclImpl(user1, new ArrayList<AccessControlEntry>());
		acl1.getEntries().add(new AccessControlEntryImpl("ace1", acl1, new PrincipalSid("manager1"), ExtendedPermission.ACCEPT, true, true, true));
		acls.put(acl1.getObjectIdentity(), acl1);

		Acl acl2 = new SimpleAclImpl(user2, new ArrayList<AccessControlEntry>());
		acl2.getEntries().add(new AccessControlEntryImpl("ace2", acl2, new PrincipalSid("manager1"), ExtendedPermission.ACCEPT, true, true, true));
		acls.put(acl2.getObjectIdentity(), acl2);

		Acl acl3 = new SimpleAclImpl(user3, new ArrayList<AccessControlEntry>());
		acl3.getEntries().add(new AccessControlEntryImpl("ace3", acl3, new PrincipalSid("manager2"), ExtendedPermission.ACCEPT, true, true, true));
		acls.put(acl3.getObjectIdentity(), acl3);

		Acl acl4 = new SimpleAclImpl(user4, new ArrayList<AccessControlEntry>());
		acl4.getEntries().add(new AccessControlEntryImpl("ace4", acl4, new PrincipalSid("manager2"), ExtendedPermission.ACCEPT, true, true, true));
		acls.put(acl4.getObjectIdentity(), acl4);

		Acl acl5 = new SimpleAclImpl(report1, new ArrayList<AccessControlEntry>());
		acl5.getEntries().add(new AccessControlEntryImpl("ace5", acl5, new PrincipalSid("manager1"), BasePermission.READ, true, true, true));
		acls.put(acl5.getObjectIdentity(), acl5);

		Acl acl6 = new SimpleAclImpl(report2, new ArrayList<AccessControlEntry>());
		acl6.getEntries().add(new AccessControlEntryImpl("ace6", acl6, new PrincipalSid("manager2"), BasePermission.READ, true, true, true));
		acls.put(acl6.getObjectIdentity(), acl6);

		Acl acl12 = new SimpleAclImpl(classRecordServices, new ArrayList<AccessControlEntry>());
		acl12.getEntries().add(new AccessControlEntryImpl("ace12", acl12, new GrantedAuthoritySid("ROLE_ADMIN"), BasePermission.READ, true, true, true));
		acls.put(acl12.getObjectIdentity(), acl12);

		// 하나의 ACL 에 여러 entry 등록
		Acl acl7 = new SimpleAclImpl(methodCreateRecord, new ArrayList<AccessControlEntry>(), acl12);
		acl7.getEntries().add(new AccessControlEntryImpl("ace7", acl7, new GrantedAuthoritySid("ROLE_MANAGER"), BasePermission.READ, true, true, true));
		acl7.getEntries().add(new AccessControlEntryImpl("ace7", acl7, new PrincipalSid("consumer"), BasePermission.READ, true, true, true));
		acls.put(acl7.getObjectIdentity(), acl7);

		Acl acl8 = new SimpleAclImpl(methodGetRecord, new ArrayList<AccessControlEntry>(), acl12);
		acl8.getEntries().add(new AccessControlEntryImpl("ace8", acl8, new GrantedAuthoritySid("ROLE_MANAGER"), BasePermission.READ, true, true, true));
		acl8.getEntries().add(new AccessControlEntryImpl("ace8", acl8, new GrantedAuthoritySid("ROLE_EMPLOYEE"), BasePermission.READ, true, true, true));
		acl8.getEntries().add(new AccessControlEntryImpl("ace8", acl8, new PrincipalSid("consumer"), BasePermission.READ, true, true, true));
		acls.put(acl8.getObjectIdentity(), acl8);

		Acl acl9 = new SimpleAclImpl(methodGetRecords, new ArrayList<AccessControlEntry>(), acl12);
		acl9.getEntries().add(new AccessControlEntryImpl("ace9", acl9, new GrantedAuthoritySid("ROLE_MANAGER"), BasePermission.READ, true, true, true));
		acl9.getEntries().add(new AccessControlEntryImpl("ace9", acl9, new GrantedAuthoritySid("ROLE_EMPLOYEE"), BasePermission.READ, true, true, true));
		acl9.getEntries().add(new AccessControlEntryImpl("ace9", acl8, new PrincipalSid("consumer"), BasePermission.READ, true, true, true));
		acls.put(acl9.getObjectIdentity(), acl9);

		Acl acl10 = new SimpleAclImpl(record1, new ArrayList<AccessControlEntry>());
		acl10.getEntries().add(new AccessControlEntryImpl("ace10", acl10, new PrincipalSid("manager1"), BasePermission.READ, true, true, true));
		acls.put(acl10.getObjectIdentity(), acl10);

		Acl acl11 = new SimpleAclImpl(record2, new ArrayList<AccessControlEntry>());
		acl11.getEntries().add(new AccessControlEntryImpl("ace11", acl11, new PrincipalSid("manager2"), BasePermission.READ, true, true, true));
		acls.put(acl11.getObjectIdentity(), acl11);
	}

	public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
		// I'm not really sure what this method should do...
		throw new UnsupportedOperationException("Not implemented");
	}

	@SuppressWarnings("serial")
	@Override
	public Acl readAclById(final ObjectIdentity object, List<Sid> sids) throws NotFoundException {
		List<ObjectIdentity> objectIdList = new ArrayList<ObjectIdentity>();
		objectIdList.add(object);
		Map<ObjectIdentity, Acl> map = readAclsById(objectIdList, sids);

		Assert.isTrue(map.containsKey(object), "There should have been an Acl entry for ObjectIdentity " + object);

		return map.get(object);
	}

	public Acl readAclById(ObjectIdentity object) throws NotFoundException {
		return readAclById(object, null);
	}

	@Override
	public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects) throws NotFoundException {
		return readAclsById(objects, null);
	}

	@Override
	public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects, List<Sid> sids) throws NotFoundException {
		logger.debug("readAclsById(List<ObjectIdentity> objects, List<Sid> sids) 메소드 수행");

		Map<ObjectIdentity, Acl> result = new HashMap<ObjectIdentity, Acl>();
		for (ObjectIdentity object : objects) {
			if (acls.containsKey(object)) {
				result.put(object, acls.get(object));
			} else {
				throw new NotFoundException("Unable to find ACL information for object identity '" + object.toString() + "'");
			}
		}
		return result;
	}
}
