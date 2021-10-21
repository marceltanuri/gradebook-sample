package com.liferay.training.gradebook.web.portlet.action;

import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.training.gradebook.exception.AssignmentValidationException;
import com.liferay.training.gradebook.model.Assignment;
import com.liferay.training.gradebook.service.AssignmentService;
import com.liferay.training.gradebook.web.constants.GradebookPortletKeys;
import com.liferay.training.gradebook.web.constants.MVCCommandNames;

/**
 * MVC Action Command for adding assignments.
 *
 * @author liferay
 */
@Component(immediate = true, property = { "javax.portlet.name=" + GradebookPortletKeys.GRADEBOOK,
		"mvc.command.name=" + MVCCommandNames.ADD_ASSIGNMENT }, service = MVCActionCommand.class)
public class AddAssignmentMVCActionCommand extends BaseMVCActionCommand {
	@Override
	protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		ServiceContext serviceContext = ServiceContextFactory.getInstance(Assignment.class.getName(), actionRequest);
		// Get parameters from the request.
		String title = ParamUtil.getString(actionRequest, "title");

		String description = ParamUtil.getString(actionRequest, "description", null);
		Date dueDate = ParamUtil.getDate(actionRequest, "dueDate", null);
		try {
			// Call the service to add a new assignment.

			_assignmentService.addAssignment(themeDisplay.getScopeGroupId(), title, description, dueDate,
					serviceContext);

			// Set the success message.
			SessionMessages.add(actionRequest, "assignmentAdded");
			sendRedirect(actionRequest, actionResponse);
		} catch (AssignmentValidationException ave) {

			// Get error messages from the service layer.
			ave.getErrors().forEach(key -> SessionErrors.add(actionRequest, key));

			ave.printStackTrace();
			actionResponse.setRenderParameter("mvcRenderCommandName", MVCCommandNames.EDIT_ASSIGNMENT);
		} catch (PortalException pe) {

			// Set error messages from the service layer.
			SessionErrors.add(actionRequest, "serviceErrorDetails", pe);
			pe.printStackTrace();

			actionResponse.setRenderParameter("mvcRenderCommandName", MVCCommandNames.EDIT_ASSIGNMENT);
		}
	}

	@Reference
	protected AssignmentService _assignmentService;
}