/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.exceptions;

import Model.PtsV2PaymentsCapturesPost201Response;
import Model.PtsV2PaymentsPost201Response;

/**
 * @author Sagar.patil
 *
 */

public class HmNonAuthorizedPaymentException extends Exception
{
	private PtsV2PaymentsPost201Response ptsV2PaymentsPost201Response;
	
	private PtsV2PaymentsCapturesPost201Response ptsV2PaymentsCapturesPost201Response;
	
	public HmNonAuthorizedPaymentException(String message) 
	{
		super(message);
   }
	
	public HmNonAuthorizedPaymentException(PtsV2PaymentsPost201Response ptsV2PaymentsPost201Response)
	{
       this.ptsV2PaymentsPost201Response = ptsV2PaymentsPost201Response;
   }
	
	public HmNonAuthorizedPaymentException(PtsV2PaymentsCapturesPost201Response ptsV2PaymentsCapturesPost201Response)
	{
       this.ptsV2PaymentsCapturesPost201Response = ptsV2PaymentsCapturesPost201Response;
   }

	public PtsV2PaymentsPost201Response getPtsV2PaymentsPost201Response()
	{
		return ptsV2PaymentsPost201Response;
	}
	
	public void setPtsV2PaymentsPost201Response(final PtsV2PaymentsPost201Response ptsV2PaymentsPost201Response)
	{
		this.ptsV2PaymentsPost201Response = ptsV2PaymentsPost201Response;
	}
	
	public PtsV2PaymentsCapturesPost201Response getPtsV2PaymentsCapturesPost201Response()
	{
		return ptsV2PaymentsCapturesPost201Response;
	}
	
	public void setPtsV2PaymentsCapturesPost201Response(final PtsV2PaymentsCapturesPost201Response ptsV2PaymentsCapturesPost201Response)
	{
		this.ptsV2PaymentsCapturesPost201Response = ptsV2PaymentsCapturesPost201Response;
	}	
}