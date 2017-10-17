import com.webdriverfw.CI.CIFactory;
import com.webdriverfw.CI.CIHandler;
import com.webdriverfw.DBConnection.DBConnection;
import com.webdriverfw.Driver.Controller;
import com.webdriverfw.Libraries.ConfigFileReader;
import com.webdriverfw.Libraries.DataLogger;
import com.webdriverfw.Libraries.DynamicQuery;
import com.webdriverfw.Libraries.Messages;
import java.util.Arrays;
import org.apache.log4j.Logger;

public class callController
{
  private static final Logger logger = Logger.getLogger(callController.class);
  private static CIHandler CIH = null;

  public static void main(String[] args)
  {
    try {
      logger.debug("\n******************************************************************************************************\n**************************** ADAA Webdriver Core Engine Started *********************************\n******************************************************************************************************\n");

      DynamicQuery.CONFIG_CITOOL = "DEFAULT";

      CIH = CIFactory.getInstance(DynamicQuery.CONFIG_CITOOL);

      if (!loadConfig(args));
      while (true) {
     // return;

        CIH = initializeCITool(DynamicQuery.CONFIG_CITOOL);

       // setDBConnectionStrings();

        if (!new DataLogger().initializeDataLogger()) {
          logger.error("exception while initilializing log path. Aborting execution now...");
          continue;
        }

        if (new Controller().execute()) break;
        logger.fatal("cont.execute() returned false. Aborting now... ");
        CIH.logErrorMessage("ERROR", "Fatal exception during execution. Please check Qualitia logs");
      }

      if (!DynamicQuery.CONFIG_OFFLINE) {
        logger.debug("Closing Project and Result DB connection now ...");
        DBConnection.closeProjectDBConnection();
        DBConnection.closeResultDBConnection();
      }
    } catch (Exception e) {
      logger.fatal("Generic Exception thrown in Call Controller. Execution will stop and Exit to UI", e);
      DataLogger.writeToErrorLog(Messages.getProperty(Messages.CALLCONTROLLERERROR) + " : " + e);

      DataLogger.showErrorMessage(Messages.getProperty(Messages.CALLCONTROLLERERROR));
      CIH.logErrorMessage("ERROR", Messages.getProperty(Messages.CALLCONTROLLERERROR), e);
    }
    finally {
      logger.debug("\n******************************************************************************************************\n**************************** Qualitia Webdriver Core Engine Shut Down *********************************\n******************************************************************************************************\n\n\n");
    }
  }

  private static boolean loadConfig(String[] args)
  {
    String configPath = "";
    if ((args != null) && (args.length > 0))
    {
      configPath = args[0];
      if ((configPath == null) || (configPath.isEmpty())) {
        String errMsg = "Config Path is null or empty. Execution cannot continue further and will be aborted.";
        logger.fatal(errMsg);
        CIH.logErrorMessage("ERROR", errMsg);
        return false;
      }
    }

    logger.debug("Argments received from Qualitia UI Total Count = |" + args.length + "|.List of Arguments and Values ... ");
    logger.debug("List of Arguments :: " + Arrays.toString(args));

    if (!ConfigFileReader.loadProperties(configPath, true)) {
      String errMsg = "Error loading properties file. Please check the path. Aborting now...";
      logger.fatal(errMsg);
      return false;
    }

    return true;
  }
/*
  private static void setDBConnectionStrings()
  {
    DBConnection.ProjectDBURL = "jdbc:mysql://" + DBConnection.DBServer + ":" + DBConnection.DBPort + "/" + DynamicQuery.CONFIG_PROJECT + "_projectdb?useUnicode=true&characterEncoding=utf8";
    DBConnection.ResultsURL = "jdbc:mysql://" + DBConnection.DBServer + ":" + DBConnection.DBPort + "/" + DynamicQuery.CONFIG_PROJECT + "_resultdb?useUnicode=true&characterEncoding=utf8";
    DBConnection.DRIVER = "com.mysql.jdbc.Driver";

    if (DynamicQuery.CONFIG_DBTYPE.equalsIgnoreCase("SQLServer"))
    {
      if (DBConnection.WindowsAuth) {
        DBConnection.ProjectDBURL = "jdbc:sqlserver://" + DBConnection.DBServer + ":" + DBConnection.DBPort + ";databaseName=" + DynamicQuery.CONFIG_PROJECT + "_projectdb;integratedSecurity=true;";
        DBConnection.ResultsURL = "jdbc:sqlserver://" + DBConnection.DBServer + ":" + DBConnection.DBPort + ";databaseName=" + DynamicQuery.CONFIG_PROJECT + "_resultdb;integratedSecurity=true;";
      }
      else
      {
        DBConnection.ProjectDBURL = "jdbc:sqlserver://" + DBConnection.DBServer + ":" + DBConnection.DBPort + ";databaseName=" + DynamicQuery.CONFIG_PROJECT + "_projectdb;";
        DBConnection.ResultsURL = "jdbc:sqlserver://" + DBConnection.DBServer + ":" + DBConnection.DBPort + ";databaseName=" + DynamicQuery.CONFIG_PROJECT + "_resultdb;";
      }

      DBConnection.DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }
    logger.debug(DBConnection.ProjectDBURL + "\r\n" + DBConnection.ResultsURL + "\r\n" + DBConnection.DRIVER);
  }
*/
  private static CIHandler initializeCITool(String cONFIG_CITOOL)
  {
    return CIFactory.getInstance(DynamicQuery.CONFIG_CITOOL);
  }
}
