/**
 * A Wrapper Class for the TSECaseGeneratorTemplate
 *************************************************
 * History
 * -----------------------------------------------
 * 11/03/2014 Islam Ibrahim, Mohamed ELSOBKY
 * Initial revision
 * -----------------------------------------------
 */
package prjUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.soft.tsecg.entities.TSECaseGeneratorTemplate;

public abstract class TestGroupTemplate extends TSECaseGeneratorTemplate {

	/**
	 * When the tester does two successive reactions without pause between them,
	 * a default pause will be added with this default value. This value can be
	 * configured by the tester to add flexibility
	 */
	public int pauseBetweenSuccessiveReactions = 100;
	/**
	 * steps to be added at the start and end of each test group (Test Serie)
	 * generated under this Test Group Generator Class
	 */
	public IInitDeinitSteps testSerie_InitSteps;
	/**
	 * steps to be added at the start and end of each test case (Test sub-Serie)
	 * generated under this Test Group Generator Class
	 */
	public IInitDeinitSteps testSubSerie_InitSteps;
	/**
	 * The maximum time that a test group can take. If the test group exceeds
	 * this maximum time it will be split into multiple test groups each is
	 * around this max value the splitter will not split any test sub-serie the
	 * splitter will add a number at the end of the test group in the format
	 * "_xyz" where xyz is a counter increased each new split test group default
	 * value is 60000 min represents infinite time
	 */
	public int Max_TG_Time_in_mins = 60000;
	/**
	 * public reference to the current test group
	 */
	public TestGroupTemplate currentTestGroup;

	/**
	 * The name of the test group
	 */
	public String testGroupName;
	/**
	 * The tester name
	 */
	public String TGTesterName;
	/**
	 * Test Group description. It is called initial as there may be some
	 * automatic additions to it based on the configurations
	 */
	public String TGInitialDescription = "";
	/**
	 * The base time for the test group default valus is 1000
	 */
	public int TGBaseTime = 1000;

	// private CfgProfiles currentProfile = null;
	private ArrayList<String> MissingEnglish = new ArrayList<String>();
	private ArrayList<String> OtherExceptions = new ArrayList<String>();
	private boolean lastStepWasReaction = false;
	private String[] Library_Path;
	private String[] actionLibrary_Path;
	private String[] expectationLibrary_Path;
	private int TGCounter;

	private int setActionParamCount = 0, setReactionParamCount = 0;

	/**
	 * The template constructor Automatically retrieves the library path from
	 * the properties file (calculates the relative path) Sets the test group
	 * properties
	 */
	public TestGroupTemplate() {
		java.util.Properties prop = new java.util.Properties();
		String libraryPath = "";
		try {
			// load a properties file
			prop.load(new FileInputStream(".\\src\\org\\soft\\tsecg\\ressource\\TSECaseGeneratorRessources.properties"));
			String startLib = new File(prop.getProperty("StartLibrary")).getCanonicalPath();
			String PlanSpec = new File(prop.getProperty("TGroupMainPath")).getCanonicalPath();
			libraryPath = getRelativePath(startLib, PlanSpec);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		Library_Path = new String[] { libraryPath };
		actionLibrary_Path = Library_Path;
		expectationLibrary_Path = Library_Path;
		currentTestGroup = this;
		TGCounter = 1;
	}

	/**
	 * automatically create test serie and initialize the tester name with the
	 * current windows user and the base time with the default base time (1000)
	 */
	public void preAction() {
		String testerName = System.getProperty("user.name");
		createNewTestGroup(testerName, TGInitialDescription, 1000);
	}

	/**
	 * automatically finalize test group generation
	 */
	public void postAction() {
		FinalizeTestGroupGeneration();
	}

	/**
	 * Creates new test group with the same java class name
	 * 
	 * @param testerName
	 *            The tester name
	 * @param _description
	 *            the description of the test group
	 * @param baseTime
	 *            the base time
	 */
	public void createNewTestGroup(String testerName, String _description, int baseTime) {
		createNewTestGroup(getNameOfFile(), testerName, _description, baseTime);
	}

	/**
	 * Creates new test group specifying the TSXML test group family name
	 * 
	 * @param _testGroupName
	 *            test Group Name
	 * @param testerName
	 *            The tester name
	 * @param _description
	 *            the description of the test group
	 * @param baseTime
	 *            the base time
	 */
	public void createNewTestGroup(String _testGroupName, String testerName, String _description, int baseTime) {
		testGroupName = _testGroupName;
		TGTesterName = testerName;
		TGInitialDescription = _description;
		TGBaseTime = baseTime;
		createSubTestGroup(testGroupName, testerName, _description, baseTime);
	}

	/**
	 * Creates new test group specifying the TSXML test group name only for this
	 * sub-test group The main name for the rest of test groups will remain the
	 * same
	 * 
	 * @param _testGroupName
	 *            test Group Name
	 * @param testerName
	 *            The tester name
	 * @param _description
	 *            the description of the test group
	 * @param baseTime
	 *            the base time
	 */
	public void createSubTestGroup(String _testGroupName, String testerName, String _description, int basetime) {
		String description = "";
		// if (currentProfile != null) {
		// description = currentProfile.getDescription();
		// }
		description += _description;
		createSpecsPath(_testGroupName);
		this.createTestGroup("\\" + _testGroupName + "\\" + "CastleScript", "", "", basetime + "", "", Library_Path, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
				actionLibrary_Path, "", "", expectationLibrary_Path, "", this.getClass().getName());

		this.createTestSerie(description, "", "", "1", "", "", description, testerName, "", "", "");
		// if (currentProfile != null) {
		// this.createTestSubSerie();
		// currentProfile.setProfile(this);
		// }
	}

	/**
	 * Finalize the test group generation and display the errors generated while
	 * the whole generation process
	 */
	public void FinalizeTestGroupGeneration() {
		String name = currentTestGroup.getClass().getName();
		boolean englishE = false, otherE = false;
		String overallErr = "";
		if (MissingEnglish.size() == 0) {
			System.out.println("Test Group " + name + " : The Test Case Generation ran successfully with no missing English");
		} else {
			englishE = true;
			String errorString = "Test Group " + name + " : Missing English in the Database as follows:";
			for (String eng : MissingEnglish) {
				errorString += "\r\n" + eng;
			}
			System.err.println(errorString);
			overallErr = errorString;
		}

		if (OtherExceptions.size() == 0) {
			System.out.println("Test Group " + name + " : No Other exception happened");
		} else {
			otherE = true;
			String errorString = "Test Group " + name + " : Other exceptions as follows:";
			for (String eng : OtherExceptions) {
				errorString += "\r\n" + eng;
			}
			System.err.println(errorString);
			overallErr += "\r\n" + errorString;
		}

		if (englishE || otherE)
			JOptionPane.showMessageDialog(null, overallErr, "Generation errors", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * create new action in the tsxml if the action requires parameters, other
	 * calls to setActionParameter API is needed to set the parameters.
	 * 
	 * @param actionText The action English from database
	 */
	public void setAction(String actionText) {
		if (setActionParamCount != 0) {
			String errorMessage = "Test SubSerie:(" + getTestSubSerieNumber() + ") Action:(" + getActionNumber() + "), (" + setActionParamCount + ") parameter(s) is not set";
			System.err.println(errorMessage);
			OtherExceptions.add(errorMessage);
		}
		lastStepWasReaction = false;
		if (getEventBundleByText(actionText) == null)
			EnglishException(actionText);
		ArrayList<String> parameters_match = getParamArray(actionText);
		setActionParamCount = parameters_match.size();
	}

	/**
	 * create new reaction in the tsxml if the action requires parameters, other
	 * calls to setActionParameter API is needed to set the parameters.
	 * 
	 * If the previous step was reaction with no action in between (two successive reactions) 
	 * a Pause will be added so that we don't have multiple reactions in the same action 
	 * @param reactionText The reaction English from database
	 */
	public void setReaction(String reactionText) {
		if (setReactionParamCount != 0) {
			String errorMessage = "Test SubSerie:(" + getTestSubSerieNumber() + ") Reaction:(" + getReactionText() + "), (" + setReactionParamCount + ") parameter(s) is not set";
			System.err.println(errorMessage);
			OtherExceptions.add(errorMessage);
		}
		if (lastStepWasReaction) {
			setActionWithParameters("Pause [*d1] ms", pauseBetweenSuccessiveReactions);
		}
		lastStepWasReaction = true;
		if (setEventBundleByText(reactionText) == null)
			EnglishException(reactionText);
		ArrayList<String> parameters_match = getParamArray(reactionText);
		setReactionParamCount = parameters_match.size();
	}

	/**
	 * set the parameters for the setAction API
	 * the function check that the parameter name and the number of set parameters are matched with the last Action set
	 * This API accepts any data type for parameters (e.g. int, String, float,..,etc)
	 * @param parameter Parameter name
	 * @param value parameter value
	 */
	public void setActionParameter(String parameter, Object value) {
		if (setActionParamCount == 0) {
			String errorMessage = "Test SubSerie:(" + getTestSubSerieNumber() + ") Action:(" + getActionNumber() + "), can't set parameter as all parameters are already set";
			System.err.println(errorMessage);
			OtherExceptions.add(errorMessage);
			return;
		}

		if (setEventByNameForAction(parameter, value.toString()) == null) {
			String errorMessage = "Test SubSerie:(" + getTestSubSerieNumber() + ") Action:(" + getActionNumber() + "), parameter (" + parameter + ") can't be set";
			System.err.println(errorMessage);
			OtherExceptions.add(errorMessage);
		} else {
			setActionParamCount--;
		}
	}

	/**
	 * set the parameters for the setReaction API
	 * the function check that the parameter name and the number of set parameters are matched with the last Reaction set
	 * This API accepts any data type for parameters (e.g. int, String, float,..,etc)
	 * @param parameter Parameter name
	 * @param value parameter value
	 */
	public void setReactionParameter(String parameter, Object value) {
		;
		if (setReactionParamCount == 0) {
			String errorMessage = "Test SubSerie:(" + getTestSubSerieNumber() + ") Action:(" + getReactionText() + "), can't set parameter as all parameters are already set";
			System.err.println(errorMessage);
			OtherExceptions.add(errorMessage);
			return;
		}

		if (setEventByName(parameter, value.toString()) == null) {
			String errorMessage = "Test SubSerie:(" + getTestSubSerieNumber() + ") Action:(" + getReactionText() + "), parameter (" + parameter + ") can't be set";
			System.err.println(errorMessage);
			OtherExceptions.add(errorMessage);
		} else {
			setReactionParamCount--;
		}
	}

	/**
	 * set action English from the database followed by its parameters (comma separated) if required
	 * this API accepts any number of parameters and validates that the passed parameters match the number of parameters required in the English
	 * This API accepts any data type for parameters (e.g. int, String, float,..,etc)
	 * 
	 * example: setActionWithParameters("Pause [*d1] ms", 1000);
	 * @param _EnglishAndParameters the English followed by the parameters comma separated
	 */
	public void setActionWithParameters(Object... _EnglishAndParameters) {
		if (_EnglishAndParameters == null)
			return;
		if (_EnglishAndParameters.length == 0)
			return;
		String[] EnglishAndParameters = new String[_EnglishAndParameters.length];
		for (int i = 0; i < _EnglishAndParameters.length; i++) {
			EnglishAndParameters[i] = _EnglishAndParameters[i].toString();
		}

		String eng = EnglishAndParameters[0];
		ArrayList<String> parameters_match = getParamArray(eng);
		try {
			if (parameters_match.size() != (EnglishAndParameters.length - 1)) {
				throw new Exception("The parameter length stated in the action \"" + eng + "\" doesn't match with the passed parameters length (" + (EnglishAndParameters.length - 1) + ")");
			}
			lastStepWasReaction = false;
			if (getEventBundleByText(eng) == null)
				EnglishException(eng);
			int index = 1;
			for (String param : parameters_match) {
				setEventByNameForAction(param, EnglishAndParameters[index++]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Runtime.getRuntime().exit(1);

		}
	}

	/**
	 * set reaction English from the database followed by its parameters (comma separated) if required
	 * this API accepts any number of parameters and validates that the passed parameters match the number of parameters required in the English
	 * This API accepts any data type for parameters (e.g. int, String, float,..,etc)
	 * 
	 * example: setReactionWithParameters("Check Message xyz [*b1]", "01");
	 * @param _EnglishAndParameters the English followed by the parameters comma separated
	 */
	public void setReactionWithParameters(Object... _EnglishAndParameters) {
		if (_EnglishAndParameters == null)
			return;
		if (_EnglishAndParameters.length == 0)
			return;

		String[] EnglishAndParameters = new String[_EnglishAndParameters.length];
		for (int i = 0; i < _EnglishAndParameters.length; i++) {
			EnglishAndParameters[i] = _EnglishAndParameters[i].toString();
		}

		String eng = EnglishAndParameters[0];
		ArrayList<String> parameters_match = getParamArray(eng);
		try {
			if (parameters_match.size() != (EnglishAndParameters.length - 1)) {
				throw new Exception("The parameter length stated in the reaction \"" + eng + "\" doesn't match with the passed parameters length (" + (EnglishAndParameters.length - 1) + ")");
			}
			if (lastStepWasReaction) {
				setActionWithParameters("Pause [*d1] ms", pauseBetweenSuccessiveReactions);
			}
			lastStepWasReaction = true;
			if (setEventBundleByText(eng) == null)
				EnglishException(eng);
			int index = 1;
			for (String param : parameters_match) {
				setEventByName(param, EnglishAndParameters[index++]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
	}

	/**
	 * this API can be used to write any kind of a test step (Action/Reaction)
	 * you need to specify the step type before the English as follows:
	 * "do," in case of action
	 * "chk," in case of reaction
	 * if you don't specify do or chk the step will be considered as action English
	 * the parameters should follow separated by comma
	 * 
	 *  example:
	 *  action: testStep("do,Pause [*d1] ms", 1000);
	 *  reaction: testStep("chk,Check Message xyz [*b1]", "01");
	 * @param EnglishAndParameters the step type and English followed by the parameters comma separated
	 */
	public void testStep(String... EnglishAndParameters) {
		boolean isChk = false;
		String eng;
		String stepEnglish = EnglishAndParameters[0];
		for (int i = 1; i < EnglishAndParameters.length; i++) {
			stepEnglish += "," + EnglishAndParameters[i];
		}
		Pattern p_chk = Pattern.compile("chk\\s*,(.*)");
		Pattern p_do = Pattern.compile("do\\s*,(.*)");
		Matcher m_chk = p_chk.matcher(stepEnglish);
		Matcher m_do = p_do.matcher(stepEnglish);
		if (m_chk.find()) {
			// the command is check
			isChk = true;
			eng = m_chk.group(1);
		} else if (m_do.find()) {
			// The command is do
			eng = m_do.group(1);
		} else {
			// no do/chk found
			eng = stepEnglish;
		}

		// Split the English from the parameters
		String[] engArray = eng.split(",");
		if (isChk) {
			setReactionWithParameters((Object[]) engArray);
		} else {
			setActionWithParameters((Object[]) engArray);
		}
	}

	/**
	 * Create new test subSerie.
	 * Checking the TG execution time vs the maximum execution time, if it is exceeded, a new test group will be created.
	 * The new test group will have the same name as the original one with a post fix represents a counter for the new test groups
	 */
	@Override
	public void createTestSubSerie() {
		checkTimeWithinLimit();
		super.createTestSubSerie();
	}

	/**
	 * This method creates a new TestSubserie inside of the xmlFile, 
	 * Checking the TG execution time vs the maximum execution time, if it is exceeded, a new test group will be created.
	 * The new test group will have the same name as the original one with a post fix represents a counter for the new test groups,
	 * depending on the boolean Value, that has been introduced. If there is any similar
	 * TestSubSerie, this will be deleted.
	 */
	@Override
	public void createTestSubSerie(boolean canI) {
		checkTimeWithinLimit();
		super.createTestSubSerie(canI);
	}

	private String getNameOfFile() {
		String nameOfFile = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);
		return nameOfFile;
	}

	private void createSpecsPath(String name) {
		java.util.Properties prop = new java.util.Properties();
		String specs = "";
		try {
			// load a properties file
			prop.load(new FileInputStream(".\\src\\org\\soft\\tsecg\\ressource\\TSECaseGeneratorRessources.properties"));
			specs = prop.getProperty("TGroupMainPath");
			String tmp_name = specs + name + ".tsxml";
			File file = new File(tmp_name);
			file.getParentFile().mkdirs();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private ArrayList<String> getParamArray(String eng) {
		Pattern pattern = Pattern.compile("(\\[\\*)(.*?)(\\])");
		Matcher matcher = pattern.matcher(eng);
		ArrayList<String> parameters_match = new ArrayList<String>();
		while (matcher.find()) {
			String temp = matcher.group(2);
			parameters_match.add(temp);
		}
		return parameters_match;
	}

	private void EnglishException(String English) {
		String name = this.getClass().getName();
		String error = name + ": The English (" + English + ") can't be found in the database, Please add it";
		System.err.println(error);
		for (String eng : MissingEnglish) {
			if (eng.equals(English))
				return;
		}
		MissingEnglish.add(English);
	}

	private void checkTimeWithinLimit() {
		int time = this.getMyTime();
		boolean isTimeWithinLimits = (time / 60000 < this.Max_TG_Time_in_mins);
		if (!isTimeWithinLimits) {
			createSubTestGroup(this.testGroupName + "_" + String.format("%02d", TGCounter++), TGTesterName, TGInitialDescription, TGBaseTime);
		}
	}

	/**
	 * Get the relative path from one file to another. If one of the provided
	 * resources does not exist, it is assumed to be a file unless it ends with
	 * '/' or '\'.
	 * 
	 * @param t_argetPath
	 *            targetPath is calculated to this file
	 * @param b_asePath
	 *            basePath is calculated from this file
	 * @return
	 */
	private String getRelativePath(String targetPath, String basePath) {

		String pathSeparator = "\\";
		String[] base = basePath.split(Pattern.quote(pathSeparator));
		String[] target = targetPath.split(Pattern.quote(pathSeparator));

		// First get all the common elements. Store them as a string,
		// and also count how many of them there are.
		StringBuffer common = new StringBuffer();

		int commonIndex = 0;
		while (commonIndex < target.length && commonIndex < base.length && target[commonIndex].equals(base[commonIndex])) {
			common.append(target[commonIndex] + pathSeparator);
			commonIndex++;
		}

		if (commonIndex == 0) {
			// No single common path element. This most
			// likely indicates differing drive letters, like C: and D:.
			// These paths cannot be relativized.
			// return the absolute path
			return targetPath;
		}

		// The number of directories we have to backtrack depends on whether the
		// base is a file or a dir
		// For example, the relative path from
		//
		// /foo/bar/baz/gg/ff to /foo/bar/baz
		//
		// ".." if ff is a file
		// "../.." if ff is a directory
		//
		// The following is a heuristic to figure out if the base refers to a
		// file or dir. It's not perfect, because
		// the resource referred to by this path may not actually exist, but
		// it's the best I can do
		boolean baseIsFile = true;

		File baseResource = new File(basePath);

		if (baseResource.exists()) {
			baseIsFile = baseResource.isFile();

		} else if (basePath.endsWith(pathSeparator)) {
			baseIsFile = false;
		}

		StringBuffer relative = new StringBuffer();

		if (base.length != commonIndex) {
			int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;

			for (int i = 0; i < numDirsUp; i++) {
				relative.append(".." + pathSeparator);
			}
		}
		relative.append(targetPath.substring(common.length()));
		return relative.toString();
	}

	@Override
	public abstract void bodyAction();

}
