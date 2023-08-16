/**
 *
 */
package com.hm.core.services.impl;

import java.util.List;

import javax.annotation.Resource;

import com.hm.core.dao.GuestDao;
import com.hm.core.services.GuestService;
import com.hm.model.GuestDetailsModel;


/**
 * @author Sathya.m
 *
 */
public class GuestServiceImpl implements GuestService
{

	@Resource
	private GuestDao guestDao;

	@Override
	public List<GuestDetailsModel> getGuestIdDetails(final String guestId)
	{
		return guestDao.getGuestDetailsByCode(guestId);
	}

}
