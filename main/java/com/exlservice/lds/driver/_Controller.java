package com.exlservice.lds.driver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.exlservice.lds.libraries.ConstVariables;
import com.exlservice.lds.libraries.DynamicQuery;
import com.exlservice.lds.libraries.Messages;
import com.exlservice.lds.libraries.PublicVariables;
import com.exlservice.lds.utillib.CommonUtil;
import com.exlservice.lds.utillib.FwUtil;
import com.exlservice.lds.utillib.XMLUtil;
import com.test.KeywordParameterInfoHashMap;
import com.test.ObjectInfo;
import com.test.ObjectInfoHashMap;

public class Controller {
  public static String strTaskID;
  public static String strKeywordID;
  public static String strSuiteID;
  public static String strScenarioID;
  public static String strTestCaseID;
  public static String strKeywordFunc;
  public static String strActionQClass;
  public static String strDataSetId;
  public static String strStepID;
  public static String taskName;
  public static String testCaseName;
  public static String testCaseNameWithoutSpecialChars;
  public static String scenarioName;
  public static String suiteName;
  public static String strObjectID = "0";
  int reccount = 0;
  public static Object[] paramValues = null;
  public static Class<?>[] paramTypes = null;
  public static DBConnection conn = new DBConnection();
  public static ObjectInfoHashMap oiq;
  public static KeywordParameterInfoHashMap keywordParamInfoHashMap;
  SuiteInfo suiteInfo = new SuiteInfo();
  public static String ITERATION_FOLDER_PATH;
  public static List<Long> stepExecutionTimeList = new ArrayList();
  public static List<Long> taskIterationExecutionTimeList = new ArrayList();
  public static List<Long> tcIterationExecutionTimeList = new ArrayList();
  public static List<Long> tcExecutionTimeList = new ArrayList();
  public SummaryTable summaryTable = new SummaryTable();
  public static TestCaseInfo[] testcases;
  public static int testCaseCounter;
  public static TC testCase;
  Report report;
  public static String strKeywordName;
  public static String objectName;
  public static final Map<String, String> STOREHASHMAP = new HashMap();
  public static Map<String, TaskNameInfo> TASKNAMEPERTC = new HashMap();
  public static String TC_SEQ_NO = null;
  public static String ONERROR_ACTION = "Continue";

  public static Map<String, BranchBlock> TC_BRANCH_MAP = new HashMap();
  public static Map<String, BranchBlock> STEP_BRANCH_MAP = new HashMap();

  private static final Logger logger = Logger.getLogger(Controller.class);

  private static final CIHandler CIH = CIFactory.getInstance(DynamicQuery.CONFIG_CITOOL);

  private boolean init() throws Exception {
    boolean success = true;
    try {
      try {
        logger.trace("Before ObjectInfo hashmap creation from Map.xml");
        oiq = new ObjectInfoHashMap(true);
        logger.trace("After ObjectInfo hashmap creation from Map.xml");
      } catch (IOException e) {
        logger.error("Error while reading ObjectInfo into hashmap");
        success = false;

        DataLogger.showErrorMessage(Messages.getProperty(Messages.MAPXMLNOTFOUND));
        return success;
      }

      logger.trace("Before keywordParamInfoHashMap creation from Map.xml");

      keywordParamInfoHashMap = new KeywordParameterInfoHashMap();

      logger.trace("After keywordParamInfoHashMap creation from Map.xml");

      if (!DynamicQuery.CONFIG_DRYRUN) {
        ITERATION_FOLDER_PATH =
            DataLogger.BUILD_FOLDER_PATH + "\\IterationNumber_" + DynamicQuery.ITERATION_NUMBER;
        logger.trace("report.txt created at |" + ITERATION_FOLDER_PATH + "\\"
            + PublicVariables.REPORTTXT + "|");
      }

      if (DynamicQuery.CONFIG_OFFLINE)
        DataLogger.writePropertyToFile("ITERATION_FOLDER_PATH", ITERATION_FOLDER_PATH);
    } catch (Exception e) {
      String msg = "Exception occurred while initialising the execution:";
      logger.error(msg, e);
      DataLogger.writeToErrorLog(msg + "<br />" + FwUtil.getStackTrace(e));
      DataLogger.writeToDebugLog(msg + "Exception:" + e.getMessage());

      DataLogger.showErrorMessage("Exception in Controller init =" + e.getMessage()
          + ". Please check QualitiaWD.log under <<Qualitia Installation Folder>>/logs.");
    }

    return success;
  }

  public boolean readSourceXMLsIntoMemory() {
    boolean success = true;
    NodeList suiteList = null;
    XMLUtil suiteXmlUtil = null;
    boolean dataFound = false;
    int uid = 1;
    try {
      logger.trace("Before reading Suite.xml to DOM");
      suiteXmlUtil = new XMLUtil(DynamicQuery.CONFIG_XMLPATH + "\\" + "Suite.XML");
      suiteList = suiteXmlUtil.queryXML("//Suite");
      logger.trace("After reading Suite.xml to DOM");
    } catch (IOException e) {
      logger.error("Test Suite xml '" + DynamicQuery.CONFIG_XMLPATH + "\\" + "Suite.XML"
          + "' was not found. Test Execution aborted.", e);

      DataLogger.writeToDebugAndInfoLogs("Test Suite xml '" + DynamicQuery.CONFIG_XMLPATH + "\\"
          + "Suite.XML" + "' was not found. Test Execution aborted.");

      DataLogger.showErrorMessage(Messages.getProperty(Messages.SUITEXMLNOTFOUND));

      success = false;
      return success;
    } catch (Exception e) {
      String errMsg = "Exception while loading Suite.xml from " + DynamicQuery.CONFIG_XMLPATH + "\\"
          + "Suite.XML" + "Test Execution will be aborted.";
      logger.error(errMsg + " Exception Message=" + e.getMessage(), e);
      DataLogger.writeToErrorLog(errMsg + " StackTrace: <br/>" + FwUtil.getStackTrace(e));

      success = false;
      return success;
    }
    try {
      for (int i = 0; i < suiteList.getLength(); i++) {
        logger.trace("Start Suite XML parsing");
        Element suiteEle = (Element) suiteList.item(i);

        this.suiteInfo.setSuiteID(suiteEle.getAttribute("Id"));
        this.suiteInfo.setSuiteName(suiteEle.getAttribute("Name"));
        this.suiteInfo.setScheduleName(suiteEle.getAttribute("ScheduleName"));

        logger.debug("SuiteID=|" + this.suiteInfo.getSuiteID() + "|\t SuiteName=|"
            + this.suiteInfo.getSuiteName() + "|\t ScheduleName=|"
            + this.suiteInfo.getScheduleName() + "|");

        NodeList TCList = suiteXmlUtil.queryXML("//TCs/TC");
        XMLUtil TCXmlUtil = null;
        int TCslength = TCList.getLength();
        if (TCslength == 0) {
          logger.error("No Test case found in Suite.xml");

          DataLogger.showErrorMessage(Messages.getProperty(Messages.NOTESTCASESINSUITEXML)
              .replace("{0}", DynamicQuery.CONFIG_XMLPATH + "\\" + "Suite.XML"));
          success = false;

          return success;
        }

        TestCaseInfo[] testCases = new TestCaseInfo[TCslength];
        this.suiteInfo.setTestCases(testCases);

        for (int j = 0; j < TCslength; j++) {
          Element TCEle = (Element) TCList.item(j);
          testCases[j] = new TestCaseInfo();
          testCases[j].setScenarioID(TCEle.getAttribute("ScenarioId"));
          testCases[j].setScenarioName(TCEle.getAttribute("ScenarioName"));
          testCases[j].setRunID(TCEle.getAttribute("RunId"));
          testCases[j].setSeqID(Integer.parseInt(TCEle.getAttribute("SeqId")));
          testCases[j].setTcID(TCEle.getAttribute("TCId"));

          testCases[j].setManualTCID(TCEle.getAttribute("ManualTCId"));
          testCases[j].setDescription(TCEle.getAttribute("Desc"));

          String tcName = TCEle.getAttribute("TCName");
          testCases[j].setTcName(tcName);

          testCases[j].setValid(0);

          testCases[j].setOnError(TCEle.getAttribute("OnError").trim());

          String TCFileName = TCEle.getTextContent();

          logger.debug("tcId=|" + TCEle.getAttribute("TCId") + "|\ntcName=|" + tcName + "|\nRunId=|"
              + TCEle.getAttribute("RunId") + "|\ntcSeqId=|" + TCEle.getAttribute("SeqId")
              + "|\nTCFileName=|" + TCFileName + "|\nOnError=|" + TCEle.getAttribute("OnError")
              + "|");

          TC_SEQ_NO = TCEle.getAttribute("SeqId");
          NodeList TCNodeList = null;
          try {
            if (TCXmlUtil == null)
              TCXmlUtil = new XMLUtil(DynamicQuery.CONFIG_XMLPATH + "\\" + TCFileName);
            else {
              TCXmlUtil.setDocument(DynamicQuery.CONFIG_XMLPATH + "\\" + TCFileName);
            }
            TCNodeList = TCXmlUtil.queryXML("//TC");

            Element testCaseEle = (Element) TCNodeList.item(0);

            logger.trace("Before readTCWorkflows");
            String[] workflows = readTCWorkflows(TCXmlUtil, testCaseEle.getAttribute("TCName"));
            logger.trace("After readTCWorkflows");

            testCases[j].setWorkflows(workflows);

            logger.trace("Before Filtering Marked TCDataSet");
            String iterationXMLQuery = "//Data/TCDataSet";
            NodeList dataList = TCXmlUtil.queryXML(iterationXMLQuery);
            int datasetLength = dataList.getLength();

            if (datasetLength < 1) {
              continue;
            }

            ArrayList dataSetList = new ArrayList();

            for (int k = 0; k < datasetLength; k++) {
              Element datasetEle = (Element) dataList.item(k);
              String markIteration = datasetEle.getAttribute("Mark");

              if (markIteration.equalsIgnoreCase("true")) {
                dataSetList.add(datasetEle);
              } else {
                if (markIteration.equalsIgnoreCase("false"))
                  continue;
                if ((markIteration.startsWith("{")) && (markIteration.endsWith("}"))) {
                  markIteration = markIteration.replace(markIteration.charAt(0), ' ');
                  markIteration =
                      markIteration.replace(markIteration.charAt(markIteration.length() - 1), ' ');
                  markIteration = markIteration.trim().toUpperCase();

                  if (STOREHASHMAP.containsKey(markIteration))
                    if (((String) STOREHASHMAP.get(markIteration)).equalsIgnoreCase("true"))
                      dataSetList.add(datasetEle);
                    else if (!markIteration.equalsIgnoreCase("false"))
                      DataLogger.writeToDebugAndInfoLogs("The selection flag " + markIteration
                          + " in the datasheet is invalid.Please verify ");
                } else {
                  DataLogger.writeToDebugAndInfoLogs("The selection flag " + markIteration
                      + " in the datasheet is invalid.Please verify ");
                }
              }
            }
            logger.trace("After Filtering Marked TCDataSet");

            TCDatasetInfo[] tcDataSet = new TCDatasetInfo[dataSetList.size()];
            testCases[j].setIteration(tcDataSet);

            logger.trace("Before readTasksIntoHashmap i.e. TaskInfo");
            HashMap tasksStepsHM = readTasksIntoHashmap(TCXmlUtil, tcName);
            logger.trace("After readTasksIntoHashmap  i.e. TaskInfo");

            logger.trace("Before TCDataSet parsing");
            for (int k = 0; k < tcDataSet.length; k++) {
              tcDataSet[k] = new TCDatasetInfo();
              String markIteration = ((Element) dataSetList.get(k)).getAttribute("Mark");
              String TCItNo = ((Element) dataSetList.get(k)).getAttribute("Iteration");

              String TCDatasetTag = ((Element) dataSetList.get(k)).getAttribute("TCDataSetTag");

              tcDataSet[k].setIterationNo(Integer.parseInt(TCItNo));
              tcDataSet[k].setMark(Boolean.parseBoolean(markIteration));
              tcDataSet[k].setTCDatasetTag(TCDatasetTag);

              NodeList tasksList = ((Element) dataSetList.get(k)).getElementsByTagName("Task");
              int noTasks = tasksList.getLength();

              TaskInfo[] tasks = new TaskInfo[noTasks];
              tcDataSet[k].setTasks(tasks);

              logger.debug("Test Case Details... \nTCName=|" + tcName + "|\nNo. Of Tasks |"
                  + Integer.toString(noTasks) + "|");
              logger.trace("Before Data/TCDataSet/Task parsing");
              for (int l = 0; l < noTasks; l++) {
                Element taskEle = (Element) tasksList.item(l);
                tasks[l] = new TaskInfo();
                String taskID = taskEle.getAttribute("ID");
                String taskName = taskEle.getAttribute("Name");
                int taskSeq = Integer.parseInt(taskEle.getAttribute("TaskSeq"));

                tasks[l].setTaskID(taskID);
                tasks[l].setSeqID(taskSeq);
                tasks[l].setTaskName(taskName);

                TaskNameInfo tni =
                    (TaskNameInfo) TASKNAMEPERTC.get(TC_SEQ_NO + "_" + Integer.toString(taskSeq));
                tasks[l].setIsSystemTask(Boolean.toString(tni.getIsSystemTask()));
                tasks[l].setOnError(tni.getOnError());
                tasks[l].setParentId(tni.getParentId());

                logger.debug("taskID=|" + taskID + "|\ntaskName=|" + taskName + "|\ntaskSeq=|"
                    + taskSeq + "|\nIsSystemTask=|" + Boolean.toString(tni.getIsSystemTask())
                    + "|\nParentId=|" + tni.getParentId() + "|\nOnError=|" + tni.getOnError()
                    + "|");

                NodeList tasksDataSetList = taskEle.getElementsByTagName("TaskDataSet");
                int taskIterations = tasksDataSetList.getLength();
                logger.debug("No. Of Task Iteration=" + Integer.toString(taskIterations));

                TaskDatasetInfo[] tasksIteration = new TaskDatasetInfo[taskIterations];
                tasks[l].setTaskDataIterations(tasksIteration);

                HashMap taskSteps = (HashMap) tasksStepsHM.get(taskID);
                int stepsNo = taskSteps.size();
                logger.debug("No. Of Steps=" + Integer.toString(stepsNo));

                for (int t = 0; t < taskIterations; t++) {
                  Element taskDataSetEle = (Element) tasksDataSetList.item(t);
                  int taskDataSetIt = Integer.parseInt(taskDataSetEle.getAttribute("Iteration"));

                  tasksIteration[t] = new TaskDatasetInfo();

                  tasksIteration[t].setIterationNo(taskDataSetIt);
                  tasksIteration[t].setTaskName(taskName);

                  NodeList stepList = taskDataSetEle.getElementsByTagName("Step");
                  int stepListLength = stepList.getLength();

                  StepDataInfo[] steps = new StepDataInfo[stepsNo];
                  tasksIteration[t].setSteps(steps);

                  for (int s = 0; s < stepsNo; s++) {
                    int stepSEQNo = s + 1;

                    steps[s] = new StepDataInfo();

                    HashMap oneStep = (HashMap) taskSteps.get(String.valueOf(stepSEQNo));
                    String sStepID = (String) oneStep.get("StepId");
                    String sObjectId = (String) oneStep.get("ObjectId".toUpperCase());

                    String iKeywordID = (String) oneStep.get("ActionId".toUpperCase());
                    String sActionName = (String) oneStep.get("ActionName".toUpperCase());
                    String QClass = (String) oneStep.get("QClass".toUpperCase());
                    String stepParentId = (String) oneStep.get("ParentId".toUpperCase());

                    steps[s].setobjectName((String) oneStep.get("ObjectName".toUpperCase()));

                    logger.trace("ObjectId=|" + sObjectId + "|\nObjectName=|"
                        + (String) oneStep.get("ObjectName".toUpperCase()) + "|\nActionName=|"
                        + sActionName + "|\nstepSEQNo=|" + Integer.toString(stepSEQNo)
                        + "|\nStepID=|" + sStepID + "|\nStepParentId=|"
                        + (String) oneStep.get("ParentId".toUpperCase()) + "|\nQClass=|" + QClass
                        + "|");

                    steps[s].SetStepID(Integer.parseInt(sStepID));
                    steps[s].setSeqID(stepSEQNo);
                    if (!sObjectId.equals(""))
                      steps[s].setObjectID(sObjectId);
                    else {
                      steps[s].setObjectID("-1");
                    }
                    steps[s].setKeywordID(iKeywordID);

                    steps[s].setKeywordName(sActionName);
                    steps[s].setActionQClass(QClass);
                    steps[s].setUID(uid);
                    steps[s].setParentId(stepParentId);
                    uid++;

                    if (keywordParamInfoHashMap.isKeywordParameterised(iKeywordID)) {
                      for (int d = 0; d < stepListLength; d++) {
                        Element stepdata = (Element) stepList.item(d);
                        int stepIDData = Integer.parseInt(stepdata.getAttribute("StepId"));
                        if (stepIDData == Integer.parseInt(sStepID)) {
                          NodeList paramList = stepdata.getElementsByTagName("Param");
                          int paramListLength = paramList.getLength();
                          String[] parameterNames = new String[paramListLength];
                          String[] parameterValues = new String[paramListLength];
                          steps[s].setParameterNames(parameterNames);
                          steps[s].setParameterValues(parameterValues);

                          for (int m = 0; m < paramListLength; m++) {
                            Element paramEle = (Element) paramList.item(m);
                            parameterNames[m] = paramEle.getAttribute("Name");
                            parameterValues[m] = paramEle.getTextContent();
                          }

                          dataFound = true;
                          break;
                        }
                      }
                    }

                    if (!dataFound)
                      DataLogger.writeToDebugLog("Failed to retrieve the data for TCName:" + tcName
                          + "TCIteration No:" + TCItNo + "TaskName:" + taskName + "TaskIterationNo:"
                          + taskDataSetIt + "StepID:" + sStepID);
                  }
                }
              }
            }
          } catch (IOException e) {
            strTestCaseID = TCEle.getAttribute("TCId");
            testCases[j].setTcID(strTestCaseID);

            testCases[j].setTcName("[TC OUT OF SYNC] : " + tcName);
            testCases[j].setManualTCID(TCEle.getAttribute("ManualTCId"));

            testCases[j].setValid(1);
            logger.error("TestCaseID = |" + strTestCaseID + "|\t having Test Case XML= |"
                + DynamicQuery.CONFIG_XMLPATH + "\\" + TCFileName + "| was not found", e);
            DataLogger.writeToDebugAndInfoLogs("Test Case XML: " + DynamicQuery.CONFIG_XMLPATH
                + "\\" + TCFileName + " was not found");
          } catch (Exception e) {
            logger.error("Exception occurred while reading Test Case xml " + TCFileName, e);
            DataLogger.writeToErrorLog("Exception occurred while reading Test Case xml "
                + TCFileName + " : <br/>" + FwUtil.getStackTrace(e));
          }
        }
      }
    } catch (Throwable t) {
      success = false;
      logger.error("Exception occurred in method readSourceXMLsIntoMemory() of Controller", t);
      DataLogger.writeToErrorLog("Exception occurred while buffering source xml , " + t);
    }
    return success;
  }

  public HashMap<String, HashMap<String, HashMap<String, String>>> readTasksIntoHashmap(
      XMLUtil tcXML, String tcName) {
    String strQuery = "//TaskInfo/Task";
    HashMap tasksHM = null;
    try {
      NodeList tasksList = tcXML.queryXML(strQuery);
      int noOfTask = tasksList.getLength();
      if (noOfTask < 1) {
        logger.warn("No Tasks for Test Case= |" + tcName + "| in execution XML");
        DataLogger.writeToDebugLog("Fail to retrieve the tasks for the testcase '" + tcName
            + "'. Please verify" + " the execution xml of the testcase.");
        return tasksHM;
      }

      tasksHM = new HashMap();
      logger.debug("Building Task to Step HashMap");
      for (int k = 0; k < noOfTask; k++) {
        Element taskEle = (Element) tasksList.item(k);
        String taskID = taskEle.getAttribute("ID").trim();

        NodeList stepList = taskEle.getElementsByTagName("Step");
        int stepListLength = stepList.getLength();
        HashMap step = new HashMap();
        int stepSEQ = 1;
        String branchStepId = "";

        logger.debug("Building Step HashMap");
        for (int l = 0; l < stepListLength; l++) {
          HashMap stepHM = new HashMap();
          Element stepEle = (Element) stepList.item(l);
          String sStepID = stepEle.getAttribute("StepId").trim();
          String sObjectId = stepEle.getAttribute("ObjectId").trim();
          String sObjectName = stepEle.getAttribute("ObjectName").trim();
          String sKeywordID = stepEle.getAttribute("ActionId").trim();
          String sActionName = stepEle.getAttribute("ActionName").trim();
          String QClass = stepEle.getAttribute("QClass").trim();
          String parentId = stepEle.getAttribute("ParentID").trim();

          stepHM.put("StepId", sStepID);
          stepHM.put("ObjectId".toUpperCase(), sObjectId);
          stepHM.put("ObjectName".toUpperCase(), sObjectName);
          stepHM.put("ActionId".toUpperCase(), sKeywordID);
          stepHM.put("ActionName".toUpperCase(), sActionName);
          stepHM.put("QClass".toUpperCase(), QClass);
          stepHM.put("ParentId".toUpperCase(), parentId);

          if (TC_BRANCH_MAP.get(taskID) == null) {
            if ((sActionName.equalsIgnoreCase("IF")) || (sActionName.equalsIgnoreCase("ELSE IF"))
                || (sActionName.equalsIgnoreCase("ELSE"))
                || (sActionName.equalsIgnoreCase("END IF"))) {
              BranchBlock bb = null;

              if (sActionName.equalsIgnoreCase("IF")) {
                bb = new BranchBlock();

                bb.setBranchType(ConstVariables.ConditionType.IF);
                branchStepId = taskID + "_" + sStepID;
                STEP_BRANCH_MAP.put(branchStepId, bb);

                NodeList paramNode = tcXML.queryXML(
                    "//Task[@ID='" + taskID + "']/TaskDataSet[@Iteration='1']/Step[@StepId='"
                        + sStepID + "']/Param[@ParamSeq='1']");

                if (paramNode != null) {
                  Element pEle = (Element) paramNode.item(0);
                  bb.setExpression(pEle.getTextContent());
                }
              } else if (sActionName.equalsIgnoreCase("ELSE IF")) {
                bb = (BranchBlock) STEP_BRANCH_MAP.get(branchStepId);
                bb.setJumpTo(Integer.toString(stepSEQ));

                bb = new BranchBlock();
                bb.setBranchType(ConstVariables.ConditionType.ELSEIf);
                branchStepId = taskID + "_" + sStepID;
                STEP_BRANCH_MAP.put(branchStepId, bb);

                NodeList paramNode = tcXML.queryXML(
                    "//Task[@ID='" + taskID + "']/TaskDataSet[@Iteration='1']/Step[@StepId='"
                        + sStepID + "']/Param[@ParamSeq='1']");

                if (paramNode != null) {
                  Element pEle = (Element) paramNode.item(0);
                  bb.setExpression(pEle.getTextContent());
                }
              } else if (sActionName.equalsIgnoreCase("ELSE")) {
                bb = (BranchBlock) STEP_BRANCH_MAP.get(branchStepId);

                bb.setJumpTo(Integer.toString(stepSEQ));

                bb = new BranchBlock();
                bb.setBranchType(ConstVariables.ConditionType.ELSE);
                branchStepId = taskID + "_" + sStepID;

                STEP_BRANCH_MAP.put(branchStepId, bb);
              } else if (sActionName.equalsIgnoreCase("END IF")) {
                bb = (BranchBlock) STEP_BRANCH_MAP.get(branchStepId);

                bb.setJumpTo(Integer.toString(stepSEQ));

                bb = new BranchBlock();
                bb.setBranchType(ConstVariables.ConditionType.ENDIF);
                branchStepId = taskID + "_" + sStepID;

                STEP_BRANCH_MAP.put(branchStepId, bb);
              }
            }
          }
          step.put(String.valueOf(stepSEQ++), stepHM);
        }
        tasksHM.put(taskID, step);
        logger.debug("TaskID= |" + taskID + "| steps mapped");
      }
    } catch (Exception e) {
      tasksHM = null;
      logger.error("Exception in readTasksIntoHashmap()", e);

      String errMsg =
          "An exception occured while reading the tasks of the testcase  '" + tcName + "'.";
      DataLogger.writeToDebugLog(errMsg + " Exception :" + e);
      DataLogger.writeToErrorLog(errMsg + " StackTrace :<br/>" + FwUtil.getStackTrace(e));

      CIH.logErrorMessage("ERROR", errMsg, e);
    }
    return tasksHM;
  }

  public String[] readTCWorkflows(XMLUtil node, String TCName) {
    String[] workflows = (String[]) null;
    try {
      String workflowQuery = "//WF/TaskName";
      logger.trace("In readTCWorkflows parsing xml for query= |" + workflowQuery + "|");

      NodeList wfNode = node.queryXML(workflowQuery);
      int workflowNo = wfNode.getLength();
      if (workflowNo < 1) {
        logger.warn("No workflows in Execution XML.");
        CIH.logErrorMessage("WARNING",
            "Exception in Test case execution. No workflows in Execution XML. Incorrect XML");
        return workflows;
      }

      Integer[] flowSeq = new Integer[workflowNo];
      for (int i = 0; i < workflowNo; i++) {
        Element task = (Element) wfNode.item(i);
        flowSeq[i] = Integer.valueOf(Integer.parseInt(task.getAttribute("TaskSeq")));
      }
      Integer[] sortFlowSeq = CommonUtil.sortIntArray(flowSeq);
      workflows = new String[workflowNo];

      String branchTaskId = "";

      for (int j = 0; j < sortFlowSeq.length; j++) {
        logger.trace("Before fetching taskNode from root node //WF/TaskName[@TaskSeq");
        NodeList taskNode = node.queryXML("//WF/TaskName[@TaskSeq='" + sortFlowSeq[j] + "']");
        logger.trace("After fetching taskNode from root node ");

        Element taskEle = (Element) taskNode.item(0);

        String wfDesc = node.getAttribute(taskEle, "Desc");
        String wfIsSystemTask = node.getAttribute(taskEle, "IsSystemTask");
        String wfOnError = node.getAttribute(taskEle, "OnError");
        String wfTaskId = node.getAttribute(taskEle, "ID");
        String wfTaskName = taskEle.getTextContent().trim();
        String wfTaskSeq = node.getAttribute(taskEle, "TaskSeq");
        String wfParentId = node.getAttribute(taskEle, "ParentID");

        TaskNameInfo taskNameInfo = new TaskNameInfo();
        taskNameInfo.setDesc(wfDesc);
        taskNameInfo.setIsSystemTask(wfIsSystemTask);
        taskNameInfo.setOnError(wfOnError);
        taskNameInfo.setTaskId(wfTaskId);
        taskNameInfo.setTaskName(wfTaskName);
        taskNameInfo.setTaskSeq(wfTaskSeq);
        taskNameInfo.setParentId(wfParentId);

        if (taskNameInfo.getIsSystemTask()) {
          BranchBlock bb = null;
          if (wfDesc.equalsIgnoreCase("IF")) {
            bb = new BranchBlock();
            bb.setBranchType(ConstVariables.ConditionType.IF);
            branchTaskId = taskNameInfo.getTaskId();
            TC_BRANCH_MAP.put(branchTaskId, bb);

            NodeList paramNode = node.queryXML("//Task[@ID='" + branchTaskId
                + "']/TaskDataSet[@Iteration='1']/Step[@StepId='1']/Param[@ParamSeq='1']");

            if (paramNode != null) {
              Element pEle = (Element) paramNode.item(0);
              bb.setExpression(pEle.getTextContent());
            }
          } else if (wfDesc.equalsIgnoreCase("ELSE IF")) {
            bb = (BranchBlock) TC_BRANCH_MAP.get(branchTaskId);
            bb.setJumpTo(wfTaskSeq);

            bb = new BranchBlock();
            bb.setBranchType(ConstVariables.ConditionType.ELSEIf);
            branchTaskId = taskNameInfo.getTaskId();
            TC_BRANCH_MAP.put(branchTaskId, bb);

            NodeList paramNode = node.queryXML("//Task[@ID='" + branchTaskId
                + "']/TaskDataSet[@Iteration='1']/Step[@StepId='1']/Param[@ParamSeq='1']");

            if (paramNode != null) {
              Element pEle = (Element) paramNode.item(0);
              bb.setExpression(pEle.getTextContent());
            }
          } else if (wfDesc.equalsIgnoreCase("ELSE")) {
            bb = (BranchBlock) TC_BRANCH_MAP.get(branchTaskId);
            bb.setJumpTo(wfTaskSeq);

            bb = new BranchBlock();
            bb.setBranchType(ConstVariables.ConditionType.ELSE);
            branchTaskId = taskNameInfo.getTaskId();
            TC_BRANCH_MAP.put(wfTaskId, bb);
          } else if (wfDesc.equalsIgnoreCase("END IF")) {
            bb = (BranchBlock) TC_BRANCH_MAP.get(branchTaskId);
            bb.setJumpTo(wfTaskSeq);

            bb = new BranchBlock();

            bb.setBranchType(ConstVariables.ConditionType.ENDIF);
            branchTaskId = taskNameInfo.getTaskId();
            TC_BRANCH_MAP.put(wfTaskId, bb);
          }

        }

        workflows[j] = taskNameInfo.getTaskId();

        logger.debug("Adding TaskName node for TaskID= |" + wfTaskId + "|\t TaskName= |"
            + wfTaskName + "| to Hashmap TASKNAMEPERTC");

        TASKNAMEPERTC.put(TC_SEQ_NO + "_" + taskNameInfo.getTaskSeq(), taskNameInfo);
      }
    } catch (Exception e) {
      workflows = (String[]) null;
      logger.error("readTCWorkflows() throws exception while reading the workflow of testcase = |"
          + TCName + "|", e);

      String errMsg =
          "An exception occurred while reading the workflow of testcase = '" + TCName + "'. ";
      DataLogger.writeToDebugLog(errMsg + "Exception :" + e);
      DataLogger.writeToErrorLog(errMsg + "StackTrace :<br/>" + FwUtil.getStackTrace(e));
      CIH.logErrorMessage("ERROR", errMsg, e);
    }
    return workflows;
  }

  public boolean readEnvironmentVariables() {
    try {
      logger.trace("Before Loading and Parsing Environment Variables xml");
      JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] {EnvironmentVariables.class});
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      EnvironmentVariables envVariables =
          (EnvironmentVariables) unmarshaller.unmarshal(new FileInputStream(
              DynamicQuery.CONFIG_XMLPATH + "\\EnvironmentVariables\\EnvironmentVariables.XML"));
      List variables = envVariables.getVariable();

      for (EnvironmentVariables.Variable variable : variables) {
        FwUtil.storeData(variable.getName(), variable.getValue());
      }

      logger.trace("Before Loading and Parsing Environment Variables xml");

      FwUtil.storeData(ConstVariables.CURRENT_STEP_STATUS, "-1");
    } catch (IOException e) {
      String errMsg = "No Environment Variables set for the project. " + DynamicQuery.CONFIG_XMLPATH
          + "\\EnvironmentVariables\\EnvironmentVariables.XML not found";
      logger.error(errMsg, e);
      CIH.logErrorMessage("WARNING", errMsg);
      DataLogger.writeToDebugAndInfoLogs("No Environment Variables set for the project");

      return true;
    } catch (Exception e) {
      logger.error(
          "readEnvironmentVariables() throws exception while reading environment variables from the location "
              + DynamicQuery.CONFIG_XMLPATH + "\\EnvironmentVariables.",
          e);
      String errMsg =
          "Exception occured while reading environment variables. Exception: " + e.getMessage();
      DataLogger.writeToDebugAndInfoLogs(errMsg);
      DataLogger.writeToErrorLog(
          "Exception occured while reading environment variables from the location "
              + DynamicQuery.CONFIG_XMLPATH + "\\EnvironmentVariables . StackTrace:"
              + e.getStackTrace());
      CIH.logErrorMessage("ERROR", errMsg, e);
      return false;
    }

    return true;
  }

  private boolean createReportTemplates() {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] {Report.class});
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty("jaxb.formatted.output", new Boolean(true));

      Report report = Report.createReport();
      report.setProjectName(DynamicQuery.CONFIG_PROJECT);
      if (DynamicQuery.CONFIG_OFFLINE)
        report.setExecutionType("O");
      else {
        report.setExecutionType("E");
      }

      report.setTool("WEBDRIVER");
      report.setSuiteExecutionID(DynamicQuery.suiteExecutionID);

      report.setReleaseNumber(DynamicQuery.CONFIG_RELEASE_NUMBER);
      report.setBuildNumber(DynamicQuery.CONFIG_BUILD_NUMBER);
      report.setHostName(DynamicQuery.GetHostName());
      report.setUserName(DynamicQuery.CONFIG_USERNAME);
      report.setSuiteIterationNumber(DynamicQuery.ITERATION_NUMBER);

      report.setCreateInfoLog(Boolean.parseBoolean(DynamicQuery.CONFIG_INFOLOG));
      report.setCreateDebugLog(Boolean.parseBoolean(DynamicQuery.CONFIG_DEBUGLOG));
      report.setCreateErrorLog(Boolean.parseBoolean(DynamicQuery.CONFIG_ERRORLOG));

      Report.Suite suite = Report.createReportSuite();
      suite.setId(this.suiteInfo.getSuiteID());
      suite.setName(this.suiteInfo.getSuiteName());
      suite.setScheduleName(this.suiteInfo.getScheduleName());
      suite.setStartTime("yyyy-MM-dd 00:00:00.000");
      suite.setEndTime("yyyy-MM-dd 00:00:00.000");
      suite.setExecutionTime("00:00:00.000");

      report.setSuite(suite);

      Report.Suite.TCs testCases = Report.createReportSuiteTCs();

      List testCaseList = testCases.getTC();
      TestCaseInfo[] testCaseInfo = this.suiteInfo.getTestCases();

      for (int i = 0; i < this.suiteInfo.getTestCases().length; i++) {
        Report.Suite.TCs.TC testCase = new Report.Suite.TCs.TC();
        testCase.setScenarioName(testCaseInfo[i].getScenarioName());
        testCase.setScenarioId(testCaseInfo[i].getScenarioID());
        testCase.setTcId(testCaseInfo[i].getTcID());
        testCase.setDesc(testCaseInfo[i].getDescription());
        testCase.setTCName(testCaseInfo[i].getTcName());
        testCase.setRunId(testCaseInfo[i].getRunID());
        testCase.setTCSeqId(testCaseInfo[i].getSeqID());
        testCase.setManualTCId(testCaseInfo[i].getManualTCID());
        testCase.setOnError(testCaseInfo[i].getOnError());
        testCase.setExecutionTime("00:00:00.000");
        testCase.setStartTime("yyyy-MM-dd 00:00:00.000");
        testCase.setEndTime("yyyy-MM-dd 00:00:00.000");
        testCase.setStatus("-1");
        testCase.setReportLink(createReportLink(testCaseInfo[i]));
        testCaseList.add(testCase);

        List tcIterationList = testCase.getTCIteration();
        TCDatasetInfo[] tcDataSetInfo = testCaseInfo[i].getIteration();
        if (tcDataSetInfo != null)
          for (int tcIt = 0; tcIt < tcDataSetInfo.length; tcIt++) {
            Report.Suite.TCs.TC.TCIteration testCaseIteration =
                new Report.Suite.TCs.TC.TCIteration();
            testCaseIteration.setIterationNo(tcDataSetInfo[tcIt].getIterationNo());
            testCaseIteration.setTCDataSetTag(tcDataSetInfo[tcIt].getTCDatasetTag());
            testCaseIteration.setExecutionTime("00:00:00.000");
            testCaseIteration.setStatus("-1");
            tcIterationList.add(testCaseIteration);
          }
        else {
          logger.error("No TC itereation for Test Case: |" + testCase.getTCName()
              + "|. Can be a case of File TC XML not being generated, might be locked by another user during execution.");
        }

        if (DynamicQuery.CONFIG_OFFLINE) {
          continue;
        }
        logger.debug("Qualitia Mode Execution");
        this.summaryTable.insertExecutionSummary_SummaryTable(testCaseInfo[i].getScenarioName(),
            testCaseInfo[i].getScenarioID(), testCaseInfo[i].getTcID(), testCaseInfo[i].getSeqID(),
            testCaseInfo[i].getRunID(), testCaseInfo[i].getManualTCID(),
            testCaseInfo[i].getTcName(), testCaseInfo[i].getDescription(),
            Integer.parseInt(testCase.getStatus()), this.suiteInfo.getSuiteID(),
            this.suiteInfo.getSuiteName());
      }

      suite.setTCs(testCases);

      logger.trace("Before writing SummaryReport.xml at |"
          + DataLogger.LOGFOLDERS_MAP.get("LogPath").toString() + "\\SummaryReport.xml|");
      FileOutputStream fs_SummaryReport = new FileOutputStream(
          DataLogger.LOGFOLDERS_MAP.get("LogPath").toString() + "\\SummaryReport.xml");
      marshaller.marshal(report, fs_SummaryReport);
      fs_SummaryReport.flush();
      fs_SummaryReport.close();
      logger.trace("After writing SummaryReport.xml");

      DataLogger.writeToDebugAndInfoLogs("Created SummaryReport.xml at location "
          + DataLogger.LOGFOLDERS_MAP.get("LogPath").toString());
    } catch (Exception e) {
      logger.error("Exception in method writeReportXML() of Controller", e);

      String errMsg = "Exception while writing Summary Report.";
      DataLogger.writeToDebugAndInfoLogs(errMsg + " Exception: " + e.getMessage());
      DataLogger.writeToErrorLog(errMsg + " StackTrace: <br/>" + FwUtil.getStackTrace(e));

      CIH.logErrorMessage("ERROR", errMsg, e);
    }
    return true;
  }

  private String createReportLink(TestCaseInfo tc) {
    String strLink = "";

    if (tc != null) {
      String fileURL =
          "file:///" + ITERATION_FOLDER_PATH.replaceAll("\\\\", "/") + "/" + "TestCaseReport.html";

      String tcName = CommonUtil.skipFileSpecialChars(tc.getTcName().trim());

      String strQueryString = !tcName.isEmpty() ? tcName + "_" + tc.getRunID() : "";
      strLink = fileURL + "?" + strQueryString;
      strLink = strLink + "#" + DynamicQuery.CONFIG_INFOLOG;
      strLink = strLink + "#" + DynamicQuery.CONFIG_DEBUGLOG;
      strLink = strLink + "#" + DynamicQuery.CONFIG_ERRORLOG;

      logger.trace(strLink);
    }

    return strLink;
  }

  public boolean execute() {
    boolean success = true;
    try {
      logger.trace("Before init()");
      if (!init()) {
        success = false;
        return success;
      }
      logger.trace("After init()");

      logger.trace("Before readEnvironmentVariables()");
      if (!readEnvironmentVariables()) {
        success = false;
        DataLogger
            .writeToErrorLog("Test Execution aborted as environment variables could not be read");
        String errMsg = "Test Execution aborted as environment variables could not be read";
        logger.error(errMsg);
        CIH.logErrorMessage("ERROR", errMsg);
      }
      logger.trace("After readEnvironmentVariables()");

      logger.trace("Before readSourceXMLsIntoMemory()");
      if (!readSourceXMLsIntoMemory()) {
        success = false;
        DataLogger.writeToErrorLog("Test Execution aborted as the execution XML cannot be read");
        String errMsg = "Test Execution aborted as the execution XML cannot be read";
        logger.error(errMsg);
        CIH.logErrorMessage("ERROR", errMsg);
      }

      logger.trace("After readSourceXMLsIntoMemory()");

      suiteName = this.suiteInfo.getSuiteName();

      if ((!DynamicQuery.CONFIG_OFFLINE) && (!DynamicQuery.CONFIG_DRYRUN)) {
        logger.trace("Before updateTestExecutionTable");

        updateTestExecutionTable();
        logger.trace("After updateTestExecutionTable");
      }

      if (!DynamicQuery.CONFIG_DRYRUN) {
        logger.trace("Before writeReportXML()");

        if (!createReportTemplates()) {
          success = false;
          DataLogger
              .writeToErrorLog("Test Execution aborted as summary report.xml could not be created");
          String errMsg = "Test Execution aborted as summary report.xml could not be created";
          logger.error(errMsg);
          CIH.logErrorMessage("ERROR", errMsg);
        }

        logger.trace("After writeReportXML()");
      }

      DataLogger.writeToDebugLog("Execution Started");
      logger.trace("Before Execution Start");

      JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] {TC.class});
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty("jaxb.formatted.output", new Boolean(true));
      testcases = this.suiteInfo.getTestCases();

      int TCCount = testcases.length;

      logger.debug("TC Count=" + Integer.toString(TCCount));

      if (QualitiaSelenium.getWDExecutionInstance().equalsIgnoreCase(PublicVariables.PERSUITE)) {
        QualitiaSelenium.startUp();
        logger.debug("ExecutionMode: = " + PublicVariables.PERSUITE + ". Selenium started");
      }

      CIH.logSuiteStart(suiteName);
      CIH.writeToBuildOutput("Execution starting for Qualitia Test Suite: " + suiteName
          + " containing " + TCCount + " test cases.");
      for (int i = 0; i < TCCount; i++) {
        if (testcases[i].getValid() > 0) {
          continue;
        }
        testCaseCounter = i;
        com.webdriverfw.Libraries.StatusRollUp.stepStatusList = new ArrayList();
        com.webdriverfw.Libraries.StatusRollUp.taskIterationStatusList = new ArrayList();
        com.webdriverfw.Libraries.StatusRollUp.taskStatusList = new ArrayList();
        com.webdriverfw.Libraries.StatusRollUp.tcIterationStatusList = new ArrayList();
        com.webdriverfw.Libraries.StatusRollUp.tcStatusList = new ArrayList();

        logger.debug("Before running QualitiaTestCase.class");

        if (QualitiaSelenium.getWDExecutionInstance().equalsIgnoreCase(PublicVariables.PERSUITE)) {
          QualitiaTestCase qTC = new QualitiaTestCase();
          qTC.executeTestCase();
        } else {
          JUnitCore.runClasses(new Class[] {QualitiaTestCase.class});
        }

        logger.debug("After running QualitiaTestCase.class");

        if ((!DynamicQuery.CONFIG_DRYRUN) && (!DynamicQuery.CONFIG_OFFLINE)) {
          this.summaryTable.updateTestCaseInSummaryTable(testCase.getStartTime(),
              testCase.getEndTime(), testCase.getStatus(), testCase.getRunId());
        }

        if (!DynamicQuery.CONFIG_DRYRUN) {
          DataLogger.writeToReportFile("TC|" + testCase.getRunId() + "|" + testCase.getStartTime()
              + "|" + testCase.getEndTime() + "|" + testCase.getExecutionTime() + "|"
              + testCase.getStatus() + "|");
        }

        logger.trace("Before Writing Test Case Report XML for |" + testCase.getTCName() + "|");
        FileOutputStream fs_TCReport =
            new FileOutputStream(DataLogger.LOGFOLDERS_MAP.get("LogPath").toString() + "\\"
                + testCaseNameWithoutSpecialChars + "_" + testCase.getRunId() + ".xml");
        marshaller.marshal(testCase, fs_TCReport);
        fs_TCReport.flush();
        fs_TCReport.close();

        logger.trace("After Writing Test Case Report XML for |" + testCase.getTCName() + "|");

        DataLogger
            .writeToDebugAndInfoLogs(testCaseNameWithoutSpecialChars + "_" + testCase.getRunId()
                + ".xml at location " + DataLogger.LOGFOLDERS_MAP.get("LogPath").toString());

        if ((!ONERROR_ACTION.equalsIgnoreCase("ExitSuite"))
            || ((!((String) STOREHASHMAP.get(ConstVariables.CURRENT_STEP_STATUS)).equals("1"))
                && (!strKeywordFunc.equalsIgnoreCase("ExitSuite"))))
          continue;
        logger.debug("Exiting Suite as Current Step Status=1 Or ExitSuite Action is used in step");
        break;
      }

      CIH.writeToBuildOutput("Qualitia Test Suite: " + suiteName + " execution completed.");
      CIH.logSuiteEnd(suiteName);

      if (QualitiaSelenium.getWDExecutionInstance().equalsIgnoreCase(PublicVariables.PERSUITE)) {
        QualitiaSelenium.cleanUp();
        logger.debug("ExecutionMode: = " + PublicVariables.PERSUITE + ". Selenium Ended.");
      }

      logger.trace("After Execution End");
    } catch (Exception e) {
      DataLogger.writeToErrorLog(
          "Exception in Execution Controller. StackTrace: <br/>" + FwUtil.getStackTrace(e));
      logger.error("Exception in mehtod execute() of Controller", e);
      CIH.logErrorMessage("ERROR", "Exception in Execution Controller.", e);
      success = false;
    }
    return success;
  }

  public static long getTotalTaskIterationExecutionTime() {
    long totalTime = 0L;
    for (Iterator localIterator = stepExecutionTimeList.iterator(); localIterator.hasNext();) {
      Object time = localIterator.next();
      totalTime += ((Long) time).longValue();
    }
    stepExecutionTimeList = new ArrayList();
    return totalTime;
  }

  public static void updateResultsTable(int finalres, String startTime, String endTime, int uid) {
    logger.debug("finalres=|" + finalres + "|\nstartTime=|" + startTime + "|\nendTime=|" + endTime
        + "|\nUId=|" + Integer.toString(uid) + "|");
    String qry =
        "Update results set Status = ?, StartTime = ?, Endtime = ? Where Uid = ? And SuiteExecutionID = ? ";
    logger.trace("updateResultsTable Query=|" + qry + "|");

    startTime = CommonUtil.getMYSQLFormatTime(startTime);
    logger.trace("MySQLFormat StartTime=|" + startTime + "|");
    endTime = CommonUtil.getMYSQLFormatTime(endTime);
    logger.trace("MySQLFormat End Time=|" + endTime + "|");
    conn.updateResultsTable(qry, finalres, startTime, endTime, uid);
  }

  private void updateTestExecutionTable() {
    String qry =
        "insert into test_execution(SuiteExecutionID, ReleaseNumber, BuildNumber, Host, UserID, IterationNumber, SuiteName, ScheduleName) values ('"
            + DynamicQuery.suiteExecutionID + "', '" + DynamicQuery.CONFIG_RELEASE_NUMBER + "', '"
            + DynamicQuery.CONFIG_BUILD_NUMBER + "', '" + DynamicQuery.GetHostName() + "', '"
            + DynamicQuery.CONFIG_USERNAME + "', '" + DynamicQuery.ITERATION_NUMBER + "', '"
            + FwUtil.dbformat(suiteName) + "', '" + this.suiteInfo.getScheduleName() + "')";
    logger.trace("Update Query::" + qry);

    logger.trace("Before Update Query");
    conn.executeResultDbUpdate(qry);
    logger.trace("After Update Query");
  }

  public static Class<?>[] getArgumentList(ObjectInfo objInfo, int argSize) {
    paramTypes = null;
    int isize = 0;
    if ((objInfo != null) && (!objInfo.getObjectID().trim().isEmpty())) {
      paramTypes = new Class[argSize + 1];
      paramTypes[isize] = ObjectInfo.class;
      isize++;
    } else {
      paramTypes = new Class[argSize];
      isize = 0;
    }

    for (int j = 0; j < argSize; isize++) {
      String datatype = keywordParamInfoHashMap.getParameterDataType(strKeywordID + "_" + (j + 1));
      if (datatype.equalsIgnoreCase("array"))
        paramTypes[isize] = Object.class;
      else
        paramTypes[isize] = String.class;
      j++;
    }

    return paramTypes;
  }

  public static int invokeKeyword(String strClass, String strFunc, ObjectInfo objInfo1,
      String[] parameterValues) {
    int result = 1;

    if (objInfo1 != null) {
      logger.trace("strClass=|" + strClass + "|\nstrFunc=|" + strFunc + "|\nobjInfo1=|"
          + objInfo1.getLocators() + "|\nparameterValues=|" + Arrays.toString(parameterValues)
          + "|");
      if (!FwUtil.resolveObjectProperties(objInfo1)) {
        return result;
      }
    }

    result = getParameterTypesAndValues(objInfo1, parameterValues);

    logger.trace("Before Invoke Keyword Through Reflection");
    if (result == 0)
      result = invokeKeywordThruReflection(strClass, strFunc);
    logger.trace("After Invoke Keyword Through Reflection");
    return result;
  }

  public static int getParameterTypesAndValues(ObjectInfo objObjInfo, String[] parameterValues) {
    int i = 0;
    int intArgumentSize = 0;
    int result = 0;
    try {
      if (parameterValues != null) {
        int paramSize = parameterValues.length;
        intArgumentSize = paramSize;

        if ((objObjInfo != null) && (!objObjInfo.getObjectID().trim().isEmpty())) {
          intArgumentSize++;
        }

        paramValues = new Object[intArgumentSize];

        if ((objObjInfo != null) && (!objObjInfo.getObjectID().trim().isEmpty())) {
          paramValues[i] = objObjInfo;
          i++;
        }

        for (int k = 0; k < paramSize; i++) {
          String strParamValue = null;
          strParamValue = parameterValues[k];
          strParamValue = strParamValue == null ? "" : strParamValue;
          int argSeq = k + 1;
          String dataType =
              keywordParamInfoHashMap.getParameterDataType(strKeywordID + "_" + argSeq);
          if (dataType.equalsIgnoreCase("array")) {
            FwUtil.warningMsg = "";
            List<String> array = FwUtil.resolveDataSeparator(strParamValue, false, false);
            if (array == null) {
              result = 1;
              DataLogger.writeToDebugAndInfoLogs("Action: " + strKeywordName
                  + "   Status: Failed   Message: " + FwUtil.errorMsg + " in test data.");
              return result;
            }
            if (!FwUtil.warningMsg.equalsIgnoreCase(""))
              DataLogger.writeToDebugAndInfoLogs(FwUtil.warningMsg);
            FwUtil.warningMsg = "";
            for (int a = 0; a < array.size(); a++) {
              if (((String) array.get(a)).length() > 0) {
                Object o = FwUtil.resolveSpecialCharactersAndVariables((String) array.get(a));
                String val = null;
                if (o != null) {
                  val = o.toString();
                } else {
                  result = 1;
                  DataLogger.writeToDebugAndInfoLogs("Action: " + strKeywordName
                      + "   Status: Failed   Message: " + FwUtil.errorMsg + " in test data.");
                  return result;
                }

                array.set(a, val);
              }
            }
            if (!FwUtil.warningMsg.equalsIgnoreCase("")) {
              DataLogger.writeToDebugAndInfoLogs(FwUtil.warningMsg);
            }
            paramValues[i] = array;
          } else {
            String val = null;
            FwUtil.warningMsg = "";
            List array = FwUtil.resolveDataSeparator(strParamValue, false, false);
            if (!FwUtil.warningMsg.equals("")) {
              DataLogger.writeToDebugAndInfoLogs(FwUtil.warningMsg);
            }
            if (array == null) {
              result = 1;
              DataLogger.writeToDebugAndInfoLogs("Action: " + strKeywordName
                  + "   Status: Failed   Message: " + FwUtil.errorMsg + " in test data.");
              return result;
            }
            if (array.size() > 1) {
              result = 1;
              DataLogger.writeToDebugAndInfoLogs("Action: " + strKeywordName
                  + "   Status: Failed   Message: Syntax error in test data '" + '~'
                  + "' escape character required before '" + '^' + "'");
              return result;
            }

            FwUtil.warningMsg = "";
            Object o = FwUtil.resolveSpecialCharactersAndVariables((String) array.get(0));
            if (!FwUtil.warningMsg.equals("")) {
              DataLogger.writeToDebugAndInfoLogs(FwUtil.warningMsg);
            }
            if (o != null) {
              val = o.toString();
            } else {
              result = 1;
              DataLogger.writeToDebugAndInfoLogs("Action: " + strKeywordName
                  + "   Status: Failed   Message: " + FwUtil.errorMsg + " in test data.");
              return result;
            }

            paramValues[i] = val;
          }
          k++;
        }

        getArgumentList(objObjInfo, paramSize);
      } else {
        paramTypes = null;
        paramValues = null;
        if ((objObjInfo != null) && (!objObjInfo.getObjectID().trim().isEmpty())) {
          paramValues = new Object[1];
          paramValues[0] = objObjInfo;
          getArgumentList(objObjInfo, 0);
        }
      }
    } catch (Exception e) {
      result = 1;
      String err =
          "Exception occurred in method getParameterTypesAndValues of Class Controller\n" + e;
      logger.error(err, e);
      CIH.logErrorMessage("ERROR", err, e);
      DataLogger.writeToErrorLog(
          "Exception occurred in method getParameterTypesAndValues of Class Controller. StackTrace :<br/>"
              + FwUtil.getStackTrace(e));
    }

    return result;
  }

  public static int invokeKeywordThruReflection(String strClass, String strFunc) {
    int res = 1;
    try {
      if ((strClass != null) && (strClass != "")) {
        Class c = null;
        c = Class.forName(strClass);
        Method m = c.getMethod(strFunc, paramTypes);
        logger.trace("KW Class=|| KW method=|" + strFunc);
        logger.trace("Before Reflection Invoke");
        res = ((Integer) m.invoke(c.newInstance(), paramValues)).intValue();
        logger.trace("After Reflection Invoke");
      } else {
        logger.error("Object class name is null for strKeywordName= |" + strKeywordName + "|");
        DataLogger.writeToErrorLog("Object class name is null");
        DataLogger.writeToInfoLog("Action: " + strKeywordName + "    " + "Status: Failed    "
            + "Message: Object class name is null");
      }
    } catch (LinkageError e) {
      logger.error("Linkage Exception thrown in invokeKeywordThruReflection for strKeywordName= |"
          + strKeywordName + "|", e);
      DataLogger.writeToErrorLog(e.toString());
      String errMsg = "Action: " + strKeywordName + "    " + "Status: Failed    "
          + "Message: An exception occured. Message:" + e.getMessage();
      DataLogger.writeToInfoLog(errMsg);
      CIH.logErrorMessage("ERROR", errMsg, e);
    } catch (ClassNotFoundException e) {
      logger.error(
          "ClassNotFoundException Exception thrown in invokeKeywordThruReflection for strKeywordName= |"
              + strKeywordName + "|",
          e);
      DataLogger.writeToErrorLog(e.toString());
      String errMsg = "Action: " + strKeywordName + "    " + "Status: Failed    "
          + "Message: An exception occured. Message:" + e.getMessage();
      DataLogger.writeToInfoLog(errMsg);
      CIH.logErrorMessage("ERROR", errMsg, e);
    } catch (IllegalAccessException e) {
      logger.error(
          "IllegalAccessException Exception thrown in invokeKeywordThruReflection for strKeywordName= |"
              + strKeywordName + "|",
          e);
      String errMsg = "IllegalAccessException occurred in invokeKeywordThruReflection: Method "
          + strKeywordName + " cannot be accessed" + "\n" + e;
      DataLogger.writeToErrorLog(errMsg);
      CIH.logErrorMessage("ERROR", errMsg, e);
    } catch (InvocationTargetException e) {
      logger.error(
          "InvocationTargetException Exception thrown in invokeKeywordThruReflection for strKeywordName= |"
              + strKeywordName + "|",
          e);
      Throwable t = e.getTargetException();
      if ((t.toString().contains("SeleniumException"))
          && (t.getMessage().contains("Connection refused"))) {
        String expectionMsg =
            "Execution aborted. " + Messages.getProperty(Messages.SELENIUMSERVERNOTFOUND)
                + " , Exception:" + t.getLocalizedMessage();
        logger.fatal(expectionMsg + ". Cannot Continue Execution. System will now Exit");

        CIH.logErrorMessage("ERROR",
            expectionMsg + ". Cannot Continue Execution. System will now Exit", t);
        String msg = "<span style='color:red;' ><strong>" + expectionMsg + "</strong></span>";
        DataLogger.writeToDebugAndErrorLogs(msg + " Hence a System exit is performed.");
        DataLogger.writeToInfoLog(msg);
        System.exit(3);
      }

      String errMsg = "Action: " + strKeywordName + "    " + "Status: Failed    "
          + "Message: An exception occured. Message:" + t.getMessage();
      DataLogger.writeToInfoLog(errMsg);
      DataLogger.writeToDebugAndErrorLogs("Action: " + strKeywordName + "    "
          + "Status: Failed    " + "Message: An exception occured. Target :" + t.toString()
          + "   ,  Message:" + t.getMessage());

      CIH.logErrorMessage("ERROR", errMsg, t);
    } catch (Exception e) {
      logger.error("Generic Exception thrown in invokeKeywordThruReflection for strKeywordName= |"
          + strKeywordName + "|", e);
      String errMsg = "Exception occurred in invokeKeywordThruReflection for keyword "
          + strKeywordName + ":" + FwUtil.getStackTrace(e);
      DataLogger.writeToErrorLog(errMsg);

      CIH.logErrorMessage("ERROR", errMsg, e);
    }
    return res;
  }
}
