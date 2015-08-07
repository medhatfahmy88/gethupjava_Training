package src;

import org.soft.tsecg.entities.TSECaseGeneratorTemplate;


public class BasicTest extends TSECaseGeneratorTemplate 
{
	String[] Library = { "..\\..\\Support\\Libraries\\Main_lib.txt" };
	String[] actionLibrary = Library;
	String[] expectationLibrary = Library;
	
	String requirementsFile = "/DOORS_Project_Name/Path/SWReqSpec_Document_Name;";
	
	String nameOfFile = "\\"
						+	this.getClass().getName().substring(
								this.getClass().getName().lastIndexOf(".") + 1)
						+	"\\" 
						+	"CastleScript";
	String Text = "This test group is for testing all supported diagnostic session start using physical addressing\n" +
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
	
	String TestcaseInfo = "Checking the positive and negative response while transition between all supported sessions";

	String DiagSession;
	
	
	public BasicTest() 
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void preAction() 
	{
		this.createTestGroup(nameOfFile, "", "", "1000", "", Library, "", "", "",
				"", "", "", "", "", "", "", "", "", "", "", "", "",
				actionLibrary, "", "", expectationLibrary, "", this.getClass().getName());
		this.createTestSerie(Text, "", "", "1", "", "", TestcaseInfo, "Mohamed El Sobky", "", "", "");
		
		this.updateRequierementKey(requirementsFile + "SwReq_1140");
		this.updateRequierementKey(requirementsFile + "SwReq_1711");
		this.updateRequierementKey(requirementsFile + "SwReq_1547");
	}
	
	public void bodyAction() 
	{
	
		this.createTestSubSerie(true);
		if(this.getEventBundleByText("Terminal 15 ON") == null) return;
		
		for (int i=0;i<4;i++)
		{
			if(this.getEventBundleByText("Start Diagnostic Session [*d1] Default :81, Programming :85, Valeo :86, Extended :C0") == null) return;
			DiagSession = this.getEventByName("d1",true).getEventValueParseString();
			
			if(((DiagSession.equals(this.getEventByName("d1").getSpecificValueParseStringAt(0)))
			  ||(DiagSession.equals(this.getEventByName("d1").getSpecificValueParseStringAt(1)))))
			{		
				if(this.setEventBundleByText("Start Diagnostic session [*d1] : OK") == null) return;
				this.setEventByName("d1", DiagSession);
			}
			else
			{	
					if(this.setEventBundleByText("Negative Response SubFun Not Supported - Invalid Format for service[*d1]")== null) return;
					this.setEventByName("d1", "10");
			}
		}
		
		if(this.getEventBundleByText("Terminal 15 OFF") == null) return;
		
	}
	
	public void postAction() 
	{
		System.out.println("The Test Case Generation ran successfully!");
	}
}
