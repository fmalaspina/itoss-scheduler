package com.frsi.itoss.mgr.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@Profile("dev")
public class DataFillService {
//
//	@Autowired
//	LocationRepo locationRepo;
//	@Autowired
//	LocationTypeRepo locationTypeRepo;
//	@Autowired
//	CompanyTypeRepo companyTypeRepo;
//	@Autowired
//	WorkgroupTypeRepo workgroupTypeRepo;
//	@Autowired
//	UserAccountTypeRepo userAccountTypeRepo;
//	@Autowired
//	EventRuleRepo eventRuleRepo;
//	@Autowired
//	DashboardEntryRepo issueRepo;
//	@Autowired
//	ContainerRepo containerRepo;
//	@Autowired
//	ContainerTypeRepo containerTypeRepo;
//	@Autowired
//	PasswordEncoder passwordEncoder;
//	@Autowired
//	CtRepo ctRepo;
//	@Autowired
//	MonitoringProfileRepo monitoringProfRepo;
//	@Autowired
//	CryptoService cryptoService;
//	@Autowired
//	UserAccountRepo userAccountRepo;
//	@Autowired
//	WorkgroupRepo workgroupRepo;
//	@Autowired
//	CtTypeRepo ctTypeRepo;
//	@Autowired
//	Scheduler scheduler;
//	@Autowired
//	MetricRepo metricRepo;
//	@Autowired
//	CompanyRepo companyRepo;
//	@Autowired
//	InstrumentationRepo instRepo;
//	@Autowired
//	CollectorRepo collRepo;
//
//	@Autowired
//	MetricDataRepoImpl metricDataRepo;
//	@Autowired
//	MonitorRepo monitorRepo;
//	@Autowired
//	CtRelationRepo ctRelationRepo;
//	@Autowired
//	AnalizerRepo analizerRepo;
//	@Autowired
//	JmsTemplate jmsTemplate;
//	@Autowired
//	ManagerRepo managerRepo;
//
//	public Instrumentation ICMP;
//	public Instrumentation TCP;
//	public Instrumentation JDBC;
//	public Instrumentation EXEC;
//	public Instrumentation RFCREADTABLE;
//
//	@PostConstruct
//	void init() throws FileNotFoundException, Exception {
//
//		Optional<Instrumentation> RFCREADTABLEFound = instRepo.findById("RFCREADTABLE");
//		if (RFCREADTABLEFound.isPresent()) {
//			RFCREADTABLE = RFCREADTABLEFound.get();
//		} else {
//			RFCREADTABLE = new Instrumentation();
//		}
//
//		RFCREADTABLE.setName("RFCREADTABLE");
//		RFCREADTABLE.setDescription("RFCREADTABLE Instrumentation");
//		RFCREADTABLE.setInstrumentationParameters(Arrays.asList(new InstrumentationParameter("timeout", 3000,DataType.NUMBER, null,Source.MONITOR ),
//				new InstrumentationParameter("queryTable", "",DataType.TEXT, null,Source.METRIC),
//				new InstrumentationParameter("delimiter", "|",DataType.TEXT,null, Source.METRIC),
//				new InstrumentationParameter("rowskips", "0", DataType.TEXT,null, Source.METRIC),
//				new InstrumentationParameter("rowcount", "100",DataType.TEXT,null, Source.METRIC),
//				new InstrumentationParameter("queryStr", "", DataType.LIST,null, Source.METRIC),
//				new InstrumentationParameter("countResult", false, DataType.BOOLEAN, null,Source.METRIC),
//				new InstrumentationParameter("instance", "", DataType.TEXT, null,Source.CT),
//				new InstrumentationParameter("hostname", "", DataType.HOSTNAME,null, Source.CT),
//				new InstrumentationParameter("sysNr", "00", DataType.TEXT,null, Source.CT),
//				new InstrumentationParameter("clientNr", "000", DataType.TEXT, null,Source.CT),
//				new InstrumentationParameter("user", "", DataType.TEXT, null,Source.CT),
//				new InstrumentationParameter("password", "", DataType.PASSWORD, null,Source.CT),
//				new InstrumentationParameter("timezone", "", DataType.TEXT,null, Source.CT)
//				
//				));
//
//		RFCREADTABLE = instRepo.save(RFCREADTABLE);
//		
//		
//		Optional<Instrumentation> EXECFound = instRepo.findById("EXEC");
//		if (EXECFound.isPresent()) {
//			EXEC = EXECFound.get();
//		} else {
//			EXEC = new Instrumentation();
//		}
//
//		EXEC.setName("EXEC");
//		EXEC.setDescription("EXEC Instrumentation");
//		EXEC.setInstrumentationParameters(Arrays.asList(new InstrumentationParameter("timeout", 3000,DataType.NUMBER, null,Source.MONITOR),
//				new InstrumentationParameter("retries", 3, DataType.NUMBER, null,Source.MONITOR)));
//
//		EXEC = instRepo.save(EXEC);
//
//		Optional<Instrumentation> JDBCFound = instRepo.findById("JDBC");
//		if (JDBCFound.isPresent()) {
//			JDBC = JDBCFound.get();
//		} else {
//			JDBC = new Instrumentation();
//		}
//
//		JDBC.setName("JDBC");
//		JDBC.setDescription("JDBC Instrumentation");
//		JDBC.setInstrumentationParameters(Arrays.asList(new InstrumentationParameter("timeout", 3000,DataType.NUMBER, null,Source.MONITOR),
//				new InstrumentationParameter("port", 3000,DataType.NUMBER, null,Source.CT),
//				new InstrumentationParameter("url", "",DataType.TEXT,null, Source.CT),
//				new InstrumentationParameter("queryStr", 3000,DataType.TEXT, null,Source.METRIC),
//				new InstrumentationParameter("user", 3000,DataType.TEXT, null,Source.CT),
//				new InstrumentationParameter("password", 3000,DataType.PASSWORD, null,Source.CT),
//				new InstrumentationParameter("instance", 3000,DataType.TEXT, null,Source.CT)));
//
//		JDBC = instRepo.save(JDBC);
//
//		
//
//		Optional<Instrumentation> TCPFound = instRepo.findById("TCP");
//		if (TCPFound.isPresent()) {
//			TCP = TCPFound.get();
//		} else {
//			TCP = new Instrumentation();
//		}
//
//		TCP.setName("TCP");
//		TCP.setDescription("TCP Instrumentation");
//		TCP.setInstrumentationParameters(Arrays.asList(
//				new InstrumentationParameter("timeout", 3000, DataType.NUMBER, null,Source.MONITOR),
//				new InstrumentationParameter("hostname", "" ,DataType.HOSTNAME, null,Source.CT),
//				new InstrumentationParameter("port", 0 ,DataType.NUMBER, null,Source.CT)
//				));
//
//		TCP = instRepo.save(TCP);
//
//		Optional<Instrumentation> ICMPFound = instRepo.findById("ICMP");
//		if (ICMPFound.isPresent()) {
//			ICMP = ICMPFound.get();
//		} else {
//			ICMP = new Instrumentation();
//		}
//
//		ICMP.setName("ICMP");
//		ICMP.setDescription("ICMP Instrumentation");
//		ICMP.setInstrumentationParameters(Arrays.asList(new InstrumentationParameter("timeout", 3000,DataType.NUMBER,null, Source.MONITOR),
//				new InstrumentationParameter("concurrency", 1000,DataType.NUMBER,null, Source.MONITOR),
//				new InstrumentationParameter("retries", 3, DataType.NUMBER, null,Source.MONITOR), 
//				new InstrumentationParameter("pingmode", "INET_ADDRESS_REACHABLE_NEED_ROOT" ,DataType.TEXT, null,Source.MONITOR),
//				new InstrumentationParameter("hostname", "" ,DataType.HOSTNAME, null,Source.CT)
//				));
//
//		ICMP = instRepo.save(ICMP);
//
//		
//		//------------------------------------------------------------------------------------------------------------------------------------------
//		
//		// Containers
//		ContainerType semaphoreType = new ContainerType();
//		semaphoreType.setName("Semaphore Type");
//		List<TypeAttribute> semaphoreTypeAttrs = new ArrayList<>();
//		TypeAttribute severity = new TypeAttribute();
//		severity.setListValues(Arrays.asList("GREEN","YELLOW","RED"));
//		severity.setType(DataType.TEXT);
//		semaphoreTypeAttrs.add(severity);
//		semaphoreType.setTypeAttributes(semaphoreTypeAttrs);
//		semaphoreType = containerTypeRepo.save(semaphoreType);
//		
//		Container semaphore = new Container();
//		semaphore.setLabel("Semaphore container");
//		semaphore.setTooltip("Tootltip");
//		semaphore.setType(semaphoreType);
//		semaphore = containerRepo.save(semaphore);
//
//		// create and save types
//		CtType dbType = new CtType();
//		dbType.setName("DATABASE SERVER");
//
//		dbType = ctTypeRepo.save(dbType);
//
//		CtType mssqlType = new CtType();
//		mssqlType.setName("MSSQL");
//		mssqlType.setParent(dbType);
//		mssqlType = ctTypeRepo.save(mssqlType);
//		TypeAttribute mssqlhostname = new TypeAttribute();
//		mssqlhostname.setName("hostname");
//		mssqlhostname.setType(DataType.TEXT);
//		mssqlhostname.setIdPart(true);
//		mssqlhostname.setRequired(true);
//		TypeAttribute mssqlport = new TypeAttribute();
//		mssqlport.setName("port");
//		mssqlport.setType(DataType.NUMBER);
//		mssqlport.setRequired(true);
//		TypeAttribute mssqlurl = new TypeAttribute();
//		mssqlurl.setName("url");
//		mssqlurl.setType(DataType.TEXT);
//		mssqlurl.setRequired(true);
//		TypeAttribute mssqlavailtype = new TypeAttribute();
//		mssqlavailtype.setName("availtype");
//		mssqlavailtype.setType(DataType.MULTI_LIST);
//		mssqlavailtype.setListValues(Arrays.asList("MASTER-SLAVE", "MASTER-MASTER"));
//
//		TypeAttribute mssqlinstance = new TypeAttribute();
//		mssqlinstance.setName("instance");
//		mssqlinstance.setType(DataType.TEXT);
//		mssqlinstance.setRequired(true);
//		mssqlinstance.setIdPart(true);
//
//		TypeAttribute mssqldatabase = new TypeAttribute();
//		mssqldatabase.setName("database");
//		mssqldatabase.setType(DataType.TEXT);
//		mssqldatabase.setRequired(true);
//		mssqldatabase.setIdPart(true);
//
//		TypeAttribute mssqluser = new TypeAttribute();
//		mssqluser.setName("user");
//		mssqluser.setType(DataType.TEXT);
//		mssqluser.setRequired(true);
//
//		TypeAttribute mssqlpassword = new TypeAttribute();
//		mssqlpassword.setName("password");
//		mssqlpassword.setType(DataType.PASSWORD);
//		mssqlpassword.setRequired(true);
//
//		mssqlType.setTypeAttributes(Arrays.asList(mssqlport, mssqlhostname, mssqlinstance, mssqldatabase, mssqlurl,
//				mssqluser, mssqlpassword, mssqlavailtype));
//
//		mssqlType = ctTypeRepo.save(mssqlType);
//
//		CtType oracleType = new CtType();
//		oracleType.setName("ORACLE");
//		oracleType.setParent(dbType);
//		oracleType = ctTypeRepo.save(oracleType);
//		TypeAttribute orclhostname = new TypeAttribute();
//		orclhostname.setName("hostname");
//		orclhostname.setType(DataType.HOSTNAME);
//		orclhostname.setIdPart(true);
//		orclhostname.setRequired(true);
//		TypeAttribute orclport = new TypeAttribute();
//		orclport.setName("port");
//		orclport.setType(DataType.NUMBER);
//		orclport.setRequired(true);
//		TypeAttribute orclurl = new TypeAttribute();
//		orclurl.setName("url");
//		orclurl.setType(DataType.TEXT);
//		orclurl.setRequired(true);
//
//		TypeAttribute orclinstance = new TypeAttribute();
//		orclinstance.setName("instance");
//		orclinstance.setType(DataType.TEXT);
//		orclinstance.setRequired(true);
//		orclinstance.setIdPart(true);
//
//		TypeAttribute orcluser = new TypeAttribute();
//		orcluser.setName("user");
//		orcluser.setType(DataType.TEXT);
//		orcluser.setRequired(true);
//
//		TypeAttribute orclpassword = new TypeAttribute();
//		orcluser.setName("password");
//		orcluser.setType(DataType.PASSWORD);
//		orcluser.setRequired(true);
//
//		oracleType.setTypeAttributes(Arrays.asList(orclport, orclhostname, orclinstance, orcluser, orclpassword));
//
//		oracleType = ctTypeRepo.save(oracleType);
//
//		CtType sapType = new CtType();
//
//		sapType.setName("SAP");
//		CtType serverType = new CtType();
//		serverType.setName("SERVER");
//
//		sapType = ctTypeRepo.save(sapType);
//
//		CtType sapInstanceType = new CtType();
//		sapInstanceType.setName("SAP_INSTANCE");
//		sapInstanceType.setParent(sapType);
//		
//		TypeAttribute sysNr = new TypeAttribute();
//		sysNr.setIdPart(true);
//		sysNr.setName("sysNr");
//		sysNr.setRequired(true);
//		sysNr.setType(DataType.NUMBER);
//
//		TypeAttribute hostname = new TypeAttribute();
//		hostname.setName("hostname");
//		hostname.setType(DataType.HOSTNAME);
//		hostname.setIdPart(true);
//		hostname.setRequired(true);
//
//		sapInstanceType.setTypeAttributes(Arrays.asList(sysNr, hostname));
//		sapInstanceType = ctTypeRepo.save(sapInstanceType);
//		serverType = ctTypeRepo.save(serverType);
//
//		CtType saprouterType = new CtType();
//		saprouterType.setName("SAPROUTER");
//		saprouterType.setParent(sapType);
//		TypeAttribute port = new TypeAttribute();
//		port.setName("port");
//		port.setType(DataType.NUMBER);
//		port.setRequired(true);
//
//		saprouterType.setTypeAttributes(Arrays.asList(port, hostname));
//
//		saprouterType = ctTypeRepo.save(saprouterType);
//
//		CtType windowsType = new CtType();
//		windowsType.setName("WINDOWS");
//		windowsType.setTypeAttributes(Arrays.asList(hostname));
//		windowsType.setParent(serverType);
//
//		windowsType = ctTypeRepo.save(windowsType);
//
//		// collector
//
//		Collector coll = new Collector();
//		coll.addInstrumentation(EXEC);
//		coll.addInstrumentation(TCP);
//		coll.addInstrumentation(ICMP);
//		coll.addInstrumentation(JDBC);
//		coll.addInstrumentation(RFCREADTABLE);
//		
//		coll.setEndpoint("COLLECTOR-1-ARGENTINA");
//		coll.setName("Collector basico de Argentina 1");
//
//		coll = collRepo.save(coll);
//
//		Manager manager = new Manager();
//		manager.setName("Manager 1 de Argentina");
//		manager.setEndpoint("MANAGER-1-ARGENTINA");
//
//		Analizer analizer = new Analizer();
//		analizer.setName("Analizador de Argentina");
//		analizer.addCollector(coll);
//
//		analizer.setEndpoint("ANALIZER-1-ARGENTINA");
//
//		analizer = analizerRepo.save(analizer);
//		manager.addAnalizer(analizer);
//		manager = managerRepo.save(manager);
//		analizer = analizerRepo.save(analizer);
//
//		coll = collRepo.save(coll);
//
//		// assign type to ct
//		Ct sapInstance = new Ct();
//		sapInstance.setType(sapInstanceType);
//		sapInstance.setEnvironment("PRODUCTION");
//		sapInstance.setState(CtState.MAINTENANCE);
//		Attribute sapInstHostnameValue = new Attribute();
//		sapInstHostnameValue.setName("hostname");
//		sapInstHostnameValue.setValue("bue-lx-pgr.bunge.ar");
//
//		Attribute sapInstSysnrValue = new Attribute();
//		sapInstSysnrValue.setName("sysNr");
//		sapInstSysnrValue.setValue("00");
//
//		Attribute sapInstClientnrValue = new Attribute();
//		sapInstClientnrValue.setName("clientNr");
//		sapInstClientnrValue.setValue("000");
//		
//		Attribute sapInstUserValue = new Attribute();
//		sapInstUserValue.setName("user");
//		sapInstUserValue.setValue("extdbchakoch");
//		
//		Attribute sapInstPasswordValue = new Attribute();
//		sapInstPasswordValue.setName("password");
//		sapInstPasswordValue.setValue(cryptoService.encrypt("Soporte0"));
//		
//		
//		Attribute sapInstTimezoneValue = new Attribute();
//		sapInstTimezoneValue.setName("timezone");
//		sapInstTimezoneValue.setValue("UTC-3");
//
//		Attribute sapInstInstanceValue = new Attribute();
//		sapInstInstanceValue.setName("instance");
//		sapInstInstanceValue.setValue("PGR");
//		
//		
//		
//		
//		
//		List<Attribute> sapInstCtAttrs = new ArrayList<>();
//
//		sapInstCtAttrs.add(sapInstHostnameValue);
//		sapInstCtAttrs.add(sapInstSysnrValue);
//		sapInstCtAttrs.add(sapInstClientnrValue);
//		sapInstCtAttrs.add(sapInstUserValue);
//		sapInstCtAttrs.add(sapInstPasswordValue);
//		sapInstCtAttrs.add(sapInstTimezoneValue);
//		sapInstCtAttrs.add(sapInstInstanceValue);
//		sapInstance.setAttributes(sapInstCtAttrs);
//		
//		sapInstance = ctRepo.save(sapInstance);
//
//		Ct mssql = new Ct();
//		mssql.setType(mssqlType);
//		mssql.setState(CtState.OPERATIONS);
//		mssql.setEnvironment("PRODUCTION");
//		Attribute mssqlhostnameValue = new Attribute();
//		mssqlhostnameValue.setName("hostname");
//		mssqlhostnameValue.setValue("octsqlltrprd.octagon.ar");
//
//		Attribute mssqlportValue = new Attribute();
//		mssqlportValue.setName("port");
//		mssqlportValue.setValue(1433);
//
//		Attribute mssqlurlValue = new Attribute();
//		mssqlurlValue.setName("url");
//		mssqlurlValue.setValue("jdbc:sqlserver://octsqlltrprd.octagon.ar");
//
//		Attribute mssqlinstanceValue = new Attribute();
//		mssqlinstanceValue.setName("instance");
//		mssqlinstanceValue.setValue("");
//
//		Attribute mssqldatabaseValue = new Attribute();
//		mssqldatabaseValue.setName("database");
//		mssqldatabaseValue.setValue("");
//
//		Attribute mssqluserValue = new Attribute();
//		mssqluserValue.setName("user");
//		mssqluserValue.setValue("hp_dbspi");
//
//		Attribute mssqlpasswordValue = new Attribute();
//		mssqlpasswordValue.setName("password");
//		mssqlpasswordValue.setValue(cryptoService.encrypt("hpdbspi"));
//
//		Attribute mssqlavailtypeValue = new Attribute();
//		mssqlavailtypeValue.setName("availtype");
//		mssqlavailtypeValue.setValue(Arrays.asList("MASTER-MASTER", "MASTER-SLAVE"));
//
//		List<Attribute> mssqlctAttrs = new ArrayList<>();
//
//		mssqlctAttrs.add(mssqlhostnameValue);
//		mssqlctAttrs.add(mssqlportValue);
//		mssqlctAttrs.add(mssqlurlValue);
//		mssqlctAttrs.add(mssqlinstanceValue);
//		mssqlctAttrs.add(mssqldatabaseValue);
//		mssqlctAttrs.add(mssqluserValue);
//		mssqlctAttrs.add(mssqlpasswordValue);
//		mssqlctAttrs.add(mssqlavailtypeValue);
//		mssql.setAttributes(mssqlctAttrs);
//
//		mssql = ctRepo.save(mssql);
//
//		Ct oracle = new Ct();
//		oracle.setType(oracleType);
//		oracle.setState(CtState.OPERATIONS);
//		oracle.setEnvironment("PRODUCTION");
//		Attribute orclhostnameValue = new Attribute();
//		orclhostnameValue.setName("Hostname ");
//		orclhostnameValue.setValue("localhost");
//
//		Attribute orclportValue = new Attribute();
//		orclportValue.setName("port");
//		orclportValue.setValue(1521);
//
//		Attribute orclurlValue = new Attribute();
//		orclurlValue.setName("url");
//		orclurlValue.setValue("jdbc:oracle:thin:@//localhost:1521/xe");
//
//		Attribute orclinstanceValue = new Attribute();
//		orclinstanceValue.setName("instance");
//		orclinstanceValue.setValue("itoss");
//
//		Attribute orcluserValue = new Attribute();
//		orcluserValue.setName("user");
//		orcluserValue.setValue("itoss");
//
//		Attribute orclpasswordValue = new Attribute();
//		orclpasswordValue.setName("password");
//		orclpasswordValue.setValue(cryptoService.encrypt("itosspwd"));
//
//		List<Attribute> orclctAttrs = new ArrayList<>();
//
//		orclctAttrs.add(orclhostnameValue);
//		orclctAttrs.add(orclportValue);
//		orclctAttrs.add(orclurlValue);
//		orclctAttrs.add(orclinstanceValue);
//		orclctAttrs.add(orcluserValue);
//		orclctAttrs.add(orclpasswordValue);
//		oracle.setAttributes(orclctAttrs);
//
//		oracle = ctRepo.save(oracle);
//
//		Ct saprouter = new Ct();
//		saprouter.setType(saprouterType);
//		saprouter.setState(CtState.DELIVERY);
//		saprouter.setEnvironment("PRODUCTION");
//		Attribute portValue = new Attribute();
//		portValue.setName("port");
//		portValue.setValue(3299);
//		Attribute hostnameValue = new Attribute();
//		hostnameValue.setName("hostname");
//		hostnameValue.setValue("saprouter.bunge.ar");
//
//		List<Attribute> ctAttrs = new ArrayList<>();
//		ctAttrs.add(portValue);
//		ctAttrs.add(hostnameValue);
//
//		saprouter.setAttributes(ctAttrs);
//
//		saprouter = ctRepo.save(saprouter);
//
//		Ct saprouter2 = new Ct();
//		saprouter2.setType(saprouterType);
//		saprouter2.setEnvironment("PRODUCTION");
//		saprouter2.setState(CtState.OPERATIONS);
//		portValue = new Attribute();
//		portValue.setName("port");
//		portValue.setValue(3299);
//		hostnameValue = new Attribute();
//		hostnameValue.setName("hostname");
//		hostnameValue.setValue("gbdsap.globant.ar");
//
//		List<Attribute> ctAttrs2 = new ArrayList<>();
//		ctAttrs2.add(portValue);
//		ctAttrs2.add(hostnameValue);
//
//		saprouter2.setAttributes(ctAttrs2);
//
//		saprouter2 = ctRepo.save(saprouter2);
//
//		// assign ct to ctType
//
//		saprouterType = ctTypeRepo.save(saprouterType);
//
//		Ct windows = new Ct();
//		windows.setType(windowsType);
//		windows.setState(CtState.OPERATIONS);
//		windows.setEnvironment("PRODUCTION");
//
//		hostnameValue = new Attribute();
//		hostnameValue.setName("hostname");
//		hostnameValue.setValue("bdsap.globant.ar");
//
//		List<Attribute> ctAttrs3 = new ArrayList<>();
//
//		ctAttrs3.add(hostnameValue);
//
//		windows.setAttributes(ctAttrs3);
//		windows = ctRepo.save(windows);
//
//		Ct windows2 = new Ct();
//		windows2.setType(windowsType);
//		windows2.setState(CtState.OPERATIONS);
//		windows2.setEnvironment("PRODUCTION");
//
//		Attribute hostnamew2Value = new Attribute();
//		hostnamew2Value.setName("hostname");
//		hostnamew2Value.setValue("gbdsap.globant.ar");
//
//		List<Attribute> ctAttrsw2 = new ArrayList<>();
//
//		ctAttrsw2.add(hostnamew2Value);
//
//		windows2.setAttributes(ctAttrsw2);
//		windows2 = ctRepo.save(windows2);
//
//		windows2.setEnvironment("TEST");
//		windows2 = ctRepo.save(windows2);
//		windowsType = ctTypeRepo.save(windowsType);
//
//		// creación de perfil
//
//		Metric mssqlstatusMetric = new Metric();
//		mssqlstatusMetric.setCtType(oracleType);
//		mssqlstatusMetric.setInstrumentation(JDBC);
//		
//		mssqlstatusMetric.setTaskInstrumentationParameterValues(Arrays.asList(new InstrumentationParameterValue("queryStr","SELECT name,state_desc FROM sys.databases")));
//		mssqlstatusMetric.setName("MSSQL_STATUS");
//		mssqlstatusMetric.setStatusMetric(true);
//		mssqlstatusMetric.setMetricCategory(MetricCategory.Availability);
//
//		
//		MetricPayloadAttributes mssqlstatusAttrs = new MetricPayloadAttributes();
//		// orclstatusAttrs.setTags(Arrays.asList(new Tag(1, "hostname", "text"),new
//		// Tag(2, "instance", "text")));
//		mssqlstatusAttrs.setFields(Arrays.asList(new Field(1, "name", DataType.TEXT), new Field(2, "state_desc", DataType.TEXT)));
//
//		mssqlstatusMetric.setMetricPayloadAttributes(mssqlstatusAttrs);
//
//		mssqlstatusMetric = metricRepo.save(mssqlstatusMetric);
//
//		Monitor mssqlstatusmonitor = new Monitor();
//		mssqlstatusmonitor.setMetric(mssqlstatusMetric);
//		List<InstrumentationParameterValue> mssqlmonitorStatusOList = new ArrayList<>();
//		mssqlmonitorStatusOList.add(new InstrumentationParameterValue("timeout", 3000));
//		
//		mssqlstatusmonitor.setTaskInstrumentationParameterValues(mssqlmonitorStatusOList);
//		mssqlstatusmonitor.setFrequencyExpression("0/10 * * * * ?");
//		mssqlstatusmonitor.setName("MSSQL status monitor de Desarrollo");
//
//		List<EventRule> mssqlEventCond = new ArrayList<>();
//		EventRule mssqlEc = new EventRule();
//		mssqlEc.setCondition("true");
//		mssqlEc.setName("dummy");
//		mssqlEc.setDescription("MSSQL Status down");
//		mssqlEc.setActions("action.save();action.createDashEntry(2,'GREEN');System.out.println('THIS RULE --->' +action.ruleEvaluated.toString());");
//		mssqlEc.setPriority(1);
//		mssqlEc = eventRuleRepo.save(mssqlEc);
//		
//		EventRule mssqlEc2 = new EventRule();
//		mssqlEc2.setCondition("metric.fields.fault");
//		mssqlEc2.setName("dummy");
//		mssqlEc2.setDescription("MSSQL Status down");
//		mssqlEc2.setActions("action.save();action.createDashEntry(2,'YELLOW');System.out.println('THIS RULE --->' +action.ruleEvaluated.toString());");
//		mssqlEc2.setPriority(2);
//		mssqlEc2 = eventRuleRepo.save(mssqlEc2);
//		
//		
//		
//		mssqlEventCond.add(mssqlEc);
//		mssqlEventCond.add(mssqlEc2);
//		mssqlstatusmonitor.setEventRules(mssqlEventCond);
//		mssqlstatusmonitor.setRuleEvaluationMode(RuleEvaluationMode.SingleRule);
//		
//		mssqlstatusmonitor = monitorRepo.save(mssqlstatusmonitor);
//
//		Metric orcltsMetric = new Metric();
//		orcltsMetric.setCtType(oracleType);
//		orcltsMetric.setInstrumentation(JDBC);
//		
//		orcltsMetric.setTaskInstrumentationParameterValues(Arrays.asList(new InstrumentationParameterValue("queryStr",
//				"SELECT df.tablespace_name TS_NAME,ROUND(100 * (fs.free_space / df.total_space),2) PERCENT_FREE_SPACE FROM (SELECT tablespace_name, SUM(bytes) TOTAL_SPACE,ROUND(SUM(bytes) / 1048576) TOTAL_SPACE_MB FROM dba_data_files GROUP BY tablespace_name) df,(SELECT tablespace_name, SUM(bytes) FREE_SPACE, ROUND(SUM(bytes) / 1048576) FREE_SPACE_MB FROM dba_free_space GROUP BY tablespace_name) fs WHERE df.tablespace_name = fs.tablespace_name(+) ORDER BY fs.tablespace_name")));
//		
//		orcltsMetric.setName("ORACLE_TS_FREE_SPACE");
//
//		orcltsMetric.setMetricCategory(MetricCategory.Capacity);
//
//		MetricPayloadAttributes orcltsAttrs = new MetricPayloadAttributes();
//		// orclstatusAttrs.setTags(Arrays.asList(new Tag(1, "hostname", "text"),new
//		// Tag(2, "instance", "text")));
//		orcltsAttrs.setFields(
//				Arrays.asList(new Field(1, "ts_name", DataType.TEXT), new Field(2, "percent_free_space", DataType.NUMBER)));
//
//		orcltsMetric.setMetricPayloadAttributes(orcltsAttrs);
//
//		orcltsMetric = metricRepo.save(orcltsMetric);
//
//		Monitor orcltsmonitor = new Monitor();
//		orcltsmonitor.setMetric(orcltsMetric);
//		List<InstrumentationParameterValue> orclmonitorTsOList = new ArrayList<>();
//		orclmonitorTsOList.add(new InstrumentationParameterValue("timeout", 3000));
//		
//		orcltsmonitor.setTaskInstrumentationParameterValues(orclmonitorTsOList);
//		orcltsmonitor.setFrequencyExpression("0/10 * * * * ?");
//		orcltsmonitor.setName("ORACLE ts monitor de Desarrollo");
//
//		Metric orclstatusMetric = new Metric();
//		orclstatusMetric.setCtType(oracleType);
//		orclstatusMetric.setInstrumentation(JDBC);
//		
//		orclstatusMetric.setTaskInstrumentationParameterValues(Arrays.asList(
//				new InstrumentationParameterValue("queryStr","SELECT STATUS, DATABASE_STATUS, TO_TIMESTAMP_TZ(TO_CHAR(cast(startup_time as timestamp) at time zone 'UTC')) AS STARTUP_TIME FROM sys.v_$instance")
//				
//				
//				));
//		
//		
//		orclstatusMetric.setName("ORACLE_STATUS");
//		orclstatusMetric.setStatusMetric(true);
//		orclstatusMetric.setMetricCategory(MetricCategory.Availability);
//
//		MetricPayloadAttributes orclstatusAttrs = new MetricPayloadAttributes();
//		// orclstatusAttrs.setTags(Arrays.asList(new Tag(1, "hostname", "text"),new
//		// Tag(2, "instance", "text")));
//		orclstatusAttrs.setFields(Arrays.asList(new Field(1, "status", DataType.TEXT), new Field(2, "database_status", DataType.TEXT),
//				new Field(3, "startup_time", DataType.TIME)));
//
//		orclstatusMetric.setMetricPayloadAttributes(orclstatusAttrs);
//
//		orclstatusMetric = metricRepo.save(orclstatusMetric);
//
//		Monitor orclstatusmonitor = new Monitor();
//		orclstatusmonitor.setMetric(orclstatusMetric);
//		List<InstrumentationParameterValue> orclmonitorOList = new ArrayList<>();
//		orclmonitorOList.add(new InstrumentationParameterValue("timeout", 3000));
//		orclmonitorOList.add(new InstrumentationParameterValue("classname", "oracle.jdbc.driver.OracleDriver"));
//		orclstatusmonitor.setTaskInstrumentationParameterValues(orclmonitorOList);
//		orclstatusmonitor.setFrequencyExpression("0/10 * * * * ?");
//		orclstatusmonitor.setName("DATABASE monitor de Desarrollo");
//
//		List<EventRule> orclTsEventCond = new ArrayList<>();
//		EventRule orclTsEc1 = new EventRule();
//		orclTsEc1.setCondition("true");
//		orclTsEc1.setName("dummy");
//		orclTsEc1.setDescription("dummy");
//		orclTsEc1.setActions("action.save();");
//		orclTsEc1.setPriority(1);
//		orclTsEc1 = eventRuleRepo.save(orclTsEc1);
////		orclEventCond.add(orclEc1);
////		orclEventCond.add(orclEc2);
//		orclTsEventCond.add(orclTsEc1);
//		orcltsmonitor.setEventRules(orclTsEventCond);
//		orcltsmonitor.setRuleEvaluationMode(RuleEvaluationMode.SingleRule);
//		orcltsmonitor = monitorRepo.save(orcltsmonitor);
//
//		List<EventRule> orclEventCond = new ArrayList<>();
////		EventRule orclEc1 = new EventRule();
////		orclEc1.setCondition("!metric.isAlertActive() && (metric.fields.fault || metric.fields.status != 'OPEN' || metric.fields.database_status != 'ACTIVE')");
////		orclEc1.setName("Oracle_Down");
////		orclEc1.setDescription("Oracle_down");
////		orclEc1.setActions(
////				"metric.save();metric.alert(1);metric.setStatusDown();metric.log(metric.ct.getStringValue('instance') + ' is down.');");
////		orclEc1.setPriority(1);
////		
////		EventRule orclEc2 = new EventRule();
////		orclEc2.setCondition("metric.isAlertActive() && (!metric.fields.fault && metric.fields.status == 'OPEN' || metric.fields.database_status == 'ACTIVE')");
////		orclEc2.setName("Oracle_Up");
////		orclEc2.setDescription("Oracle_UP");
////		orclEc2.setActions(
////				"metric.save();metric.reset(1);");
////		orclEc2.setPriority(1);
////		
//		EventRule orclEc3 = new EventRule();
//		orclEc3.setCondition("true");
//		orclEc3.setName("dummy");
//		orclEc3.setDescription("dummy");
//		orclEc3.setActions("action.save();action.createDashEntry(2,'GREEN');");
//		orclEc3.setPriority(1);
//		orclEc3 = eventRuleRepo.save(orclEc3);
////		orclEventCond.add(orclEc1);
////		orclEventCond.add(orclEc2);
//		orclEventCond.add(orclEc3);
//		orclstatusmonitor.setEventRules(orclEventCond);
//		orclstatusmonitor.setRuleEvaluationMode(RuleEvaluationMode.SingleRule);
//		orclstatusmonitor = monitorRepo.save(orclstatusmonitor);
//
//		MonitoringProfile orclprofile = new MonitoringProfile();
//		orclprofile.setName("Profile de monitoreo oracle de desarrollo");
//
//		orclprofile.setCtType(oracleType);
//		orclprofile.addMonitor(orclstatusmonitor);
//		orclprofile.addMonitor(orcltsmonitor);
//		orclprofile = monitoringProfRepo.save(orclprofile);
//
//		oracleType = ctTypeRepo.save(oracleType);
//		orclstatusmonitor = monitorRepo.save(orclstatusmonitor);
//		oracle.setMonitoringProfile(orclprofile);
//		oracle = ctRepo.save(oracle);
//
//		MonitoringProfile mssqlprofile = new MonitoringProfile();
//		mssqlprofile.setName("Profile de monitoreo mssql de desarrollo");
//
//		mssqlprofile.setCtType(mssqlType);
//
//		mssqlprofile.addMonitor(mssqlstatusmonitor);
//		mssqlprofile = monitoringProfRepo.save(mssqlprofile);
//
//		mssqlType = ctTypeRepo.save(mssqlType);
//		mssqlstatusmonitor = monitorRepo.save(mssqlstatusmonitor);
//		mssql.setMonitoringProfile(mssqlprofile);
//		mssql = ctRepo.save(mssql);
//
////		coll.addCollects(oracle);
////		coll.addCollects(mssql);
////		coll.addCollects(sapInstance);
//		coll = collRepo.save(coll);
//		oracle.setCollector(coll);
//		mssql.setCollector(coll);
//		oracle = ctRepo.save(oracle);
//		mssql = ctRepo.save(mssql);
//		coll = collRepo.save(coll);
//
//		Metric icmpMetric = new Metric();
//		icmpMetric.setCtType(windowsType);
//		icmpMetric.setInstrumentation(ICMP);
//		icmpMetric.setName("ICMP_STATUS");
//		icmpMetric.setStatusMetric(true);
//		icmpMetric.setMetricCategory(MetricCategory.Availability);
//
//		Metric execMetric = new Metric();
//		execMetric.setCtType(windowsType);
//		execMetric.setInstrumentation(EXEC);
//		execMetric.setName("MEMORY");
//		execMetric.setStatusMetric(false);
//		execMetric.setMetricCategory(MetricCategory.Performance);
//		execMetric.setTaskInstrumentationParameterValues(Arrays.asList(new InstrumentationParameterValue("externalCommand","systeminfo"),
//				new InstrumentationParameterValue("pattern","[\\s\\S]+Total Physical Memory:[\\s]+(-?\\d*\\.{0,1}\\d+).*MB[\\s\\S]+Available Physical Memory:[\\s]+(-?\\d*\\.{0,1}\\d+).*MB.*"),
//				new InstrumentationParameterValue("multiline",false),
//				new InstrumentationParameterValue("startRow",0),
//				new InstrumentationParameterValue("endRow",0)
//				
//				));
//		
//		
//		Metric dumpsMetric = new Metric();
//		dumpsMetric.setCtType(sapInstanceType);
//		dumpsMetric.setInstrumentation(RFCREADTABLE);
//		dumpsMetric.setName("ABAP_DUMPS");
//		dumpsMetric.setStatusMetric(false);
//		dumpsMetric.setMetricCategory(MetricCategory.Performance);
//		
//
//		dumpsMetric.setTaskInstrumentationParameterValues(Arrays.asList(
//				new InstrumentationParameterValue("queryTable","SNAP"),
//				new InstrumentationParameterValue("delimiter","|"),
//				new InstrumentationParameterValue("rowskips","0"),
//				new InstrumentationParameterValue("rowcount","100"),
//				new InstrumentationParameterValue("countResult",true),
//				new InstrumentationParameterValue("queryStr",Arrays.asList("DATUM GE ${lastReadDate()} AND UZEIT GE ${lastReadTime()} AND"
//						," UZEIT LE ${readTime()} AND SEQNO EQ '000'"))));
//		
//		MetricPayloadAttributes dumpspayloadAttrs = new MetricPayloadAttributes();
//
//		dumpspayloadAttrs
//				.setFields(Arrays.asList(new Field(1, "DATUM", DataType.TEXT), new Field(2, "UZEIT", DataType.TEXT),new Field(3, "UNAME", DataType.TEXT),new Field(4, "FLIST", DataType.TEXT),new Field(5, "SEQNO", DataType.TEXT)));
//		dumpsMetric.setMetricPayloadAttributes(dumpspayloadAttrs);
//
//		dumpsMetric = metricRepo.save(dumpsMetric);
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		Metric sapcontrolMetric = new Metric();
//		sapcontrolMetric.setCtType(sapInstanceType);
//		sapcontrolMetric.setInstrumentation(EXEC);
//		sapcontrolMetric.setName("SAPCONTROL_PROCESS_STATUS");
//		sapcontrolMetric.setStatusMetric(false);
//		sapcontrolMetric.setMetricCategory(MetricCategory.Availability);
//		
//
//		sapcontrolMetric.setTaskInstrumentationParameterValues(Arrays.asList(new InstrumentationParameterValue("externalCommand","sapcontrol -nr ${sysNr} -host ${hostname} -function GetProcessList"),
//				new InstrumentationParameterValue("pattern","([A-Za-z\\+\\_\\s]+),[A-Za-z\\+\\_\\s]+,([A-Za-z\\+\\_\\s]+).*"),
//				new InstrumentationParameterValue("multiline",true),
//				new InstrumentationParameterValue("startRow",6),
//				new InstrumentationParameterValue("endRow",0)
//				
//				));
//		
//		MetricPayloadAttributes sapcontrolpayloadAttrs = new MetricPayloadAttributes();
//
//		sapcontrolpayloadAttrs
//				.setFields(Arrays.asList(new Field(1, "process", DataType.TEXT), new Field(2, "status", DataType.TEXT)));
//		sapcontrolMetric.setMetricPayloadAttributes(sapcontrolpayloadAttrs);
//
//		sapcontrolMetric = metricRepo.save(sapcontrolMetric);
//
//		Monitor sapcontrolmonitor = new Monitor();
//		sapcontrolmonitor.setMetric(sapcontrolMetric);
//		List<InstrumentationParameterValue> sapcontrolOList = new ArrayList<>();
//		sapcontrolOList.add(new InstrumentationParameterValue("timeout", 10000));
//
//		sapcontrolOList.add(new InstrumentationParameterValue("retries", 3));
//
//		sapcontrolmonitor.setTaskInstrumentationParameterValues(sapcontrolOList);
//		sapcontrolmonitor.setFrequencyExpression("0/10 * * * * ?");
//		sapcontrolmonitor.setName("SAPCONTROL de Desarrollo");
//
//
//		Monitor dumpsmonitor = new Monitor();
//		dumpsmonitor.setMetric(dumpsMetric);
//		List<InstrumentationParameterValue> dumpsOList = new ArrayList<>();
//		dumpsOList.add(new InstrumentationParameterValue("timeout", 10000));
//		dumpsmonitor.setTaskInstrumentationParameterValues(dumpsOList);
//		dumpsmonitor.setFrequencyExpression("0/10 * * * * ?");
//		dumpsmonitor.setName("DUMPS de Desarrollo");
//
//		
//		
//		
//		
//		
//		
//		
//		MetricPayloadAttributes icmppayloadAttrs = new MetricPayloadAttributes();
//		icmppayloadAttrs.setTags(Arrays.asList(new Tag(1, "hostname", DataType.TEXT)));
//		icmppayloadAttrs.setFields(Arrays.asList(new Field(2, "status", DataType.TEXT)));
//		icmpMetric.setMetricPayloadAttributes(icmppayloadAttrs);
//
//		icmpMetric = metricRepo.save(icmpMetric);
//
////		
//		MetricPayloadAttributes execpayloadAttrs = new MetricPayloadAttributes();
//
//		execpayloadAttrs
//				.setFields(Arrays.asList(new Field(1, "total_memory", DataType.FLOAT), new Field(2, "avail_memory", DataType.FLOAT)));
//		execMetric.setMetricPayloadAttributes(execpayloadAttrs);
//
//		execMetric = metricRepo.save(execMetric);
//
//		//
//
//		Monitor execmonitor = new Monitor();
//		execmonitor.setMetric(execMetric);
//		List<InstrumentationParameterValue> execOList = new ArrayList<>();
//		execOList.add(new InstrumentationParameterValue("timeout", 10000));
//
//		execOList.add(new InstrumentationParameterValue("retries", 3));
//
//		execmonitor.setTaskInstrumentationParameterValues(execOList);
//		execmonitor.setFrequencyExpression("0/10 * * * * ?");
//		execmonitor.setName("EXEC de Desarrollo");
//
//		List<EventRule> execEventCond = new ArrayList<>();
//		EventRule execEc1 = new EventRule();
//		execEc1.setCondition("true");
//		execEc1.setName("Exec");
//		execEc1.setDescription("Exec");
//		execEc1.setActions("action.save();");
//		execEc1.setPriority(1);
//		execEc1 = eventRuleRepo.save(execEc1);
//
//		
//		List<EventRule> sapcontrolEventCond = new ArrayList<>();
//		EventRule sapcontrolEc1 = new EventRule();
//		sapcontrolEc1.setCondition("true");
//		sapcontrolEc1.setName("Sapcontrol proces");
//		sapcontrolEc1.setDescription("Sapcontrol");
//		sapcontrolEc1.setActions("action.save();action.createDashEntry(2,'YELLOW');");
//		sapcontrolEc1.setPriority(1);
//		sapcontrolEc1 = eventRuleRepo.save(sapcontrolEc1);
//		sapcontrolEventCond.add(sapcontrolEc1);
//		sapcontrolmonitor.setEventRules(sapcontrolEventCond);
//		sapcontrolmonitor.setRuleEvaluationMode(RuleEvaluationMode.SingleRule);
//		sapcontrolmonitor = monitorRepo.save(sapcontrolmonitor);
//		
//		
//		List<EventRule> dumpsEventCond = new ArrayList<>();
//		EventRule dumpsEc1 = new EventRule();
//		dumpsEc1.setCondition("true");
//		dumpsEc1.setName("Dumps");
//		dumpsEc1.setDescription("Dumps ABAP");
//		dumpsEc1.setActions("action.increment(1, 'MINUTES');action.saveOnChange();");
//		dumpsEc1.setPriority(1);
//		dumpsEc1 = eventRuleRepo.save(dumpsEc1);
//		dumpsEventCond.add(dumpsEc1);
//		dumpsmonitor.setEventRules(dumpsEventCond);
//		dumpsmonitor.setRuleEvaluationMode(RuleEvaluationMode.SingleRule);
//		dumpsmonitor = monitorRepo.save(dumpsmonitor);
//		
//		
//		
//		
//		
//		execEventCond.add(execEc1);
//		execmonitor.setEventRules(execEventCond);
//		execmonitor.setRuleEvaluationMode(RuleEvaluationMode.SingleRule);
//		execmonitor = monitorRepo.save(execmonitor);
//
//		
//
//		
//		
//		
//		
//		Monitor icmpmonitor = new Monitor();
//		icmpmonitor.setMetric(icmpMetric);
//		List<InstrumentationParameterValue> icmpnmOList = new ArrayList<>();
//		icmpnmOList.add(new InstrumentationParameterValue("timeout", 3000));
//		icmpnmOList.add(new InstrumentationParameterValue("concurrency", 1000));
//		icmpnmOList.add(new InstrumentationParameterValue("retries", 3));
//		icmpnmOList.add(new InstrumentationParameterValue("pingmode", "INET_ADDRESS_REACHABLE_NEED_ROOT"));
//		icmpmonitor.setTaskInstrumentationParameterValues(icmpnmOList);
//		icmpmonitor.setFrequencyExpression("0/10 * * * * ?");
//		icmpmonitor.setName("ICMP de Desarrollo");
//
//		List<EventRule> icmpEventCond = new ArrayList<>();
//		EventRule icmpEc1 = new EventRule();
//		icmpEc1.setCondition("!action.isAlertActive() && metric.fields.status != 'LIVE'");
//		icmpEc1.setName("Icmp_Down");
//		icmpEc1.setDescription("Icmp_down");
//		icmpEc1.setActions(
//				"action.createDashEntry(2,'RED');System.out.println('Icmp_Down = !action.isAlertActive() && metric.fields.status != LIVE -> TRUE');action.setStatusDown();action.save();action.alert();action.log(metric.tags.hostname + ' is down.');");
//		icmpEc1.setPriority(1);
//		icmpEc1 = eventRuleRepo.save(icmpEc1);
//		EventRule icmpEc2 = new EventRule();
//		icmpEc2.setCondition("metric.fields.status == 'LIVE'");
//		icmpEc2.setName("Icmp_UP");
//		icmpEc2.setDescription("Icmp_up");
//		icmpEc2.setActions(
//				"action.createDashEntry(2,'GREEN');System.out.println('Icmp_UP = action.isAlertActive(95) && metric.fields.status == LIVE -> TRUE');action.setStatusUp();action.save();action.reset(95);action.log(metric.tags.hostname + ' is up.');");
//		icmpEc2.setPriority(2);
//		icmpEc2 = eventRuleRepo.save(icmpEc2);
//		EventRule icmpEc3 = new EventRule();
//		icmpEc3.setCondition("true");
//		icmpEc3.setName("dummy");
//		icmpEc3.setDescription("dummy");
//		icmpEc3.setActions(
//				"System.out.println(action.createDashEntry(2,'RED');'WINDOWS IS DOWN STATUS:---->' + metric.tags.hostname + ' STATUS: ' + action.isDownStatus() + ' THIS RULE IS --->'+action.ruleEvaluated.getName() + ' DESC ->' +action.ruleEvaluated.getDescription());action.save();action.createDashEntry(1,'RED');");
//		icmpEc3.setPriority(3);
//		icmpEc3 = eventRuleRepo.save(icmpEc3);
//		icmpEventCond.add(icmpEc1);
//		icmpEventCond.add(icmpEc2);
//		icmpEventCond.add(icmpEc3);
//
//		icmpmonitor.setEventRules(icmpEventCond);
//		icmpmonitor.setRuleEvaluationMode(RuleEvaluationMode.SingleRule);
//		icmpmonitor = monitorRepo.save(icmpmonitor);
//
//		MonitoringProfile icmpprofile = new MonitoringProfile();
//		icmpprofile.setName("Profile de monitoreo icmp y exec de desarrollo");
//
//		icmpprofile.setCtType(windowsType);
//		icmpprofile.addMonitor(icmpmonitor);
//		icmpprofile.addMonitor(execmonitor);
//		icmpprofile = monitoringProfRepo.save(icmpprofile);
//
//		windowsType = ctTypeRepo.save(windowsType);
//		icmpmonitor = monitorRepo.save(icmpmonitor);
//		windows.setMonitoringProfile(icmpprofile);
//		windows2.setMonitoringProfile(icmpprofile);
//		windows = ctRepo.save(windows);
//		windows2 = ctRepo.save(windows2);
//
//		Metric srMetric = new Metric();
//		srMetric.setCtType(saprouterType);
//		srMetric.setInstrumentation(TCP);
//		srMetric.setName("SAPROUTER_STATUS");
//		srMetric.setStatusMetric(true);
//		srMetric.setMetricCategory(MetricCategory.Availability);
//		MetricPayloadAttributes payloadAttrs = new MetricPayloadAttributes();
//		payloadAttrs.setTags(Arrays.asList(new Tag(1, "hostname", DataType.TEXT)));
//		payloadAttrs.setFields(Arrays.asList(new Field(2, "status", DataType.TEXT)));
//		srMetric.setMetricPayloadAttributes(payloadAttrs);
//
//		srMetric = metricRepo.save(srMetric);
//		// Create monitor for Saprouter metric
//
//		Monitor monitor = new Monitor();
//		monitor.setMetric(srMetric);
//		List<InstrumentationParameterValue> nmOList = new ArrayList<>();
//		nmOList.add(new InstrumentationParameterValue("timeout", 3000));
//		nmOList.add(new InstrumentationParameterValue("concurrency", 1000));
//
//		monitor.setTaskInstrumentationParameterValues(nmOList);
//		monitor.setFrequencyExpression("0/10 * * * * ?");
//		monitor.setName("SAPROUTER de Desarrollo");
//
//		List<EventRule> srEventCond = new ArrayList<>();
//		EventRule srEc = new EventRule();
//		srEc.setCondition("metric.fields.status != '0 SUCCESSFUL'");
//		srEc.setName("Saprouter");
//		srEc.setDescription("SAPROUTER_down");
//		srEc.setActions(
//				"System.out.println('STATUS = ' + metric.fields.status  + ' SEVERITY: CRITICAL');action.save();action.alert()");
//		srEc.setPriority(1);
//		srEc = eventRuleRepo.save(srEc);
//		srEventCond.add(srEc);
//		monitor.setEventRules(srEventCond);
//		monitor.setRuleEvaluationMode(RuleEvaluationMode.SingleRule);
//		monitor = monitorRepo.save(monitor);
//
//		monitor = monitorRepo.save(monitor);
//
//		
//		MonitoringProfile sapprofile = new MonitoringProfile();
//		sapprofile.setName("Profile de monitoreo sapcontrol y abap de desarrollo");
//
//		sapprofile.setCtType(sapInstanceType);
//		sapprofile.addMonitor(sapcontrolmonitor);
//		sapprofile.addMonitor(dumpsmonitor);
//		sapprofile = monitoringProfRepo.save(sapprofile);
//
//		
//
//		sapcontrolmonitor = monitorRepo.save(sapcontrolmonitor);
//		
//		
//		MonitoringProfile profile = new MonitoringProfile();
//		profile.setName("Profile de monitoreo saprouters de desarrollo");
//
//		profile.setCtType(saprouterType);
//		profile.addMonitor(monitor);
//		profile = monitoringProfRepo.save(profile);
//
//		saprouterType = ctTypeRepo.save(saprouterType);
//		monitor = monitorRepo.save(monitor);
//		saprouter.setMonitoringProfile(profile);
//		saprouter = ctRepo.save(saprouter);
//
//		saprouter2.setMonitoringProfile(profile);
//		saprouter2 = ctRepo.save(saprouter2);
//
//		monitor = monitorRepo.save(monitor);
//
//		sapInstance.setMonitoringProfile(sapprofile);
//		
//		
////		coll.addCollects(saprouter);
////		coll.addCollects(windows);
////		coll.addCollects(windows2);
////		coll.addCollects(saprouter2);
////		coll.addCollects(sapInstance);
//		coll = collRepo.save(coll);
//		saprouter.setCollector(coll);
//		saprouter2.setCollector(coll);
//		windows.setCollector(coll);
//		windows2.setCollector(coll);
//		sapInstance.setCollector(coll);
//		saprouter = ctRepo.save(saprouter);
//		saprouter2 = ctRepo.save(saprouter2);
//		windows = ctRepo.save(windows);
//		windows2 = ctRepo.save(windows2);
//		sapInstance = ctRepo.save(sapInstance);
//		coll = collRepo.save(coll);
//		
////		CtRelation ctRelFrom1 = new CtRelation("IMPACTED_BY", 100, windows);
////		CtRelation ctRelFrom2 = new CtRelation("IMPACTED_BY", 100, windows);
////		CtRelation ctRelFrom3 = new CtRelation("IMPACTED_BY", 100, windows);
////		ctRelFrom1 = ctRelationRepo.save(ctRelFrom1);
////		ctRelFrom2 = ctRelationRepo.save(ctRelFrom2);
////		ctRelFrom3 = ctRelationRepo.save(ctRelFrom3);
////		saprouter.setCtRelations(Sets.newHashSet(ctRelFrom1));
////		sapInstance.setCtRelations(Sets.newHashSet(ctRelFrom3));
////		saprouter = ctRepo.save(saprouter);
////		windows = ctRepo.save(windows);
////		oracle.setCtRelations(Sets.newHashSet(ctRelFrom2));
////		oracle = ctRepo.save(oracle);
//
//		// sender.send(windows.getId());
//
////		Location parent = new Location();
////		parent.setName("Argentina");
////		parent = locationRepo.save(parent);
////		Location children = new Location();
////		children.setName("Buenos Aires");
////
////		children = locationRepo.save(children);
////		children.setParent(parent);
////		children = locationRepo.save(children);
////		// locationRepo.save(parent);
////
////		Workgroup saplatam = new Workgroup();
////		saplatam.setName("Saplatam");
////		workgroupRepo.save(saplatam);
////		UserAccount operator = new UserAccount();
////		operator.setName("Adrian DeViccenzo");
////		operator.setUsername("adrian");
////		operator.setPassword(passwordEncoder.encode("user"));
////		operator = userAccountRepo.save(operator);
////		Set<UserAccount> personSet = new HashSet<>();
////		personSet.add(operator);
////		saplatam.setUserAccounts(personSet);
////		saplatam = workgroupRepo.save(saplatam);
////		
//		
////		sapInstance.setWorkgroup(saplatam);
////		sapInstance = ctRepo.save(sapInstance);
////		
////		Set<Workgroup> wgSet = new HashSet<>();
////		wgSet.add(saplatam);
////		//operator.setWorkgroups(wgSet);
////
////		Workgroup sap = new Workgroup();
////		sap.setName("SAP Regional");
////
////		UserAccount managerUser = new UserAccount();
////		managerUser.setName("Daniel Bit");
////		managerUser.setUsername("daniel");
////		managerUser.setPassword(passwordEncoder.encode("manager"));
////		managerUser = userAccountRepo.save(managerUser);
////		sap.setWorkgroupManager(managerUser);
////		sap = workgroupRepo.save(sap);
////
////		wgSet.add(sap);
//
////		Company bunge = new Company();
////		bunge.setName("Bunge");
////		bunge = companyRepo.save(bunge);
////		Company parentOrg = new Company();
////		parentOrg.setName("Bunge Matriz Multinacional");
////		parentOrg = companyRepo.save(parentOrg);
////		bunge.setParent(parentOrg);
////		bunge = companyRepo.save(bunge);
////
////		
////
////		//operator.setWorkgroups(wgSet);
////		operator = userAccountRepo.save(operator);
////
////		LocationType paisType = new LocationType();
////		paisType.setName("Pais");
////
////		TypeAttribute idioma = new TypeAttribute();
////		idioma.setName("idioma");
////		idioma.setType(DataType.TEXT);
////		idioma.setIdPart(true);
////		idioma.setRequired(true);
////
////		paisType.setTypeAttributes(Arrays.asList(idioma));
////		paisType = locationTypeRepo.save(paisType);
////
////		parent.setType(paisType);
////		parent = locationRepo.save(parent);
//
//		WorkgroupType support1stType = new WorkgroupType();
//		support1stType.setName("1st Line Support Workgroup");
//
//		WorkgroupType support2ndType = new WorkgroupType();
//		support2ndType.setName("2nd Line Support Workgroup");
//
//		TypeAttribute groupMail = new TypeAttribute();
//		groupMail.setName("groupMail");
//		groupMail.setType(DataType.TEXT);
//		groupMail.setIdPart(true);
//		groupMail.setRequired(true);
//
//		support1stType.setTypeAttributes(Arrays.asList(groupMail));
//		support1stType = workgroupTypeRepo.save(support1stType);
//
//		support2ndType.setTypeAttributes(Arrays.asList(groupMail));
//		support2ndType = workgroupTypeRepo.save(support2ndType);
//
////		saplatam.setParent(sap);
////
////		saplatam.setType(support1stType);
////		saplatam = workgroupRepo.save(saplatam);
////
////		sap.setType(support2ndType);
////		sap = workgroupRepo.save(sap);
//
////		bunge.setLocation(parent);
////		bunge = companyRepo.save(bunge);
////		
//		//
//		portValue.setName("port");
//		portValue.setValue(3298);
//		hostnameValue.setName("hostname");
//		hostnameValue.setValue("saprouter0.bunge.ar");
//
//		ctAttrs = new ArrayList<>();
//		ctAttrs.add(portValue);
//		ctAttrs.add(hostnameValue);
//
//		saprouter.setAttributes(ctAttrs);
//
//		saprouter = ctRepo.save(saprouter);
//		
//		portValue.setName("port");
//		portValue.setValue(3299);
//		hostnameValue.setName("hostname");
//		hostnameValue.setValue("saprouter.bunge.ar");
//
//		ctAttrs = new ArrayList<>();
//		ctAttrs.add(portValue);
//		ctAttrs.add(hostnameValue);
//
//		saprouter.setAttributes(ctAttrs);
//		
//		saprouter = ctRepo.save(saprouter);
//		

//	}

}
