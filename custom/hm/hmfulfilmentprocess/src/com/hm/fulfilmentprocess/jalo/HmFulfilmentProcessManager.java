/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.fulfilmentprocess.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import com.hm.fulfilmentprocess.constants.HmFulfilmentProcessConstants;

public class HmFulfilmentProcessManager extends GeneratedHmFulfilmentProcessManager
{
	public static final HmFulfilmentProcessManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (HmFulfilmentProcessManager) em.getExtension(HmFulfilmentProcessConstants.EXTENSIONNAME);
	}
	
}
