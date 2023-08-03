/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.strategies;

import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;

/**
 * @author Sagar.patil
 *
 */

public interface HmCreateSubscriptionRequestStrategy
{
	CreateSubscriptionRequest createSubscriptionRequest(final String siteName, final String requestUrl, final String responseUrl,
			final String merchantCallbackUrl, final CustomerModel customerModel, final CreditCardPaymentInfoModel cardInfo,
			final AddressModel paymentAddress);
}