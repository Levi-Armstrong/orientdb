package com.orientechnologies.orient.core.exception;

import com.orientechnologies.common.exception.OErrorCode;
import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.storage.impl.local.paginated.base.ODurableComponent;

/**
 * @author Andrey Lomakin <lomakin.andrey@gmail.com>.
 * @since 9/28/2015
 */
public abstract class OCoreException extends OException {
  private OErrorCode   errorCode;

  private final String storageURL;
  private final String componentName;

  public OCoreException(OCoreException exception) {
    super(exception);
    this.storageURL = exception.storageURL;
    this.componentName = exception.componentName;
  }

  public OCoreException(String message) {
    this(message, null, null);
  }

  public OCoreException(String message, ODurableComponent component) {
    this(message, component, null);

  }

  public OCoreException(String message, ODurableComponent component, OErrorCode errorCode) {
    super(message);

    this.errorCode = errorCode;

    if (component != null) {
      componentName = component.getName();
    } else {
      componentName = null;
    }

    final ODatabaseDocumentInternal database = ODatabaseRecordThreadLocal.INSTANCE.getIfDefined();
    if (database != null) {
      storageURL = database.getURL();
    } else {
      storageURL = null;
    }

  }

  public OErrorCode getErrorCode() {
    return errorCode;
  }

  public String getStorageURL() {
    return storageURL;
  }

  public String getComponentName() {
    return componentName;
  }

  @Override
  public String getMessage() {
    final StringBuilder builder = new StringBuilder(super.getMessage());
    if (storageURL != null) {
      builder.append("\r\n\t").append("Storage URL:\"").append(storageURL).append("\"");
    }
    if (componentName != null) {
      builder.append("\r\n\t").append("Component name:\"").append(componentName).append("\"");
    }
    if (errorCode != null) {
      builder.append("\r\n\t").append("Error code:\"").append(errorCode.getCode()).append("\"");
    }

    return builder.toString();
  }
}
