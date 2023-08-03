/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.facades;

/**
 * @author Sagar.patil
 *
 */

public interface HmCheckoutFacade
{	
	/**
	 * Authorize payment for the order. The order must have a subscription payment details set on it before the payment
	 * can be authorized.
	 *
	 * @param securityCode
	 *           the 3 or 4 number CV2 or CVV security code
	 * @return true if successful
	 */
	boolean authorizePayment(String securityCode);
}