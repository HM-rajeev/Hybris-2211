/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.service.impl;

import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.hm.service.HmCommerceCheckoutService;
import com.hm.strategies.HmCommercePaymentAuthorizationStrategy;
import com.hm.strategies.HmCommercePaymentProviderStrategy;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * @author Sagar.patil
 *
 */

public class HmDefaultCommerceCheckoutService implements HmCommerceCheckoutService
{
	private HmCommercePaymentProviderStrategy hmCommercePaymentProviderStrategy;
	private HmCommercePaymentAuthorizationStrategy hmCommercePaymentAuthorizationStrategy;

	@Override
	public PaymentTransactionEntryModel authorizePayment(final CommerceCheckoutParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();
		validateParameterNotNull(cartModel, "Cart model cannot be null");
		validateParameterNotNull(cartModel.getPaymentInfo(), "Payment information on cart cannot be null");

		// if the authorization amount is not passed in figure it out from the cart.
		if (parameter.getAuthorizationAmount() == null)
		{
			parameter.setAuthorizationAmount(calculateAuthAmount(cartModel));
		}		
		return getHmCommercePaymentAuthorizationStrategy().authorizePaymentAmount(parameter);
	}

	/**
	 * Calculate authorization amount.
	 *
	 * @param cartModel
	 *           the cart model
	 * @return the big decimal authorization amount
	 */
	protected BigDecimal calculateAuthAmount(final CartModel cartModel)
	{
		final Double totalPrice = cartModel.getTotalPrice();
		final Double totalTax = (cartModel.getNet().booleanValue() && cartModel.getStore() != null
				&& cartModel.getStore().getExternalTaxEnabled().booleanValue()) ? cartModel.getTotalTax() : Double.valueOf(0d);
		final BigDecimal totalPriceWithoutTaxBD = BigDecimal.valueOf(totalPrice == null ? 0d : totalPrice.doubleValue()).setScale(2,
				RoundingMode.HALF_EVEN);
		final BigDecimal totalPriceBD = BigDecimal.valueOf(totalTax == null ? 0d : totalTax.doubleValue())
				.setScale(2, RoundingMode.HALF_EVEN).add(totalPriceWithoutTaxBD);
		return totalPriceBD;
	}

	public HmCommercePaymentAuthorizationStrategy getHmCommercePaymentAuthorizationStrategy()
	{
		return hmCommercePaymentAuthorizationStrategy;
	}

	public void setHmCommercePaymentAuthorizationStrategy(
			HmCommercePaymentAuthorizationStrategy hmCommercePaymentAuthorizationStrategy)
	{
		this.hmCommercePaymentAuthorizationStrategy = hmCommercePaymentAuthorizationStrategy;
	}

	@Override
	public String getPaymentProvider()
	{
		return getHmCommercePaymentProviderStrategy().getPaymentProvider();
	}

	public HmCommercePaymentProviderStrategy getHmCommercePaymentProviderStrategy()
	{
		return hmCommercePaymentProviderStrategy;
	}

	public void setHmCommercePaymentProviderStrategy(HmCommercePaymentProviderStrategy hmCommercePaymentProviderStrategy)
	{
		this.hmCommercePaymentProviderStrategy = hmCommercePaymentProviderStrategy;
	}
}