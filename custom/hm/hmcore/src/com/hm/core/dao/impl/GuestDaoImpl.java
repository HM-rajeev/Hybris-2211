/**
 *
 */
package com.hm.core.dao.impl;

import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hm.core.dao.GuestDao;
import com.hm.model.GuestDetailsModel;


/**
 * @author Sathya.m
 *
 */
public class GuestDaoImpl extends AbstractItemDao implements GuestDao
{
	private static final Logger LOGGER = Logger.getLogger(GuestDaoImpl.class);
	private static final String GUEST_QUERY = "SELECT{" + GuestDetailsModel.PK + "}FROM{" + GuestDetailsModel._TYPECODE + "}WHERE{"
			+ GuestDetailsModel.GUESTID + "}=?code";

	@Override
	public List<GuestDetailsModel> getGuestDetailsByCode(final String guestId)
	{
		ServicesUtil.validateParameterNotNull(guestId, "Guest ID must not be null");
		final Map<String, Object> params = new HashMap<>();
		params.put("code", guestId);
		LOGGER.info(getFlexibleSearchService().search(GUEST_QUERY, params).getClass());
		final SearchResult<GuestDetailsModel> guest = getFlexibleSearchService().search(GUEST_QUERY, params);
		return guest.getResult() == null ? Collections.emptyList() : guest.getResult();
	}
}
