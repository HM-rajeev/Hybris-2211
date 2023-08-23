/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.commands;

import de.hybris.platform.payment.commands.CaptureCommand;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.result.CaptureResult;

import org.apache.log4j.Logger;

import com.hm.service.HmsPaymentService;

import Model.PtsV2PaymentsCapturesPost201Response;

/**
 * @author Sagar.patil
 *
 */

public class HmCaptureCommand implements CaptureCommand
{
	private static final Logger LOG = Logger.getLogger(HmCaptureCommand.class);

	private final String CAPTURE_RECEIVED_RESPONSE = "[capture-received]";

	private HmsPaymentService hmsPaymentService;

	@Override
	public CaptureResult perform(final CaptureRequest request)
	{
		CaptureResult result = new CaptureResult();
		String code = "TC50171_3";
		String totalAmount = "82.45";
		String currency = "USD";
		String id = "";
		PtsV2PaymentsCapturesPost201Response ptsV2PaymentsCapturesPost201Response = getHmsPaymentService().capture(code, totalAmount, currency, id);
		return result;
	}

	public HmsPaymentService getHmsPaymentService()
	{
		return hmsPaymentService;
	}

	public void setHmsPaymentService(final HmsPaymentService hmsPaymentService)
	{
		this.hmsPaymentService = hmsPaymentService;
	}	
}