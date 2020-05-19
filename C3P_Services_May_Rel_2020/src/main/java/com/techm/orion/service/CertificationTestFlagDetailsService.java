package com.techm.orion.service;


import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.CertificationTestPojo;
import com.techm.orion.pojo.CreateConfigRequestDCM;


public class CertificationTestFlagDetailsService {

	
	
	public CertificationTestPojo getPrevalidationTestFlag(CreateConfigRequestDCM createConfigRequestDCM) throws Exception
	{
	    
		String requestId=createConfigRequestDCM.getRequestId();
		
		String TestType=createConfigRequestDCM.getTestType();
		
		String version=createConfigRequestDCM.getVersion_report();
		CertificationTestPojo certificationTestPojo=new CertificationTestPojo();
		
		RequestInfoDao requestInfoDao=new RequestInfoDao();
		
		try{

			if(!version.contains("."))
			{
				version=version+".0";
			}
		
		if(TestType.equalsIgnoreCase("preValidate"))
		{
			certificationTestPojo=requestInfoDao.getCertificationTestFlagData(requestId,version,TestType);
		}
		
		if(TestType.equalsIgnoreCase("networkTest"))
		{
			certificationTestPojo=requestInfoDao.getCertificationTestFlagData(requestId,version,TestType);
		}
		if(TestType.equalsIgnoreCase("HealthTest"))
		{
			certificationTestPojo=requestInfoDao.getCertificationTestFlagData(requestId,version,TestType);
		}
		
	    }
	    catch (Exception ex) {
			ex.printStackTrace();
		}
	    return certificationTestPojo;
	}

}
