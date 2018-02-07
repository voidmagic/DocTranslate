package com.baiyyang.server.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.3-hudson-390-
 * Generated source version: 2.0
 * <p>
 * An example of how this class may be used:
 * 
 * <pre>
 * STMTWebService service = new STMTWebService();
 * STMTWeb portType = service.getSTMTWebPort();
 * portType.getTranslation(...);
 * </pre>
 * 
 * </p>
 * 
 */
@WebServiceClient(name = "STMTWebService", targetNamespace = "http://webservices.mt.nlp.istic.ac.cn/", wsdlLocation = "http://168.160.19.46:8080/stmtservice/STMTWebPort?wsdl")
public class STMTWebService extends Service {

	private final static URL STMTWEBSERVICE_WSDL_LOCATION;
	private final static Logger logger = Logger
			.getLogger(com.baiyyang.server.client.STMTWebService.class
					.getName());

	static {
		URL url = null;
		try {
			URL baseUrl;
			baseUrl = com.baiyyang.server.client.STMTWebService.class
					.getResource(".");
			url = new URL(baseUrl,
					"http://168.160.19.46:8080/stmtservice/STMTWebPort?wsdl");
		} catch (MalformedURLException e) {
			logger.warning("Failed to create URL for the wsdl Location: 'http://168.160.19.46:8080/stmtservice/STMTWebPort?wsdl', retrying as a local file");
			logger.warning(e.getMessage());
		}
		STMTWEBSERVICE_WSDL_LOCATION = url;
	}

	public STMTWebService(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public STMTWebService() {
		super(STMTWEBSERVICE_WSDL_LOCATION, new QName(
				"http://webservices.mt.nlp.istic.ac.cn/", "STMTWebService"));
	}

	/**
	 * 
	 * @return returns STMTWeb
	 */
	@WebEndpoint(name = "STMTWebPort")
	public STMTWeb getSTMTWebPort() {
		return super.getPort(new QName(
				"http://webservices.mt.nlp.istic.ac.cn/", "STMTWebPort"),
				STMTWeb.class);
	}

}
