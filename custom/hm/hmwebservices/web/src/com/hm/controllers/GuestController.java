/**
 *
 */
package com.hm.controllers;

import static com.hm.constants.HmwebservicesConstants.CLIENT_CREDENTIAL_AUTHORIZATION_NAME;
import static com.hm.constants.HmwebservicesConstants.PASSWORD_AUTHORIZATION_NAME;

import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hm.facades.guest.GuestFacade;
import com.hm.webservices.data.GuestDataList;
import com.hm.webservices.dto.guest.GuestDataListWSDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * @author Sathya.m
 *
 */
@Controller
@RequestMapping(value = "/guest")
@Tag(name = "Guest")
public class GuestController
{

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	private static final Logger LOGGER = Logger.getLogger(GuestController.class);
	public static final String DEFAULT_FIELD_SET = "DEFAULT";

	@Resource(name = "guestFacade")
	private GuestFacade guestFacade;

	@GetMapping(value = "/{guestId}", produces =
	{ "application/json", "application/xml" })
	@ResponseBody
	@Operation(operationId = "getGuestDetails", summary = "Get a Specific Guest Details", description = "Return a specific Guest based on guestId", security = @SecurityRequirement(name = "oauth", scopes =
	{ CLIENT_CREDENTIAL_AUTHORIZATION_NAME, PASSWORD_AUTHORIZATION_NAME }))
	@ApiBaseSiteIdParam
	public GuestDataListWSDTO getGuestDetails(@Parameter(description = "guestId", required = true)
	@PathVariable
	final String guestId, @ApiFieldsParam(examples = "guestId,guestName")
	@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields)
	{
		LOGGER.info("Guest is " + guestId);
		final GuestDataList guestDataList = new GuestDataList();
		guestDataList.setGuest(guestFacade.getGuestDetails(guestId));
		return dataMapper.map(guestDataList, GuestDataListWSDTO.class, fields);
	}
}
