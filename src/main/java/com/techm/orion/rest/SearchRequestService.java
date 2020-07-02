package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.models.VersioningJSONModel;
import com.techm.orion.pojo.ReoprtFlags;
import com.techm.orion.pojo.RequestInfoSO;
import com.techm.orion.pojo.SearchParamPojo;

@Controller
@RequestMapping("/SearchRequestService")
public class SearchRequestService implements Observer {

	RequestInfoDao requestInfoDao = new RequestInfoDao();

	@POST
	@RequestMapping(value = "/search", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response search(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		String jsonArrayReports = "";
		String key = null, value = null, page = null;
		List<ReoprtFlags> reoportflagllist = new ArrayList<ReoprtFlags>();
		List<ReoprtFlags> reoportflagllistforselectedRecord = new ArrayList<ReoprtFlags>();
		List<RequestInfoSO> finalList = new ArrayList<RequestInfoSO>();
		List<VersioningJSONModel> model = new ArrayList<VersioningJSONModel>();
		VersioningJSONModel modelObj = null;
		RequestInfoSO objToAdd = null;

		RequestInfoSO comapreObj;
		List<RequestInfoSO> compareList = new ArrayList<RequestInfoSO>();
		ReoprtFlags selected;
		try {
			Gson gson = new Gson();
			SearchParamPojo dto = gson.fromJson(searchParameters,
					SearchParamPojo.class);
			key = dto.getKey();
			value = dto.getValue();
			page = dto.getPage();

			List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();
			if (value != null && !value.isEmpty()) {
				try {
					// quick fix for json not getting serialized

					detailsList = requestInfoDao.searchRequestsFromDB(key,
							value);
					reoportflagllist = requestInfoDao
							.getReportsInfoForAllRequestsDB();
					if (page.equalsIgnoreCase("dashboard")) {
						for (int i = 0; i < detailsList.size(); i++) {
							comapreObj = new RequestInfoSO();
							comapreObj = detailsList.get(i);
							boolean isPresent = false;
							if (finalList.size() > 0) {
								for (int j = 0; j < finalList.size(); j++) {
									if (comapreObj
											.getDisplay_request_id()
											.equalsIgnoreCase(
													finalList
															.get(j)
															.getDisplay_request_id())) {
										isPresent = true;
										break;
									}
								}
								if (isPresent == false) {
									for (int k = 0; k < detailsList.size(); k++) {
										if (comapreObj
												.getDisplay_request_id()
												.equalsIgnoreCase(
														detailsList
																.get(k)
																.getDisplay_request_id())) {
											compareList.add(detailsList.get(k));
										}
									}
									finalList.add(compareList.get(0));
									compareList.clear();
								}
							} else {
								for (int k = 0; k < detailsList.size(); k++) {
									if (comapreObj
											.getDisplay_request_id()
											.equalsIgnoreCase(
													detailsList
															.get(k)
															.getDisplay_request_id())) {
										compareList.add(detailsList.get(k));
									}
								}
								finalList.add(compareList.get(0));
								compareList.clear();
							}
						}

						if (finalList.size() > 0) {
							for (int i = 0; i < reoportflagllist.size(); i++) {
								if (reoportflagllist
										.get(i)
										.getRequestId()
										.equalsIgnoreCase(
												Integer.toString(finalList.get(
														0).getRequest_id()))) {
									selected = new ReoprtFlags();
									selected = reoportflagllist.get(i);
									reoportflagllistforselectedRecord
											.add(selected);
								}
							}
						}
						jsonArrayReports = new Gson()
								.toJson(reoportflagllistforselectedRecord);
						jsonArray = new Gson().toJson(finalList);
						obj.put(new String("output"), jsonArray);
						obj.put(new String("ReportStatus"), jsonArrayReports);

					} else if (page.equalsIgnoreCase("viewpage")) {
						if (detailsList.size() > 0) {
							for (int i = 0; i < detailsList.size(); i++) {
								boolean flag = false;
								if (model.size() > 0) {
									for (int j = 0; j < model.size(); j++) {
										if (model
												.get(j)
												.getRequest_display_id()
												.equalsIgnoreCase(
														detailsList
																.get(i)
																.getDisplay_request_id())) {
											flag = true;
											break;
										}
									}
								}
								if (flag == false) {
									modelObj = new VersioningJSONModel();
									objToAdd = new RequestInfoSO();
									objToAdd = detailsList.get(i);
									modelObj.setRequest_display_id(objToAdd
											.getDisplay_request_id());
									modelObj.setRequest_customer_name(objToAdd
											.getCustomer());
									modelObj.setRequest_device(objToAdd
											.getDeviceType());
									modelObj.setRequest_service(objToAdd
											.getService());
									modelObj.setRequest_site_id(objToAdd
											.getSiteid());
									modelObj.setRequest_model(objToAdd
											.getModel());
									modelObj.setRequest_hostname(objToAdd
											.getHostname());
									modelObj.setRequest_id(Integer
											.toString(objToAdd.getRequest_id()));
									List<RequestInfoSO> listToAdd = new ArrayList<RequestInfoSO>();
									for (int k = 0; k < detailsList.size(); k++) {
										if (objToAdd
												.getDisplay_request_id()
												.equalsIgnoreCase(
														detailsList
																.get(k)
																.getDisplay_request_id())) {
											listToAdd.add(detailsList.get(k));
										}
									}
									modelObj.setListOfRequests(listToAdd);
									model.add(modelObj);
								}

							}
							if (model.size() > 0) {
								for (int i = 0; i < reoportflagllist.size(); i++) {
									if (reoportflagllist
											.get(i)
											.getRequestId()
											.equalsIgnoreCase(
													model.get(0)
															.getRequest_id())) {
										selected = new ReoprtFlags();
										selected = reoportflagllist.get(i);
										reoportflagllistforselectedRecord
												.add(selected);
									}
								}
							}
							jsonArrayReports = new Gson()
									.toJson(reoportflagllistforselectedRecord);
							jsonArray = new Gson().toJson(model);
							obj.put(new String("output"), jsonArray);
							obj.put(new String("ReportStatus"),
									jsonArrayReports);
							obj.put(new String("Status"), "success");

						} else {
							obj.put(new String("output"), "");
							obj.put(new String("ReportStatus"), "");
							obj.put(new String("Status"), "failure");

						}
					}

		             else if (page.equalsIgnoreCase("fedashboard"))    {       List<RequestInfoSO>list=new ArrayList<RequestInfoSO>();
		                        for(int i=0;i<detailsList.size();i++)
		                        {
		                             if(detailsList.get(i).getRequest_assigned_to().equalsIgnoreCase("feuser"))
		                              {
		                                    list.add(detailsList.get(i));
		                              }
		                           //   list.add(detailsList.get(i));   
		                        }
		                        detailsList.clear();
		                        detailsList.addAll(list);
		                        list=null;
		                        for (int i = 0; i < detailsList.size(); i++) {
		                              comapreObj = new RequestInfoSO();
		                              comapreObj = detailsList.get(i);
		                              boolean isPresent = false;
		                              if (finalList.size() > 0) {
		                                    for (int j = 0; j < finalList.size(); j++) {
		                                          if (comapreObj
		                                                      .getDisplay_request_id()
		                                                      .equalsIgnoreCase(
		                                                                  finalList
		                                                                              .get(j)
		                                                                              .getDisplay_request_id())) {
		                                                isPresent = true;
		                                                break;
		                                          }
		                                    }
		                                    if (isPresent == false) {
		                                          for (int k = 0; k < detailsList.size(); k++) {
		                                                if (comapreObj
		                                                            .getDisplay_request_id()
		                                                            .equalsIgnoreCase(
		                                                                        detailsList
		                                                                                    .get(k)
		                                                                                    .getDisplay_request_id())) {
		                                                      compareList.add(detailsList.get(k));
		                                                }
		                                          }
		                                          finalList.add(compareList.get(0));
		                                          compareList.clear();
		                                    }
		                              } else {
		                                    for (int k = 0; k < detailsList.size(); k++) {
		                                          if (comapreObj
		                                                      .getDisplay_request_id()
		                                                      .equalsIgnoreCase(
		                                                                  detailsList
		                                                                              .get(k)
		                                                                              .getDisplay_request_id())) {
		                                                compareList.add(detailsList.get(k));
		                                          }
		                                    }
		                                    finalList.add(compareList.get(0));
		                                    compareList.clear();
		                              }
		                        }

		                        if (finalList.size() > 0) {
		                              for (int i = 0; i < reoportflagllist.size(); i++) {
		                                    if (reoportflagllist
		                                                .get(i)
		                                                .getRequestId()
		                                                .equalsIgnoreCase(
		                                                            Integer.toString(finalList.get(
		                                                                        0).getRequest_id()))) {
		                                          selected = new ReoprtFlags();
		                                          selected = reoportflagllist.get(i);
		                                          reoportflagllistforselectedRecord
		                                                      .add(selected);
		                                    }
		                              }
		                        }
		                        jsonArrayReports = new Gson()
		                                    .toJson(reoportflagllistforselectedRecord);
		                        jsonArray = new Gson().toJson(finalList);
		                        obj.put(new String("output"), jsonArray);
		                        obj.put(new String("ReportStatus"), jsonArrayReports);
		}		
					
					else {
						if (page.equalsIgnoreCase("dashboard")) {
							if (detailsList.size() > 0) {
								for (int i = 0; i < reoportflagllist.size(); i++) {
									if (reoportflagllist
											.get(i)
											.getRequestId()
											.equalsIgnoreCase(
													Integer.toString(detailsList
															.get(0)
															.getRequest_id()))) {
										selected = new ReoprtFlags();
										selected = reoportflagllist.get(i);
										reoportflagllistforselectedRecord
												.add(selected);
									}
								}
							}
							jsonArrayReports = new Gson()
									.toJson(reoportflagllistforselectedRecord);
							jsonArray = new Gson().toJson(detailsList);
							obj.put(new String("output"), jsonArray);
							obj.put(new String("ReportStatus"),
									jsonArrayReports);
						} else if (page.equalsIgnoreCase("viewpage")) {

							for (int i = 0; i < detailsList.size(); i++) {
								boolean flag = false;
								if (model.size() > 0) {
									for (int j = 0; j < model.size(); j++) {
										if (model
												.get(j)
												.getRequest_display_id()
												.equalsIgnoreCase(
														detailsList
																.get(i)
																.getDisplay_request_id())) {
											flag = true;
											break;
										}
									}
								}
								if (flag == false) {
									modelObj = new VersioningJSONModel();
									objToAdd = new RequestInfoSO();
									objToAdd = detailsList.get(i);
									modelObj.setRequest_display_id(objToAdd
											.getDisplay_request_id());
									modelObj.setRequest_customer_name(objToAdd
											.getCustomer());
									modelObj.setRequest_device(objToAdd
											.getDeviceType());
									modelObj.setRequest_service(objToAdd
											.getService());
									modelObj.setRequest_site_id(objToAdd
											.getSiteid());
									modelObj.setRequest_model(objToAdd
											.getModel());
									modelObj.setRequest_hostname(objToAdd
											.getHostname());
									modelObj.setRequest_id(Integer
											.toString(objToAdd.getRequest_id()));
									List<RequestInfoSO> listToAdd = new ArrayList<RequestInfoSO>();
									for (int k = 0; k < detailsList.size(); k++) {
										if (objToAdd
												.getDisplay_request_id()
												.equalsIgnoreCase(
														detailsList
																.get(k)
																.getDisplay_request_id())) {
											listToAdd.add(detailsList.get(k));
										}
									}
									modelObj.setListOfRequests(listToAdd);
									model.add(modelObj);
								}

							}
							if (model.size() > 0) {
								for (int i = 0; i < reoportflagllist.size(); i++) {
									if (reoportflagllist
											.get(i)
											.getRequestId()
											.equalsIgnoreCase(
													model.get(0)
															.getRequest_id())) {
										selected = new ReoprtFlags();
										selected = reoportflagllist.get(i);
										reoportflagllistforselectedRecord
												.add(selected);
									}
								}
							}
							jsonArrayReports = new Gson()
									.toJson(reoportflagllistforselectedRecord);
							jsonArray = new Gson().toJson(model);
							obj.put(new String("output"), jsonArray);
							obj.put(new String("ReportStatus"),
									jsonArrayReports);
							obj.put(new String("Status"), "success");

						}
					}
				} catch (Exception e) {
					System.out.println(e);
				}
			} else {
				try {
					if (page.equalsIgnoreCase("dashboard")) {
						if (detailsList.size() > 0) {
							for (int i = 0; i < reoportflagllist.size(); i++) {
								if (reoportflagllist
										.get(i)
										.getRequestId()
										.equalsIgnoreCase(
												Integer.toString(detailsList
														.get(0)
														.getRequest_id()))) {
									selected = new ReoprtFlags();
									selected = reoportflagllist.get(i);
									reoportflagllistforselectedRecord
											.add(selected);
								}
							}
						}
						jsonArrayReports = new Gson()
								.toJson(reoportflagllistforselectedRecord);
						jsonArray = new Gson().toJson(detailsList);
						obj.put(new String("output"), jsonArray);
						obj.put(new String("ReportStatus"),
								jsonArrayReports);
					} else if (page.equalsIgnoreCase("viewpage")) {

						for (int i = 0; i < detailsList.size(); i++) {
							boolean flag = false;
							if (model.size() > 0) {
								for (int j = 0; j < model.size(); j++) {
									if (model
											.get(j)
											.getRequest_display_id()
											.equalsIgnoreCase(
													detailsList
															.get(i)
															.getDisplay_request_id())) {
										flag = true;
										break;
									}
								}
							}
							if (flag == false) {
								modelObj = new VersioningJSONModel();
								objToAdd = new RequestInfoSO();
								objToAdd = detailsList.get(i);
								modelObj.setRequest_display_id(objToAdd
										.getDisplay_request_id());
								modelObj.setRequest_customer_name(objToAdd
										.getCustomer());
								modelObj.setRequest_device(objToAdd
										.getDeviceType());
								modelObj.setRequest_service(objToAdd
										.getService());
								modelObj.setRequest_site_id(objToAdd
										.getSiteid());
								modelObj.setRequest_model(objToAdd
										.getModel());
								modelObj.setRequest_hostname(objToAdd
										.getHostname());
								modelObj.setRequest_id(Integer
										.toString(objToAdd.getRequest_id()));
								List<RequestInfoSO> listToAdd = new ArrayList<RequestInfoSO>();
								for (int k = 0; k < detailsList.size(); k++) {
									if (objToAdd
											.getDisplay_request_id()
											.equalsIgnoreCase(
													detailsList
															.get(k)
															.getDisplay_request_id())) {
										listToAdd.add(detailsList.get(k));
									}
								}
								modelObj.setListOfRequests(listToAdd);
								model.add(modelObj);
							}

						}
						if (model.size() > 0) {
							for (int i = 0; i < reoportflagllist.size(); i++) {
								if (reoportflagllist
										.get(i)
										.getRequestId()
										.equalsIgnoreCase(
												model.get(0)
														.getRequest_id())) {
									selected = new ReoprtFlags();
									selected = reoportflagllist.get(i);
									reoportflagllistforselectedRecord
											.add(selected);
								}
							}
						}
						jsonArrayReports = new Gson()
								.toJson(reoportflagllistforselectedRecord);
						jsonArray = new Gson().toJson(model);
						obj.put(new String("output"), jsonArray);
						obj.put(new String("ReportStatus"),
								jsonArrayReports);
						obj.put(new String("Status"), "success");

					}
				} catch (Exception e) {
					System.out.print(e);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
