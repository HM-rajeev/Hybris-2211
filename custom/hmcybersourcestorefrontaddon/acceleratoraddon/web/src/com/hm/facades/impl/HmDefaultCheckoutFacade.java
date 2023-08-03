/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.facades.impl;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hm.constants.HmcybersourcestorefrontaddonWebConstants;
import com.hm.facades.HmCheckoutFacade;
import com.hm.service.HmCommerceCheckoutService;

/**
 * @author Sagar.patil
 *
 */

public class HmDefaultCheckoutFacade implements HmCheckoutFacade
{
	private static final Logger LOG = Logger.getLogger(HmDefaultCheckoutFacade.class);

	private CartService cartService;
	private CartFacade cartFacade;
	private CheckoutCustomerStrategy checkoutCustomerStrategy;
	private HmCommerceCheckoutService hmCommerceCheckoutService;

	@Override
	public boolean authorizePayment(final String securityCode)
	{
		final CartModel cartModel = getCart();
		final CreditCardPaymentInfoModel creditCardPaymentInfoModel = cartModel == null ? null : (CreditCardPaymentInfoModel) cartModel.getPaymentInfo();
		if (checkIfCurrentUserIsTheCartUser() && creditCardPaymentInfoModel != null && StringUtils.isNotBlank(creditCardPaymentInfoModel.getSubscriptionId()))
		{
			final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
			parameter.setSecurityCode(securityCode);
			//parameter.setPaymentProvider(getPaymentProvider());
			parameter.setPaymentProvider(HmcybersourcestorefrontaddonWebConstants.PAYMENT_PROVIDER_CYBERSOURCE);
		
			final PaymentTransactionEntryModel paymentTransactionEntryModel = getHmCommerceCheckoutService().authorizePayment(parameter);

			return paymentTransactionEntryModel != null
					   && (TransactionStatus.ACCEPTED.name().equals(paymentTransactionEntryModel.getTransactionStatus())
							|| TransactionStatus.REVIEW.name().equals(paymentTransactionEntryModel.getTransactionStatus()));
		}
		return false;
	}

	protected CartModel getCart()
	{
		return hasCheckoutCart() ? getCartService().getSessionCart() : null;
	}

	public boolean hasCheckoutCart()
	{
		return getCartFacade().hasSessionCart();
	}

	protected boolean hasItemsMatchingPredicate(final Predicate<AbstractOrderEntryModel> predicate)
	{
		final CartModel cart = getCart();
		if (cart != null && CollectionUtils.isNotEmpty(cart.getEntries()))
		{
			for (final AbstractOrderEntryModel entry : cart.getEntries())
			{
				if (predicate.test(entry))
				{
					return true;
				}
			}
		}
		return false;
	}

	protected boolean checkIfCurrentUserIsTheCartUser()
	{
		final CartModel cartModel = getCart();
		return cartModel == null ? false : cartModel.getUser().equals(getCurrentUserForCheckout());
	}

	protected CustomerModel getCurrentUserForCheckout()
	{
		return getCheckoutCustomerStrategy().getCurrentUserForCheckout();
	}

	protected String getPaymentProvider()
	{
		return getHmCommerceCheckoutService().getPaymentProvider();
	}

	protected CommerceCheckoutParameter createCommerceCheckoutParameter(final CartModel cart, final boolean enableHooks)
	{
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(enableHooks);
		parameter.setCart(cart);
		return parameter;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@Required
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	protected <T extends CartService> T getCartService()
	{
		return (T) cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
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

	public HmCommerceCheckoutService getHmCommerceCheckoutService()
	{
		return hmCommerceCheckoutService;
	}

	public void setHmCommerceCheckoutService(final HmCommerceCheckoutService hmCommerceCheckoutService)
	{
		this.hmCommerceCheckoutService = hmCommerceCheckoutService;
	}
}