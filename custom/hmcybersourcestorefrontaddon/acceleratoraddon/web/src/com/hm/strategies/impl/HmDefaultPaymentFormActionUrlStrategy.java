/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.strategies.impl;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.payment.constants.PaymentConstants;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.site.BaseSiteService;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.util.UriComponentsBuilder;

import com.hm.strategies.HmPaymentFormActionUrlStrategy;


/**
 * @author Sagar.patil
 *
 */

public class HmDefaultPaymentFormActionUrlStrategy implements HmPaymentFormActionUrlStrategy
{
	private BaseSiteService baseSiteService;
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
	private SiteConfigService siteConfigService;

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected SiteBaseUrlResolutionService getSiteBaseUrlResolutionService()
	{
		return siteBaseUrlResolutionService;
	}

	@Required
	public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService)
	{
		this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
	}

	protected SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	@Required
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}

	@Override
	public String getSopRequestUrl(final String clientRef)
	{
		final String urlStr = getSiteConfigService().getProperty(PaymentConstants.PaymentProperties.SOP_POST_URL);
		if (StringUtils.isNotEmpty(urlStr))
		{
			return String.valueOf(getAdjustRequestURI(urlStr));
		}
		return null;
	}

	protected URI getAdjustRequestURI(final String urlStr)
	{
		if (urlStr.charAt(0) == '/')
		{
			// Relative path
			final String siteBaseUrl = getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSiteService().getCurrentBaseSite(), true, "/");

			// Push the site relative path into the URL
		   //return UriComponentsBuilder.fromHttpUrl(siteBaseUrl).replacePath(urlStr).build().toUri();
			String url = siteBaseUrl + "" + urlStr;
			return UriComponentsBuilder.fromHttpUrl(url).build().toUri();
		}
		return URI.create(urlStr);
	}	
}