package com.itude.mobile.mobbl2.client.core.controller.util;

import java.util.ArrayList;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBAttributeDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBElementDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBAction;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.controller.exceptions.MBServerErrorMessageException;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;

public class MBFormSubmission implements MBAction
{

  final static String C_GENERIC_REQUEST = "MBGenericRequest";

  public MBOutcome execute(MBDocument document, String path)
  {

    validateDocument(document, path);

    MBOutcome outcome = null;
    String outcomeName;

    // get request name from document
    MBElement rootElement = (MBElement) ((ArrayList<?>) document.getElements().values().toArray()[0]).get(0);
    //TODO: check arrays are not empty else exceptions will be raised
    String requestName = rootElement.getName();

    // get outcome names from document
    String outcomeOK = rootElement.getValueForAttribute("outcomeOK");

    // set up generic request
    MBDocument request = MBDataManagerService.getInstance().loadDocument(C_GENERIC_REQUEST);
    request.setValue(requestName, "Request[0]/@name");

    // copy the attributes to the generic request
    MBElementDefinition elementDefinition = rootElement.getDefinition();
    ArrayList<MBAttributeDefinition> attributesArray = (ArrayList<MBAttributeDefinition>) elementDefinition.getAttributes();

    for (MBAttributeDefinition attributeDefinition : attributesArray)
    {

      // skip outcomeOK and
      String attributeName = attributeDefinition.getName();
      if (!"outcomeOK".equals(attributeName) && !"outcomeERROR".equals(attributeName))
      {
        String value = rootElement.getValueForAttribute(attributeName);
        setRequestParameter(value, attributeName, request);
      }
    }
    Log.d("MOBBL", "REQUEST = " + request);

    // retrieve generic response
    MBDocument response = MBDataManagerService.getInstance().loadDocument("MBGenericResponse", request);

    Log.d("MOBBL", "RESPONSE = " + response);

    String body = response.getValueForPath("Response[0]/@body");
    String error = response.getValueForPath("Response[0]/@error");

    // if error, throw error with errormessage
    if (error != null && error.trim().length() > 0)
    {
      Log.e("MOBBL", "Error returned by server: " + error);
      // use body rather than error since server-side puts error code in error and error message in body
      throw new MBServerErrorMessageException(body);
    }

    // if success, add OK action to document and navigate to confirmation page
    else if (outcomeOK == null)
    {
      response.setValue(outcomeOK, "Response[0]/@outcomeName");

      outcomeName = "OUTCOME-MBFormSubmissionOK";
      outcome = new MBOutcome(outcomeName, response);
    }
    else
    {
      outcomeName = outcomeOK;
      outcome = new MBOutcome(outcomeName, response);
    }

    return outcome;
  }

  public void validateDocument(MBDocument document, String path)
  {
    // subclasses should implement this method to perform validation
  }

  void setRequestParameter(String value, String key, MBDocument doc)
  {
    MBElement request = doc.getValueForPath("Request[0]");
    MBElement parameter = request.createElementWithName("Parameter");
    parameter.setAttributeValue(key, "key");
    parameter.setAttributeValue(value, "value");
  }

}
