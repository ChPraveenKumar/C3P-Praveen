package com.techm.c3p.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.techm.c3p.core.pojo.CertificationTestPojo;
import com.techm.c3p.core.pojo.CreateConfigRequestDCM;

@Component
public class CertificationTestFlagDetailsService {

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

			if (TestType.equalsIgnoreCase("preValidate")) {
				certificationTestPojo = requestInfoService.getCertificationTestFlagData(requestId, version, TestType);
			}

			if (TestType.equalsIgnoreCase("networkTest")) {
				certificationTestPojo = requestInfoService.getCertificationTestFlagData(requestId, version, TestType);
			}
			if (TestType.equalsIgnoreCase("HealthTest")) {
				certificationTestPojo = requestInfoService.getCertificationTestFlagData(requestId, version, TestType);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return certificationTestPojo;
	}
}