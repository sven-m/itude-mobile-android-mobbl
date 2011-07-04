package com.itude.mobile.mobbl2.client.core.util;

public interface Constants
{

  public static String       APPLICATION_NAME                              = "MOBBL";
  public static String       C_STYLESCHEME                                 = "binckStyleScheme";

  public static String       C_SPLASHSCREEN                                = "whitelabel-splashscreen";
  public static String       C_ENCODING                                    = "UTF-8";
  public static String       C_ARROW                                       = "arrow";

  //container types
  public static final String C_SECTION                                     = "SECTION";
  public static final String C_ROW                                         = "ROW";
  public static final String C_MATRIXHEADER                                = "MATRIX-HEADER";
  public static final String C_MATRIXROW                                   = "MATRIX-ROW";
  public static final String C_MATRIXTITLEROW                              = "MATRIX-ROW-TITLE";
  public static final String C_MATRIX                                      = "MATRIX";
  public static final String C_EDITABLEMATRIX                              = "EDITABLEMATRIX";

  // cell types for use in identifiers
  public static final String C_REGULARCELL                                 = "REGULARCELL";
  public static final String C_SUBTITLECELL                                = "SUBTITLECELL";
  public static final String C_DROPDOWNLISTCELL                            = "DROPDOWNLISTCELL";
  public static final String C_WEBVIEWCELL                                 = "WEBVIEWCELL";
  // field types
  public static final String C_FIELD_LABEL                                 = "LABEL";
  public static final String C_FIELD_SUBLABEL                              = "SUBLABEL";
  public static final String C_FIELD_BUTTON                                = "BUTTON";
  public static final String C_FIELD_TEXT                                  = "TEXT";
  public static final String C_FIELD_IMAGE                                 = "IMAGE";
  public static final String C_FIELD_ICON                                  = "ICON";
  public static final String C_FIELD_INPUT                                 = "INPUTFIELD";
  public static final String C_FIELD_PASSWORD                              = "PASSWORDFIELD";
  public static final String C_FIELD_DROPDOWNLIST                          = "DROPDOWNLIST";
  public static final String C_FIELD_CHECKBOX                              = "CHECKBOX";
  public static final String C_FIELD_RADIOGROUPFIELD                       = "RADIOGROUP";
  public static final String C_FIELD_MATRIXTITLE                           = "MATRIX-TITLE";
  public static final String C_FIELD_MATRIXCELL                            = "MATRIX-CELL";

  // field styles
  public static String       C_FIELD_STYLE_MATRIXCOLUMN                    = "MATRIX-COLUMN";
  public static String       C_FIELD_STYLE_UNDERLYINGSTOCKVALUE            = "UNDERLYINGSTOCKVALUE";
  public static String       C_FIELD_STYLE_DIFFABLE_MARKER                 = "DIFFABLE_MARKER";
  public static String       C_FIELD_STYLE_DIFFABLE_PRIMARY                = "DIFFABLE_PRIMARY";
  public static String       C_FIELD_STYLE_DIFFABLE_SECONDARY              = "DIFFABLE_SECONDARY";

  // field datatypes
  public static final String C_FIELD_DATATYPE_INT                          = "int";
  public static final String C_FIELD_DATATYPE_double                       = "double";
  public static final String C_FIELD_DATATYPE_float                        = "float";

  // button styles
  public static final String C_FIELD_STYLE_NAVIGATION                      = "NAVIGATION";
  public static final String C_FIELD_STYLE_NETWORK                         = "NETWORK";
  public static final String C_FIELD_STYLE_POPUP                           = "POPUP";

  // Alignment
  public static final String C_ALIGNMENT_LEFT                              = "LEFT";
  public static final String C_ALIGNMENT_CENTER                            = "CENTER";
  public static final String C_ALIGNMENT_RIGHT                             = "RIGHT";
  public static final String C_ALIGNMENT_CENTER_VERTICAL                   = "CENTER_VERTICAL";

  // Locale related constants
  public static final String C_LOCALE_CODE_DUTCH                           = "nl_NL";

  public static final String C_TRUE                                        = "1";
  public static final String C_FALSE                                       = "0";

  public static final String C_PAGE_CONTENT_VIEW                           = "PAGE-CONTENT-VIEW";
  public static final String C_PAGE_CONTENT_HEADER_VIEW                    = "PAGE-CONTENT-HEADER-VIEW";
  public static final String C_PAGE_CONTENT_HEADER_TITLE_VIEW              = "PAGE-CONTENT-HEADER-TITLE-VIEW";
  public static final String C_PAGE_SOURCE_VIEW                            = "PAGE-SOURCE-VIEW";
  public static final String C_PAGE_ORIENTATION_PERMISSION_LANDSCAPE       = "LANDSCAPE";
  public static final String C_PAGE_ORIENTATION_PERMISSION_PORTRAIT        = "PORTRAIT";
  public static final String C_PAGE_ORIENTATION_PERMISSION_ANY             = "ANY";

  public static final String C_BUTTON_REFRESH                              = "BUTTON-REFRESH";
  public static final String C_BUTTON_DELETE                               = "BUTTON-DELETE";
  public static final String C_BUTTON_UP                                   = "BUTTON-UP";
  public static final String C_BUTTON_DOWN                                 = "BUTTON-DOWN";

  // EditableMatrix 
  public static final String C_EDITABLEMATRIX_EDITBUTTON                   = "EDITABLEMATRIX-EDITBUTTON";
  public static final String C_EDITABLEMATRIX_LEFTBUTTONSCONTAINER         = "EDITABLEMATRIX-LEFTBUTTONSCONTAINER";
  public static final String C_EDITABLEMATRIX_CHECKBOX                     = "EDITABLEMATRIX-CHECKBOX";
  public static final String C_EDITABLEMATRIX_DELETEBUTTON                 = "EDITABLEMATRIX-DELETEBUTTON";
  public static final String C_EDITABLEMATRIX_RIGHTBUTTONSCONTAINER        = "EDITABLEMATRIX-RIGHTBUTTONSCONTAINER";
  public static final String C_EDITABLEMATRIX_UPBUTTON                     = "EDITABLEMATRIX-UPBUTTON";
  public static final String C_EDITABLEMATRIX_DOWNBUTTON                   = "EDITABLEMATRIX-DOWNBUTTON";
  public static final String C_EDITABLEMATRIX_MODE_EDITONLY                = "EDITONLY";
  public static final String C_EDITABLEMATRIX_MODE_EDIT                    = "EDIT";
  public static final String C_EDITABLEMATRIX_MODE_VIEW                    = "VIEW";
  public static final String C_EDITABLEMATRIX_PERMISSION_DELETE            = "DELETABLE";
  public static final String C_EDITABLEMATRIX_PERMISSION_DRAGGABLE         = "DRAGGABLE";
  public static final String C_EDITABLEMATRIX_PERMISSION_SELECTABLE        = "SELECTABLE";
  public static final String C_EDITABLEMATRIX_PERMISSION_CLICKABLE         = "CLICKABLE";

  // Properties
  public static final String C_PROPERTY_HTTPS_ALLOW_ALL_CERTIFICATES       = "allowAllHttpsCertificates";

  // Parcelable types
  public static final int    C_PARCELABLE_TYPE_OUTCOME                     = 0;
  public static final int    C_PARCELABLE_TYPE_ELEMENT_CONTAINER           = 1;
  public static final int    C_PARCELABLE_TYPE_ELEMENT                     = 2;
  public static final int    C_PARCELABLE_TYPE_DEFINITION                  = 3;
  public static final int    C_PARCELABLE_TYPE_ELEMENT_DEFINITION          = 4;
  public static final int    C_PARCELABLE_TYPE_ATTRIBUTE_DEFINITION        = 5;
  public static final int    C_PARCELABLE_TYPE_DOMAIN_DEFINITION           = 6;
  public static final int    C_PARCELABLE_TYPE_DOMAIN_VALIDATOR_DEFINITION = 7;
  public static final int    C_PARCELABLE_TYPE_DOCUMENT                    = 8;
  public static final int    C_PARCELABLE_TYPE_DOCUMENT_DEFINITION         = 9;

  // Messages
  public static final int    C_MESSAGE_INITIAL_OUTCOMES_FINISHED           = 10;
}
