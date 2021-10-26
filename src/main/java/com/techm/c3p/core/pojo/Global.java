package com.techm.c3p.core.pojo;

import java.util.ArrayList;
import java.util.List;

import com.techm.c3p.core.models.TemplateCommandJSONModel;
import com.techm.c3p.core.models.TemplateLeftPanelJSONModel;

public class Global {

	public static String templateid;
	public static List<Integer>sequenceList=new ArrayList<Integer>();
	public static List<TemplateCommandJSONModel>globalSessionRightPanel=new ArrayList<TemplateCommandJSONModel>();
	public static List<TemplateLeftPanelJSONModel>globalSessionLeftPanel=new ArrayList<TemplateLeftPanelJSONModel>();
	
	public static List<TemplateCommandJSONModel>globalSessionRightPanelCopy=new ArrayList<TemplateCommandJSONModel>();
	public static List<TemplateLeftPanelJSONModel>globalSessionLeftPanelCopy=new ArrayList<TemplateLeftPanelJSONModel>();
	public static String loggedInUser=null;

}
