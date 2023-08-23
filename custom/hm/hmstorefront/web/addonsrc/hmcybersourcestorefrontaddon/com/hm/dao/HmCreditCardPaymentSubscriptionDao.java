/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.dao;

import de.hybris.platform.acceleratorservices.model.payment.CCPaySubValidationModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

/**
 * @author Sagar.patil
 *
 */

public interface HmCreditCardPaymentSubscriptionDao extends Dao
{
	CCPaySubValidationModel findSubscriptionValidationBySubscription(String subscriptionId);
}