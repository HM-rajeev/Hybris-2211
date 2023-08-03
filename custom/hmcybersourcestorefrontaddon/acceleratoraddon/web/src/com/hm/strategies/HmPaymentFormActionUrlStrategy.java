/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.strategies;


/**
 * @author Sagar.patil
 *
 */

public interface HmPaymentFormActionUrlStrategy
{
	String getSopRequestUrl(String clientRef);
}