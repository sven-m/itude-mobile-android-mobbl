package com.itude.mobile.mobbl2.client.core.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.itude.mobile.android.util.CollectionUtilities;
import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBElementDefinition;
import com.itude.mobile.mobbl2.client.core.model.exceptions.MBCannotAssignException;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBDocumentOperationDelegate;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBDocument extends MBElementContainer
{
  private MBDocumentDefinition         _definition;
  private Map<String, MBDocument>      _sharedContext;
  private final Map<String, MBElement> _pathCache;
  private MBDocument                   _argumentsUsed;

  public MBDocument()
  {
    super();
    _sharedContext = new HashMap<String, MBDocument>();
    _pathCache = new HashMap<String, MBElement>();
  }

  public MBDocument(MBDocumentDefinition documentDefinition)
  {
    _definition = documentDefinition;
    _sharedContext = new HashMap<String, MBDocument>();
    _pathCache = new HashMap<String, MBElement>();
  }

  @Override
  public MBDocument clone()
  {
    MBDocument newDoc = new MBDocument(_definition);
    copyChildrenInto(newDoc);
    if (_argumentsUsed != null) newDoc.setArgumentsUsed(_argumentsUsed.clone());

    return newDoc;
  }

  @Override
  public Map<String, MBDocument> getSharedContext()
  {
    return _sharedContext;
  }

  @Override
  public void setSharedContext(Map<String, MBDocument> sharedContext)
  {
    _sharedContext = sharedContext;
  }

  public MBDocument getArgumentsUsed()
  {
    return _argumentsUsed;
  }

  public void setArgumentsUsed(MBDocument argumentsUsed)
  {
    _argumentsUsed = argumentsUsed;
  }

  public void assignToDocument(MBDocument target)
  {
    if (!target.getDefinition().getName().equals(_definition.getName()))
    {
      String message = "Cannot assign document since document types differ: " + target.getDefinition().getName() + " != "
                       + _definition.getName();
      throw new MBCannotAssignException(message);
    }

    target.getElements().clear();
    target._pathCache.clear();
    copyChildrenInto(target);
  }

  @Override
  public String getUniqueId()
  {
    String uid = "";

    // Specification: the uniqueId of a document starts with <docname>:
    // This is required for the cache manager to determine the document type
    uid += _definition.getName() + ":";
    uid += super.getUniqueId();

    return uid;
  }

  public void loadFreshCopyForDelegate(MBDocumentOperationDelegate delegate)
  {
    MBDataManagerService.getInstance().loadFreshDocument(_definition.getName(), _argumentsUsed, delegate);
  }

  public MBDocument loadFreshCopy()
  {
    MBDocument fresh;
    if (_argumentsUsed == null)
    {
      fresh = MBDataManagerService.getInstance().loadDocument(_definition.getName(), _argumentsUsed);
    }
    else
    {
      fresh = MBDataManagerService.getInstance().loadFreshDocument(_definition.getName(), _argumentsUsed);
    }
    return fresh;
  }

  // Be careful with reload since it might change the number of elements; making any existing path (indexes) invalid
  // It is safer to use loadFreshCopyForDelegate:resultSelector:errorSelector: and process the result in the callbacks
  public void reload()
  {

    MBDocument fresh;

    if (_argumentsUsed == null)
    {
      fresh = MBDataManagerService.getInstance().loadDocument(_definition.getName());
    }
    else
    {
      fresh = MBDataManagerService.getInstance().loadFreshDocument(_definition.getName(), _argumentsUsed);
    }

    setElements(fresh.getElements());
    _pathCache.clear();
  }

  public void clearPathCache()
  {
    _pathCache.clear();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getValueForPath(String path)
  {
    if (path != null)
    {
      int posAt = path.indexOf('@');
      if (posAt < 0 || path.indexOf('@', posAt + 1) >= 0) return (T) getValueForPath(path, null);

      String componentBeforeAt = path.substring(0, posAt);
      //      assert path.substring(posAt + 1).length() > 0;
      MBElement element = _pathCache.get(componentBeforeAt);

      if (element == null)
      {
        element = (MBElement) super.getValueForPath(componentBeforeAt);
        _pathCache.put(componentBeforeAt, element);
      }

      // TODO Check if this is a proper workaround for a bug which caused a nullpointer exception to occur
      if (element != null)
      {
        return (T) element.getValueForAttribute(path.substring(posAt + 1));
      }
      else
      {
        return null;
      }

    }
    else
    {
      return null;
    }
  }

  @Override
  public String toString()
  {
    StringBuffer rt = new StringBuffer();
    return this.asXmlWithLevel(rt, 0).toString();
  }

  public void clearAllCaches()
  {
    getSharedContext().clear();
    clearPathCache();
  }

  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return asXmlWithLevel(appendToMe, level, false);
  }

  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level, boolean escapeContent)
  {
    StringUtil.appendIndentString(appendToMe, level).append("<")
        .append((_definition.getRootElement() == null) ? _definition.getName() : _definition.getRootElement());

    if (getElements().size() == 0)
    {
      appendToMe.append("/>\n");
    }
    else
    {
      appendToMe.append(">\n");
      for (MBElementDefinition elemDef : _definition.getChildren())
      {
        List<MBElement> lst = getElements().get(elemDef.getName());
        if (CollectionUtilities.isNotEmpty(lst))
        {
          for (MBElement elem : lst)
          {
            elem.asXmlWithLevel(appendToMe, level + 2, escapeContent);
          }
        }
      }

      StringUtil.appendIndentString(appendToMe, level).append("</")
          .append((_definition.getRootElement() == null) ? _definition.getName() : _definition.getRootElement()).append(">\n");
    }

    return appendToMe;
  }

  @Override
  public MBDocumentDefinition getDefinition()
  {
    return _definition;
  }

  @Override
  public String getDocumentName()
  {
    return _definition.getName();
  }

  @Override
  public MBDocument getDocument()
  {
    return this;
  }

  //Parcelable stuff

  private MBDocument(Parcel in)
  {
    super(in);

    _sharedContext = new HashMap<String, MBDocument>();
    _pathCache = new HashMap<String, MBElement>();

    _definition = in.readParcelable(MBDocumentDefinition.class.getClassLoader());

    Bundle sharedContext = in.readBundle(MBDocument.class.getClassLoader());
    Bundle pathCache = in.readBundle(MBElement.class.getClassLoader());

    _argumentsUsed = in.readParcelable(MBDocument.class.getClassLoader());

    for (String key : sharedContext.keySet())
    {
      _sharedContext.put(key, (MBDocument) sharedContext.get(key));
    }

    for (String key : pathCache.keySet())
    {
      _pathCache.put(key, (MBElement) pathCache.get(key));
    }
  }

  @Override
  public int describeContents()
  {
    return Constants.C_PARCELABLE_TYPE_DOCUMENT;
  }

  @Override
  public void writeToParcel(Parcel out, int flags)
  {
    super.writeToParcel(out, flags);

    Bundle sharedContext = new Bundle();

    for (String key : _sharedContext.keySet())
    {
      sharedContext.putParcelable(key, _sharedContext.get(key));
    }

    Bundle pathCache = new Bundle();

    for (String key : _pathCache.keySet())
    {
      pathCache.putParcelable(key, _pathCache.get(key));
    }

    out.writeParcelable(_definition, flags);

    out.writeBundle(sharedContext);
    out.writeBundle(pathCache);

    out.writeParcelable(_argumentsUsed, flags);
  }

  public static final Parcelable.Creator<MBDocument> CREATOR = new Creator<MBDocument>()
                                                             {
                                                               @Override
                                                               public MBDocument[] newArray(int size)
                                                               {
                                                                 return new MBDocument[size];
                                                               }

                                                               @Override
                                                               public MBDocument createFromParcel(Parcel in)
                                                               {
                                                                 return new MBDocument(in);
                                                               }
                                                             };

  // End of parcelable stuff

}
