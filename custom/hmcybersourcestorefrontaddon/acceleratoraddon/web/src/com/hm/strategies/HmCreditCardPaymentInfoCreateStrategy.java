/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.strategies;

import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionInfoData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;

/**
 * @author Sagar.patil
 *
 */

public interface HmCreditCardPaymentInfoCreateStrategy
{
	CreditCardPaymentInfoModel createCreditCardPaymentInfo(final SubscriptionInfoData subscriptionInfo,
			final PaymentInfoData paymentInfo, final AddressModel billingAddress, final CustomerModel customerModel,
			final boolean saveInAccount);
	
	CreditCardPaymentInfoModel saveSubscription(CustomerModel customerModel, CustomerInfoData customerInfoData,
			SubscriptionInfoData subscriptionInfo, PaymentInfoData paymentInfoData, boolean saveInAccount);
}