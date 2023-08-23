/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.facades;

import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;

import java.util.Map;

/**
 * @author Sagar.patil
 *
 */

public interface HmPaymentFacade
{
	/**
	 * Gets the Silent Order Post request data which includes all data required to create a subscription (customer
	 * profile).
	 *
	 * @param responseUrl
	 *           - requires a {@link HostedOrderPageRequest} object containing all the request data.
	 * @param merchantCallbackUrl
	 *           - The URL of the MerchantCallbackController used by the Provider to send validation responses.
	 * @return a {@link PaymentData} object which contains all data required to create a subscription.
	 */
	PaymentData beginSopCreateSubscription(String responseUrl, String merchantCallbackUrl);

	/**
	 * Called to create a subscription internally with the result parameters obtained from the provider's Silent Order
	 * Post.
	 *
	 * @param parameters
	 *           - a Map of key-value paired Strings with the result data returned from the HOP/SOP response.
	 * @param saveInAccount
	 *           - a Flag to tell if the PaymentInfo created should saved to the account. During guest checkout the
	 *           Anonymous user should not hav the PaymentInfos assigned
	 * @param defaultPayment
	 *           - a flag to tell whether the created PaymentInfo should be made default or not
	 * @return - a {@link CCPaymentInfoData} object representing the subscription created by the HOP/SOP response.
	 */
	PaymentSubscriptionResultData completeSopCreateSubscription(Map<String, String> parameters, boolean saveInAccount,
			boolean defaultPayment);
}