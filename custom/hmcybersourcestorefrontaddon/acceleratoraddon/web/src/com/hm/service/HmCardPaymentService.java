/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.service;

import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.payment.commands.request.AuthorizationRequest;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.request.CreateSubscriptionRequest;
import de.hybris.platform.payment.commands.request.DeleteSubscriptionRequest;
import de.hybris.platform.payment.commands.request.EnrollmentCheckRequest;
import de.hybris.platform.payment.commands.request.FollowOnRefundRequest;
import de.hybris.platform.payment.commands.request.PartialCaptureRequest;
import de.hybris.platform.payment.commands.request.StandaloneRefundRequest;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.request.SubscriptionDataRequest;
import de.hybris.platform.payment.commands.request.UpdateSubscriptionRequest;
import de.hybris.platform.payment.commands.request.VoidRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.payment.commands.result.EnrollmentCheckResult;
import de.hybris.platform.payment.commands.result.RefundResult;
import de.hybris.platform.payment.commands.result.SubscriptionDataResult;
import de.hybris.platform.payment.commands.result.SubscriptionResult;
import de.hybris.platform.payment.commands.result.VoidResult;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.methods.CardPaymentService;

import java.math.BigDecimal;
import java.util.Currency;

import Model.PtsV2PaymentsCapturesPost201Response;
import Model.PtsV2PaymentsPost201Response;

/**
 * @author Sagar.patil
 *
 */

public interface HmCardPaymentService extends CardPaymentService
{
	PtsV2PaymentsPost201Response authorize(final String merchantTransactionCode, final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final Currency currency, 
			final BigDecimal totalAmount, final BillingInfo shippingInfo);

	PtsV2PaymentsCapturesPost201Response capture(final String merchantTransactionCode, final String requestId,
			final Currency currency, final BigDecimal totalAmount);

	AuthorizationResult authorize(AuthorizationRequest var1);

	AuthorizationResult authorize(SubscriptionAuthorizationRequest var1);

	CaptureResult capture(CaptureRequest var1);

	CaptureResult partialCapture(PartialCaptureRequest var1);

	EnrollmentCheckResult enrollmentCheck(EnrollmentCheckRequest var1);

	VoidResult voidCreditOrCapture(VoidRequest var1);

	RefundResult refundStandalone(StandaloneRefundRequest var1);

	RefundResult refundFollowOn(FollowOnRefundRequest var1);

	SubscriptionResult createSubscription(CreateSubscriptionRequest var1);

	SubscriptionResult updateSubscription(UpdateSubscriptionRequest var1);

	SubscriptionDataResult getSubscriptionData(SubscriptionDataRequest var1);

	SubscriptionResult deleteSubscription(DeleteSubscriptionRequest var1);
}