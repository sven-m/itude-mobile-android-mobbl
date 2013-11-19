/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.mobbl1;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.itude.mobile.android.util.DeviceUtil;
import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBRESTServiceDataHandler;
import com.itude.mobile.mobbl2.client.core.util.MBProperties;

public abstract class MBMobbl1ServerDataHandlerBase extends MBRESTServiceDataHandler
{
  private String _dataHandlerType;

  //
  //expects an argument Document of type MBMobbl1Request
  @Override
  public MBDocument doLoadDocument(String documentName, MBDocument doc)
  {
    String universeID = MBProperties.getInstance().getValueForProperty("mobblUniverseID");
    String uidPrefix = MBProperties.getInstance().getValueForProperty("UIDPrefix");
    String uid = uidPrefix + DeviceUtil.getInstance().getUniqueID();
    String secret = MBProperties.getInstance().getValueForProperty("mobblSecret");

    // package the incoming document in a StrayClient envelope
    String applicationID = (String) doc.getValueForPath("Operation[0]/@name");
    MBDocument mobblDoc = getRequestDocumentForApplicationID(applicationID);
    MBElement mobblRequest = (MBElement) mobblDoc.getValueForPath("StrayClient[0]/SendDataDetails[0]/request[0]");

    //
    @SuppressWarnings({"unchecked"})
    List<MBElement> parameters = (List<MBElement>) doc.getValueForPath("Operation[0]/Parameter");

    for (MBElement parameter : parameters)
    {
      MBElement mobblParameter = mobblRequest.createElement("parameter");
      String key = parameter.getValueForAttribute("key");
      String value = parameter.getValueForAttribute("value");
      mobblParameter.setAttributeValue(key, "key");
      mobblParameter.setAttributeValue(value, "value");
    }

    MBElement sendData = (MBElement) mobblDoc.getValueForPath("StrayClient[0]");

    // ======== Mobbl1 generic stuff =========
    Date currentDate = new Date();

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    String dateTime = formatter.format(currentDate);

    //
    sendData.setAttributeValue("http://straysystems.com/xsd/strayclient", "xmlns");
    sendData.setAttributeValue("SendData", "command");
    sendData.setAttributeValue(universeID, "universeID");
    sendData.setAttributeValue(dateTime, "dateTime");
    sendData.setAttributeValue(uid, "iPhoneUID");
    // TODO iPhoneUID will need to be changed back to uniqueDeviceID --> mobbls.alex.com does not support this yet
    //    sendData.setAttributeValue(UID, "uniqueDeviceID");

    // didn't use StringUtil on purpose. Although it is unwise to have an empty or space as secret key; why shouldn't it be possible?
    if (secret != null)
    {
      String messageID = StringUtil.md5(dateTime + uid + secret);
      sendData.setAttributeValue(messageID, "messageID");
    }

    super.setDocumentFactoryType(_dataHandlerType);
    return super.doLoadDocument(documentName, mobblDoc);
  }

  public MBDocument getRequestDocumentForApplicationID(String applicationID)
  {
    MBDocument requestDoc = MBDataManagerService.getInstance().loadDocument("MBMobbl1Request");
    MBElement element = (MBElement) requestDoc.getValueForPath("StrayClient[0]");
    element.setAttributeValue(applicationID, "applicationID");
    return requestDoc;
  }

  @Override
  protected String getRequestUrlFromDocument(String urlString, MBDocument document) throws UnsupportedEncodingException
  {
    return urlString;
  }

  protected void setDataHandlerType(String type)
  {
    _dataHandlerType = type;
  }
}
