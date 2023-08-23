/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.dao.impl;

import de.hybris.platform.acceleratorservices.model.payment.CCPaySubValidationModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import com.hm.dao.HmCreditCardPaymentSubscriptionDao;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * @author Sagar.patil
 *
 */

public class HmDefaultCreditCardPaymentSubscriptionDao extends AbstractItemDao implements HmCreditCardPaymentSubscriptionDao
{
	private static final String SUBSCRIPTION_QUERY = "SELECT {p.pk} FROM {CCPaySubValidation as p} "
			+ "WHERE {p.subscriptionId} = ?subscriptionId";

	@Override
	public CCPaySubValidationModel findSubscriptionValidationBySubscription(final String subscriptionId)
	{
		validateParameterNotNull(subscriptionId, "subscriptionId must not be null!");

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(SUBSCRIPTION_QUERY);
		fQuery.addQueryParameter("subscriptionId", subscriptionId);

		final SearchResult<CCPaySubValidationModel> searchResult = getFlexibleSearchService().search(fQuery);
		final List<CCPaySubValidationModel> results = searchResult.getResult();

		if (results != null && results.iterator().hasNext())
		{
			return results.iterator().next();
		}
		return null;
	}
}