package skemetch;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom.JDOMException;

import tr.edu.ku.iui.util.Debugger;

public class Main {
	public static void main(String[] args) throws IOException, JDOMException {
		startLogger();
		new MainFrame();
	}

	private static void startLogger() {
		Debugger.start();
		PropertyConfigurator.configure("logging/UropLoggingConfig.lcf");
		Logger.getLogger("generic").debug(
				"--------------------------------------------------------");
		Logger.getLogger("generic").debug("main: New engine started");
		Debugger.println();
		Debugger.println("Starting Application");
		Debugger.println();
	}
}
