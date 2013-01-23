package integration.funtoo;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.nvarghese.funtoo.FuntooServer;
import com.nvarghese.funtoo.FuntooSettings;

public class FuntooServerLauncher {

	private FuntooServer server;

	static Logger logger = LoggerFactory.getLogger(FuntooServerLauncher.class);

	@BeforeTest(groups = "Funtoo_integration_test")
	public void startServer() throws Exception {

		PropertyConfigurator.configure("log4j.properties");
		logger.info("Ratify Server initializing...");
		FuntooSettings settings = new FuntooSettings("funtoo.conf");
		server = FuntooServer.initializeServer(settings);
		server.startServer(false);

	}

	@AfterTest(groups = "Funtoo_integration_test")
	public void stopServer() {

		logger.info("Shutting down server");
		server.shutdown();
		logger.info("Removing funtooDB database");
		removeDirectory(new File("funtooDB"));

	}

	public boolean removeDirectory(File directory) {

		// System.out.println("removeDirectory " + directory);

		if (directory == null)
			return false;
		if (!directory.exists())
			return true;
		if (!directory.isDirectory())
			return false;

		String[] list = directory.list();

		// Some JVMs return null for File.list() when the
		// directory is empty.
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);

				// System.out.println("\tremoving entry " + entry);

				if (entry.isDirectory()) {
					if (!removeDirectory(entry))
						return false;
				} else {
					if (!entry.delete())
						return false;
				}
			}
		}

		return directory.delete();
	}

}
