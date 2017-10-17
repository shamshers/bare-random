package com.exlservice.lds.libraries;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.log4j.Logger;
import com.exlservice.lds.wrappers.SetBaseState;


/**
 * @author shamshersingh
 *
 */
public class ConfigFileReader {
  private static final Logger logger = Logger.getLogger(ConfigFileReader.class);

  /**
   * @param configFilePath
   * @param testExecution
   * @return
   */
  public static boolean loadProperties(String configFilePath, boolean testExecution) {
    boolean successStatus = true;
    File fPath = null;
    FileInputStream fis = null;
    DataReader obj = new DataReader();
    try {
      fPath = getValidatedConfigPath(configFilePath);
      if (fPath == null) {
        String errMsg =
            "Properties file " + configFilePath + " not found at the given path. Aborting now...";
        logger.fatal(errMsg);
        return false;
      }
      Properties props = new Properties() {
        private static final long serialVersionUID = 3660056385233100824L;

        public Object put(Object key, Object value) {
          String lowercase = ((String) key).toLowerCase();
          return super.put(lowercase, value);
        }

        public String getProperty(String key) {
          String lowercase = key.toLowerCase();
          return super.getProperty(lowercase);
        }

        public String getProperty(String key, String defaultValue) {
          String lowercase = key.toLowerCase();
          return super.getProperty(lowercase, defaultValue);
        }
      };
      try {
        fis = new FileInputStream(fPath);
        props.load(fis);
      } catch (Exception e) {
        logger.fatal("Cannot load properties file. configFilePath=" + configFilePath
            + ". Please check the path", e);
        releaseFile(fis);
        return false;
      }
      DynamicQuery.CONFIG_PROPERTIES = props;
      DynamicQuery.CONFIG_CONFIGFILEPATH = configFilePath;
      ArrayList<String> missingProperties = new ArrayList<String>();
      logger.trace("List of properties in <<Property Name>>:<<Value>> format");
      String propertyValue;
      for (int i = 0; i < ConstVariables.activeKeys.length; i++) {
        String propertyName = ConstVariables.activeKeys[i].toLowerCase();
        propertyValue = props.getProperty(propertyName);
        logger.trace("<<" + ConstVariables.activeKeys[i] + ">>:<<" + propertyValue + ">>");
        boolean isValid = (propertyValue != null) && (!propertyValue.equals(""));
        if (propertyName.equalsIgnoreCase("MasterFilePath")) {
          if (isValid) {
            DynamicQuery.CONFIG_EXCEL_FILE_PATH = obj.getPath(propertyValue);
            System.out.println("XL file path" + DynamicQuery.CONFIG_EXCEL_FILE_PATH);
          } else
            missingProperties.add("MasterFilePath");
        } else if (propertyName.equalsIgnoreCase("reportPath")) {
          if (isValid) {
            DynamicQuery.CONFIG_REPORTPATH = obj.getPath(propertyValue);
            logger
                .trace("DynamicQuery.CONFIG_REPORTPATH=<<" + DynamicQuery.CONFIG_REPORTPATH + ">>");
          } else {
            missingProperties.add("reportPath");
          }
        } else if (propertyName.equalsIgnoreCase("ReleaseNum")) {
          DynamicQuery.CONFIG_RELEASE_NUMBER = propertyValue;
        } else if (propertyName.equalsIgnoreCase("ProjectName")) {
          DynamicQuery.CONFIG_PROJECT = propertyValue;
        } else if (propertyName.equalsIgnoreCase("BuildNum")) {
          DynamicQuery.CONFIG_BUILD_NUMBER = propertyValue;
        } else if (propertyName.equalsIgnoreCase("PlanName")) {
          DynamicQuery.CONFIG_PLANNAME = propertyValue;
        } else if (propertyName.equalsIgnoreCase("ScreenshotPath")) {
          DynamicQuery.CONFIG_SCREENSHOTPATH = String.valueOf(Boolean.parseBoolean(propertyValue));
        } else if (propertyName.equalsIgnoreCase("JAVASCRIPT_ENABLED")) {
          DynamicQuery.CONFIG_JAVASCRIPT_ENABLED =
              String.valueOf(Boolean.parseBoolean(propertyValue));
        } else if (propertyName.equalsIgnoreCase("PROXY")) {
          DynamicQuery.CONFIG_PROXY = String.valueOf(Boolean.parseBoolean(propertyValue));
        } else if (propertyName.equalsIgnoreCase("ChromeDriverPath")) {
          DynamicQuery.CONFIG_CHROMEPATH = String.valueOf(Boolean.parseBoolean(propertyValue));
        } else if (propertyName.equalsIgnoreCase("IEDriverPath")) {
          DynamicQuery.CONFIG_IEPATH = String.valueOf(Boolean.parseBoolean(propertyValue));
        } else if (propertyName.equalsIgnoreCase("TestURL")) {
          DynamicQuery.CONFIG_TestURL = String.valueOf(Boolean.parseBoolean(propertyValue));
        } else if (propertyName.equalsIgnoreCase("PAGELOAD_PERFORMANCE_BENCHMARK")) {
          DynamicQuery.CONFIG_PAGELOAD_PERFORMANCE_BENCHMARK =
              String.valueOf(Boolean.parseBoolean(propertyValue));
        } else if (propertyName.equalsIgnoreCase("SyncTimeInSeconds")) {
          if (isValid) {
            long d = 0L;
            try {
              d = Long.valueOf(propertyValue).longValue();
            } catch (NumberFormatException e) {
              successStatus = false;
              String errMsg = Messages.getProperty(Messages.CONFIGNUMERICSYNCTIMEINSECONDS)
                  + " Default Sync time (milliseconds) of " + SetBaseState.defaultFindObjectTime
                  + " is assumed.";
              logger.error(errMsg);
              d = SetBaseState.defaultFindObjectTime;
            }
            if (testExecution)
              if (d >= 0L) {
                SetBaseState.maximumFindObjectTime = d;

                SetBaseState.maximumFindObjectTime *= 1000L;
              } else {
                SetBaseState.maximumFindObjectTime = SetBaseState.defaultFindObjectTime;
              }
          } else {
            SetBaseState.maximumFindObjectTime = SetBaseState.defaultFindObjectTime;
            logger.warn("Incorrect value of SyncTimeInSeconds. Default of "
                + SetBaseState.defaultFindObjectTime + " milliseconds is assumed");
          }
        }
      }
      if (missingProperties.size() != 0) {
        StringBuilder logMsg = new StringBuilder();
        logMsg.append(
            "Please verify that the following mandatory fields are set in the config file:\n");
        for (String property : missingProperties) {
          logMsg.append(property).append("\n");
        }
        logger.error(Messages.getProperty(Messages.CONFIGERROR) + logMsg.toString());

        successStatus = false;
      }
    } catch (Exception e) {
      successStatus = false;
      logger.error(Messages.getProperty(Messages.CONFIGERROR), e);
    } finally {
      releaseFile(fis);
    }
    return successStatus;
  }

  private static void releaseFile(FileInputStream fis) {
    if (fis != null)
      try {
        fis.close();
      } catch (Exception e) {
        logger.warn("exception in closing input stream", e);
      }
  }

  private static File getValidatedConfigPath(String configFilePath) {
    File f = new File(configFilePath);
    if (!f.exists()) {
      return null;
    }
    return f;
  }
}
