package src;

import prjUtils.TestGroupTemplate;


public class BasicTestWithWrapper extends TestGroupTemplate 
{
	private String requirementsFile = "/DOORS_Project_Name/Path/SWReqSpec_Document_Name;";

	private String DiagSession;
	
	public BasicTestWithWrapper() 
	{
		super();
		
		this.TGInitialDescription = "This test group is for testing all supported diagnostic session start using physical addressing\n" +
				"Test Serie ID: StartDiagnosticSessionControl_TS_01\n" +
				"Requirements covered in the following testserie are: SRS_CAM_D_0090, SRS_CAM_D_0100, SRS_CAM_D_0110, SRS_CAM_D_0120, SRS_CAM_D_0920, SRS_CAM_D_0940, SRS_CAM_D_0860, SRS_CAM_D_0870, SRS_CAM_D_0880, SRS_CAM_D_0890    \n" +
				"Here the following use cases are validated:\n" +
				"	•	Open all possible diagnostic session:\n" +
				"		- Default session mode 		0x81\n" +
				"		- Programming session mode	0x85\n" +
				"		- Valeo session mode	0x86\n" +
				"		- Extended session mode		0xC0\n"+
				"	•	Receive a Subfunction not supported negative response when an invaled session ID is sent.\n" +
				"	•	Receive a condition not correct negative response when CarSpeed > 3Km/h.\n";
		
		this.updateRequierementKey(requirementsFile + "SwReq_1140");
		this.updateRequierementKey(requirementsFile + "SwReq_1711");
		this.updateRequierementKey(requirementsFile + "SwReq_1547");
	}
	
	public void bodyAction() 
	{
		this.createTestSubSerie(true);
		
		setAction("Terminal 15 ON");
		
		for (int i=0;i<4;i++)
		{
			setAction("Start Diagnostic Session [*d1] Default :81, Programming :85, Valeo :86, Extended :C0");
			DiagSession = this.getEventByName("d1",true).getEventValueParseString();
			
			if(((DiagSession.equals(this.getEventByName("d1").getSpecificValueParseStringAt(0)))
			  ||(DiagSession.equals(this.getEventByName("d1").getSpecificValueParseStringAt(1)))))
			{		
				setReaction("Start Diagnostic session [*d1] : OK");
				setReactionParameter("d1", DiagSession);
			}
			else
			{	
				setReaction("Negative Response SubFun Not Supported - Invalid Format for service[*d1]");
				setReactionParameter("d1", "10");
			}
		}
		
		setAction("Terminal 15 OFF");
	}
}
