/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.configuration;

import java.util.Properties;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Sagar.patil
 *
 */

public class HmConfiguration
{

	private static ConfigurationService configurationService;

	private static final String CYBERSOURCE_AUTHENTICATION_TYPE = "hm.cybersource.authenticationType";
	private static final String SOP_CYBERSOURCE_MERCHANTID = "sop.cybersource.merchantID";
	private static final String CYBERSOURCE_RUN_ENVIRONMENT = "hm.cybersource.runEnvironment";
	private static final String CYBERSOURCE_REQUEST_JSON_PATH = "hm.cybersource.requestJsonPath";
	private static final String CYBERSOURCE_PORTFOLIO_ID = "hm.cybersource.portfolioID";
	private static final String CYBERSOURCE_USE_META_KEY = "hm.cybersource.useMetaKey";
	private static final String CYBERSOURCE_KEY_DIRECTORY = "hm.cybersource.keysDirectory";
	private static final String SOP_CYBERSOURCE_MERCHANT_KEY_ID = "sop.cybersource.merchantKeyId";
	private static final String SOP_CYBERSOURCE_MERCHANT_SECRET = "sop.cybersource.merchantsecretKey";
	private static final String CYBERSOURCE_ENABLE_LOG = "hm.cybersource.enableLog";
	private static final String CYBERSOURCE_LOG_DIRECTORY = "hm.cybersource.logDirectory";
	private static final String CYBERSOURCE_LOG_FILENAME = "hm.cybersource.logFilename";
	private static final String CYBERSOURCE_LOG_MAXIXMUM_SIZE = "hm.cybersource.logMaximumSize";


	private static final String CYBERSOURCE__ALTERNATE_AUTHENTICATION_TYPE = "hm.cybersource.alternate.authenticationType";
	private static final String SOP__ALTERNATE__CYBERSOURCE_MERCHANTID = "sop.cybersource.alternate.merchantID";
	private static final String CYBERSOURCE__ALTERNATE__RUN_ENVIRONMENT = "hm.cybersource.alternate.runEnvironment";
	private static final String CYBERSOURCE__ALTERNATE__REQUEST_JSON_PATH = "hm.cybersource.alternate.requestJsonPath";
	private static final String CYBERSOURCE__ALTERNATE__KEY_ALIAS = "hm.cybersource.alternate.keyAlias";
	private static final String CYBERSOURCE__ALTERNATE__KEY_PASS = "hm.cybersource.alternate.keyPass";
	private static final String CYBERSOURCE__ALTERNATE__KEY_FILENAME = "hm.cybersource.alternate.keyFileName";
	private static final String CYBERSOURCE__ALTERNATE__KEY_DIRECTORY = "hm.cybersource.alternate.keysDirectory";
	private static final String SOP_CYBERSOURCE__ALTERNATE___MERCHANT_KEY_ID = "sop.cybersource.alternate.merchantKeyId";
	private static final String SOP_CYBERSOURCE__ALTERNATE___MERCHANT_SECRET = "sop.cybersource.alternate.merchantsecretKey";
	private static final String CYBERSOURCE__ALTERNATE___ENABLE_LOG = "hm.cybersource.alternate.enableLog";
	private static final String CYBERSOURCE__ALTERNATE___LOG_DIRECTORY = "hm.cybersource.alternate.logDirectory";
	private static final String CYBERSOURCE__ALTERNATE___LOG_FILENAME = "hm.cybersource.alternate.logFilename";
	private static final String CYBERSOURCE__ALTERNATE___LOG_MAXIXMUM_SIZE = "hm.cybersource.alternate.logMaximumSize";


	public static Properties getMerchantDetails()
	{
		Properties props = new Properties();

		// HTTP_Signature = http_signature and JWT = jwt
		props.setProperty("authenticationType",
				getConfigurationService().getConfiguration().getString(CYBERSOURCE_AUTHENTICATION_TYPE, "http_signature"));
		// Default Provide
		//props.setProperty("merchantID", "testrest");



		// Our Account [HappiestMinds]
		props.setProperty("merchantID",
				getConfigurationService().getConfiguration().getString(SOP_CYBERSOURCE_MERCHANTID, "happiestmindstechnologies"));

		props.setProperty("runEnvironment",
				getConfigurationService().getConfiguration().getString(CYBERSOURCE_RUN_ENVIRONMENT, "apitest.cybersource.com"));

		props.setProperty("requestJsonPath", getConfigurationService().getConfiguration().getString(CYBERSOURCE_REQUEST_JSON_PATH,
				"src/main/resources/request.json"));

		// MetaKey Parameters
		props.setProperty("portfolioID", getConfigurationService().getConfiguration().getString(CYBERSOURCE_PORTFOLIO_ID, ""));
		props.setProperty("useMetaKey", getConfigurationService().getConfiguration().getString(CYBERSOURCE_USE_META_KEY, "false"));

		// JWT Parameters [Not Required Temp..]
		//props.setProperty("keyAlias", "testrest");
		//props.setProperty("keyPass", "testrest");
		//props.setProperty("keyFileName", "testrest");

		// P12 key path. Enter the folder path where the .p12 file is located.

		props.setProperty("keysDirectory",
				getConfigurationService().getConfiguration().getString(CYBERSOURCE_KEY_DIRECTORY, "src/main/resources"));

		// Default Provide
		// HTTP Parameters
		//props.setProperty("merchantKeyId", "08c94330-f618-42a3-b09d-e1e43be5efda");
		//props.setProperty("merchantsecretKey", "yBJxy6LjM2TmcPGu+GaJrHtkke25fPpUX+UY6/L/1tE=");

		// Our Account [HappiestMinds]
		props.setProperty("merchantKeyId", getConfigurationService().getConfiguration().getString(SOP_CYBERSOURCE_MERCHANT_KEY_ID,
				"1a31b642-39c0-460b-8d6c-7908c0c58781"));
		props.setProperty("merchantsecretKey", getConfigurationService().getConfiguration()
				.getString(SOP_CYBERSOURCE_MERCHANT_SECRET, "dYEH6b+vBkaOc+SgGguc2kHt1x7yWe+mJ+KsZPyLCB8="));

		// Logging to be enabled or not.
		props.setProperty("enableLog", getConfigurationService().getConfiguration().getString(CYBERSOURCE_ENABLE_LOG, "true"));
		// Log directory Path
		props.setProperty("logDirectory", getConfigurationService().getConfiguration().getString(CYBERSOURCE_LOG_DIRECTORY, "log"));
		props.setProperty("logFilename", getConfigurationService().getConfiguration().getString(CYBERSOURCE_LOG_FILENAME, "cybs"));

		// Log file size in KB
		props.setProperty("logMaximumSize",
				getConfigurationService().getConfiguration().getString(CYBERSOURCE_LOG_MAXIXMUM_SIZE, "5M"));



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
		props.setProperty("authenticationType",
				getConfigurationService().getConfiguration().getString(CYBERSOURCE__ALTERNATE_AUTHENTICATION_TYPE, "http_signature"));
		props.setProperty("merchantID",
				getConfigurationService().getConfiguration().getString(SOP__ALTERNATE__CYBERSOURCE_MERCHANTID, "testrest_cpctv"));
		props.setProperty("runEnvironment", getConfigurationService().getConfiguration()
				.getString(CYBERSOURCE__ALTERNATE__RUN_ENVIRONMENT, "apitest.cybersource.com"));
		props.setProperty("requestJsonPath", getConfigurationService().getConfiguration()
				.getString(CYBERSOURCE__ALTERNATE__REQUEST_JSON_PATH, "src/main/resources/request.json"));

		// JWT Parameters
		props.setProperty("keyAlias",
				getConfigurationService().getConfiguration().getString(CYBERSOURCE__ALTERNATE__KEY_ALIAS, "testrest_cpctv"));
		props.setProperty("keyPass",
				getConfigurationService().getConfiguration().getString(CYBERSOURCE__ALTERNATE__KEY_PASS, "testrest_cpctv"));
		props.setProperty("keyFileName",
				getConfigurationService().getConfiguration().getString(CYBERSOURCE__ALTERNATE__KEY_FILENAME, "testrest_cpctv"));

		// P12 key path. Enter the folder path where the .p12 file is located.

		props.setProperty("keysDirectory",
				getConfigurationService().getConfiguration().getString(CYBERSOURCE__ALTERNATE__KEY_DIRECTORY, "src/main/resources"));
		// HTTP Parameters
		props.setProperty("merchantKeyId", getConfigurationService().getConfiguration()
				.getString(SOP_CYBERSOURCE__ALTERNATE___MERCHANT_KEY_ID, "e547c3d3-16e4-444c-9313-2a08784b906a"));
		props.setProperty("merchantsecretKey", getConfigurationService().getConfiguration()
				.getString(SOP_CYBERSOURCE__ALTERNATE___MERCHANT_SECRET, "JXm4dqKYIxWofM1TIbtYY9HuYo7Cg1HPHxn29f6waRo="));
		// Logging to be enabled or not.
		props.setProperty("enableLog",
				getConfigurationService().getConfiguration().getString(CYBERSOURCE__ALTERNATE___ENABLE_LOG, "true"));
		// Log directory Path
		props.setProperty("logDirectory",
				getConfigurationService().getConfiguration().getString(CYBERSOURCE__ALTERNATE___LOG_DIRECTORY, "log"));
		props.setProperty("logFilename",
				getConfigurationService().getConfiguration().getString(CYBERSOURCE__ALTERNATE___LOG_FILENAME, "cybs"));

		// Log file size in KB
		props.setProperty("logMaximumSize",
				getConfigurationService().getConfiguration().getString(CYBERSOURCE__ALTERNATE___LOG_MAXIXMUM_SIZE, "5M"));

		return props;
	}


	protected static ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}