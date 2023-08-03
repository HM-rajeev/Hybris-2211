/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.controllers;

/**
 */
public interface HmcybersourcestorefrontaddonControllerConstants
{
	String ADDON_PREFIX = "addon:/hmcybersourcestorefrontaddon/";
	String CART_PREFIX = "/cart";

	/**
	 * Class with view name constants
	 */
	interface Views
	{
		interface Pages
		{
			interface MultiStepCheckout
			{
				String CheckoutSummaryPage = ADDON_PREFIX + "pages/checkout/multi/checkoutSummaryPage";

				String HmAddPaymentMethodPage = ADDON_PREFIX + "pages/checkout/multi/addPaymentMethodPage";

				String HmSilentOrderPostPage = ADDON_PREFIX + "pages/checkout/multi/hmSilentOrderPostPage";

				String HmSilentOrderRedirectPostPage = ADDON_PREFIX + "pages/checkout/multi/hmRedirectPost";

				String HmBillingAddressForm = ADDON_PREFIX + "fragments/checkout/billingAddressForm";
			}
		}
	}
}