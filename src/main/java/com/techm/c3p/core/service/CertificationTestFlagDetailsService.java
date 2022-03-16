package com.techm.c3p.core.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.techm.c3p.core.pojo.CertificationTestPojo;
import com.techm.c3p.core.pojo.CreateConfigRequestDCM;

@Component
public class CertificationTestFlagDetailsService {

	private static final Logger logger = LogManager.getLogger(CertificationTestFlagDetailsService.class);

	@Autowired
	private RequestInfoService requestInfoService;

	public CertificationTestPojo getPrevalidationTestFlag(CreateConfigRequestDCM createConfigRequestDCM)
			throws Exception {

		String requestId = createConfigRequestDCM.getRequestId();

		String TestType = createConfigRequestDCM.getTestType();

		String version = createConfigRequestDCM.getVersion_report();
		CertificationTestPojo certificationTestPojo = new CertificationTestPojo();

		try {

			if (!version.contains(".")) {
				version = version + ".0";
			}

			if ("preValidate".equalsIgnoreCase(TestType)) {
				certificationTestPojo = requestInfoService.getCertificationTestFlagData(requestId, version, TestType);
			}

			if ("networkTest".equalsIgnoreCase(TestType)) {
				certificationTestPojo = requestInfoService.getCertificationTestFlagData(requestId, version, TestType);
			}
			if ("HealthTest".equalsIgnoreCase(TestType)) {
				certificationTestPojo = requestInfoService.getCertificationTestFlagData(requestId, version, TestType);
			}

		} catch (Exception exe) {
			logger.error("Exception in getPrevalidationTestFlag method --> " + exe.getMessage());
		}
		return certificationTestPojo;
	}
}