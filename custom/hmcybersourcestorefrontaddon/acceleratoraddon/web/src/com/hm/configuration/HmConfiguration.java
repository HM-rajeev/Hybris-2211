/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.configuration;

import java.util.Properties;

/**
 * @author Sagar.patil
 *
 */

public class HmConfiguration
{
	public static Properties getMerchantDetails()
	{
		Properties props = new Properties();

		// HTTP_Signature = http_signature and JWT = jwt
		props.setProperty("authenticationType", "http_signature");
		// Default Provide
		//props.setProperty("merchantID", "testrest");

		// Our Account [HappiestMinds]
		props.setProperty("merchantID", "happiestmindstechnologies");

		props.setProperty("runEnvironment", "apitest.cybersource.com");
		props.setProperty("requestJsonPath", "src/main/resources/request.json");

		// MetaKey Parameters
		props.setProperty("portfolioID", "");
		props.setProperty("useMetaKey", "false");

		// JWT Parameters [Not Required Temp..]
		//props.setProperty("keyAlias", "testrest");
		//props.setProperty("keyPass", "testrest");
		//props.setProperty("keyFileName", "testrest");

		// P12 key path. Enter the folder path where the .p12 file is located.

		props.setProperty("keysDirectory", "src/main/resources");

		// Default Provide
		// HTTP Parameters
		//props.setProperty("merchantKeyId", "08c94330-f618-42a3-b09d-e1e43be5efda");
		//props.setProperty("merchantsecretKey", "yBJxy6LjM2TmcPGu+GaJrHtkke25fPpUX+UY6/L/1tE=");

		// Our Account [HappiestMinds]
		props.setProperty("merchantKeyId", "1a31b642-39c0-460b-8d6c-7908c0c58781");
		props.setProperty("merchantsecretKey", "dYEH6b+vBkaOc+SgGguc2kHt1x7yWe+mJ+KsZPyLCB8=");

		// Logging to be enabled or not.
		props.setProperty("enableLog", "true");
		// Log directory Path
		props.setProperty("logDirectory", "log");
		props.setProperty("logFilename", "cybs");

		// Log file size in KB
		props.setProperty("logMaximumSize", "5M");

		// OAuth related properties.
		//props.setProperty("enableClientCert", "false");
		//props.setProperty("clientCertDirectory", "src/main/resources");
		//props.setProperty("clientCertFile", "");
		//props.setProperty("clientCertPassword", "");
		//props.setProperty("clientId", "");
		//props.setProperty("clientSecret", "");

		return props;
	}

	public static Properties getAlternativeMerchantDetails()
	{
		Properties props = new Properties();

		// HTTP_Signature = http_signature and JWT = jwt
		props.setProperty("authenticationType", "http_signature");
		props.setProperty("merchantID", "testrest_cpctv");
		props.setProperty("runEnvironment", "apitest.cybersource.com");
		props.setProperty("requestJsonPath", "src/main/resources/request.json");

		// JWT Parameters
		props.setProperty("keyAlias", "testrest_cpctv");
		props.setProperty("keyPass", "testrest_cpctv");
		props.setProperty("keyFileName", "testrest_cpctv");

		// P12 key path. Enter the folder path where the .p12 file is located.

		props.setProperty("keysDirectory", "src/main/resources");
		// HTTP Parameters
		props.setProperty("merchantKeyId", "e547c3d3-16e4-444c-9313-2a08784b906a");
		props.setProperty("merchantsecretKey", "JXm4dqKYIxWofM1TIbtYY9HuYo7Cg1HPHxn29f6waRo=");
		// Logging to be enabled or not.
		props.setProperty("enableLog", "true");
		// Log directory Path
		props.setProperty("logDirectory", "log");
		props.setProperty("logFilename", "cybs");

		// Log file size in KB
		props.setProperty("logMaximumSize", "5M");

		return props;
	}
}