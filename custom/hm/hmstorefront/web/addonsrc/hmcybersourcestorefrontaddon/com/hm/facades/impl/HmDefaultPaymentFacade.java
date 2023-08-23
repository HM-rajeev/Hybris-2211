/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.facades.impl;

import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.hm.facades.HmPaymentFacade;
import com.hm.service.HmCustomerAccountService;
import com.hm.service.HmPaymentService;

/**
 * @author Sagar.patil
 *
 */

public class HmDefaultPaymentFacade implements HmPaymentFacade
{
	private BaseSiteService baseSiteService;
	private HmPaymentService hmPaymentService;
	private CheckoutCustomerStrategy checkoutCustomerStrategy;
	private HmCustomerAccountService hmCustomerAccountService;
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
	private Converter<PaymentSubscriptionResultItem, PaymentSubscriptionResultData> paymentSubscriptionResultDataConverter;
	
	public PaymentData beginSopCreateSubscription(final String responseUrl, final String merchantCallbackUrl)
	{
		final String fullResponseUrl = getFullResponseUrl(responseUrl, true);
		final String fullMerchantCallbackUrl = getFullResponseUrl(merchantCallbackUrl, true);
		final String siteName = getCurrentSiteName();

		final CustomerModel customerModel = getCurrentUserForCheckout();
		final AddressModel paymentAddress = getDefaultPaymentAddress(customerModel);

		return getHmPaymentService().beginSopCreatePaymentSubscription(siteName, fullResponseUrl, fullMerchantCallbackUrl, customerModel, null, paymentAddress);
	}
	
	@Override
	public PaymentSubscriptionResultData completeSopCreateSubscription(final Map<String, String> parameters,
			final boolean saveInAccount, final boolean defaultPayment)
	{
		final CustomerModel customerModel = getCurrentUserForCheckout();
		final PaymentSubscriptionResultItem paymentSubscriptionResultItem = getHmPaymentService().completeSopCreatePaymentSubscription(customerModel, saveInAccount, parameters);

		if (paymentSubscriptionResultItem != null)
		{
			if (defaultPayment)
			{
				hmCustomerAccountService.setDefaultPaymentInfo(customerModel, paymentSubscriptionResultItem.getStoredCard());
			}
			return getPaymentSubscriptionResultDataConverter().convert(paymentSubscriptionResultItem);
		}
		return null;
	}
	
	/**
	 * Resolves a given URL to a full URL including server and port, etc.
	 *
	 * @param responseUrl
	 *           - the URL to resolve
	 * @param isSecure
	 *           - flag to indicate whether the final URL should use a secure connection or not.
	 * @return a full URL including HTTP protocol, server, port, path etc.
	 */
	
	protected String getFullResponseUrl(final String responseUrl, final boolean isSecure)
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();

		final String fullResponseUrl = getSiteBaseUrlResolutionService().getWebsiteUrlForSite(currentBaseSite, isSecure, responseUrl);

		return fullResponseUrl == null ? "" : fullResponseUrl;
	}
	
	protected AddressModel getDefaultPaymentAddress(final CustomerModel customerModel)
	{
		return customerModel.getDefaultPaymentAddress();
	}
	
	/**
	 * @return the name of the current base site.
	 */
	protected String getCurrentSiteName()
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();

		return currentBaseSite == null ? "" : currentBaseSite.getUid();
	}
	
	protected CustomerModel getCurrentUserForCheckout()
	{
		return getCheckoutCustomerStrategy().getCurrentUserForCheckout();
	}
	
	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}	
	
	/**
	 * @return the hmPaymentService
	 */
	public HmPaymentService getHmPaymentService()
	{
		return hmPaymentService;
	}

	/**
	 * @param hmPaymentService the hmPaymentService to set
	 */
	public void setHmPaymentService(final HmPaymentService hmPaymentService)
	{
		this.hmPaymentService = hmPaymentService;
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
	
	protected CheckoutCustomerStrategy getCheckoutCustomerStrategy()
	{
		return checkoutCustomerStrategy;
	}

	@Required
	public void setCheckoutCustomerStrategy(final CheckoutCustomerStrategy checkoutCustomerStrategy)
	{
		this.checkoutCustomerStrategy = checkoutCustomerStrategy;
	}
	
	public HmCustomerAccountService getHmCustomerAccountService()
	{
		return hmCustomerAccountService;
	}
	
	public void setHmCustomerAccountService(final HmCustomerAccountService hmCustomerAccountService)
	{
		this.hmCustomerAccountService = hmCustomerAccountService;
	}	
	
	protected Converter<PaymentSubscriptionResultItem, PaymentSubscriptionResultData> getPaymentSubscriptionResultDataConverter()
	{
		return paymentSubscriptionResultDataConverter;
	}

	@Required
	public void setPaymentSubscriptionResultDataConverter(
			final Converter<PaymentSubscriptionResultItem, PaymentSubscriptionResultData> paymentSubscriptionResultDataConverter)
	{
		this.paymentSubscriptionResultDataConverter = paymentSubscriptionResultDataConverter;
	}
}