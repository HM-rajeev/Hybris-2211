/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.core.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import com.hm.core.constants.HmCoreConstants;
import com.hm.core.setup.CoreSystemSetup;


/**
 * Do not use, please use {@link CoreSystemSetup} instead.
 * 
 */
public class HmCoreManager extends GeneratedHmCoreManager
{
	public static final HmCoreManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (HmCoreManager) em.getExtension(HmCoreConstants.EXTENSIONNAME);
	}
}
