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
package com.itude.mobile.mobbl.core.util;

public interface MBConstants {

    public static String APPLICATION_NAME = "MOBBL";
    public static String C_STYLESCHEME = "styleScheme";

    public static String C_ENCODING = "UTF-8";

    public static String C_APPLICATION_ENVIRONMENT = "Environment";

    public static String C_SHOW_AS_TAB = "TAB";
    public static String C_SHOW_AS_MENU = "MENU";
    public static String C_SHOW_AS_DOCUMENT = "DOCUMENT";

    //Framework outcomes
    public static final String C_MOBBL_ORIGIN_NAME_CONTROLLER = "Controller";
    public static final String C_MOBBL_ORIGIN_CONTROLLER_INIT = "init";


    //container types
    public static final String C_SECTION = "SECTION";
    public static final String C_ROW = "ROW";
    public static final String C_LIST = "LIST";
    public static final String C_PLAIN = "PLAIN";
    public static final String C_MATRIXHEADER_CONTAINER = "MATRIX-HEADER-CONTAINER";
    public static final String C_MATRIXHEADER = "MATRIX-HEADER";
    public static final String C_MATRIXROW = "MATRIX-ROW";
    public static final String C_MATRIXTITLEROW = "MATRIX-ROW-TITLE";
    public static final String C_MATRIX = "MATRIX";
    public static final String C_SEGMENTEDCONTROL = "SEGMENTEDCONTROL";

    // cell types for use in identifiers
    public static final String C_REGULARCELL = "REGULARCELL";
    public static final String C_SUBTITLECELL = "SUBTITLECELL";
    public static final String C_DROPDOWNLISTCELL = "DROPDOWNLISTCELL";
    public static final String C_WEBVIEWCELL = "WEBVIEWCELL";
    // field types
    public static final String C_FIELD_LABEL = "LABEL";
    public static final String C_FIELD_SUBLABEL = "SUBLABEL";
    public static final String C_FIELD_BUTTON = "BUTTON";
    public static final String C_FIELD_IMAGEBUTTON = "IMAGEBUTTON";
    public static final String C_FIELD_TEXT = "TEXT";
    public static final String C_FIELD_IMAGE = "IMAGE";
    public static final String C_FIELD_ICON = "ICON";
    public static final String C_FIELD_INPUT = "INPUTFIELD";
    public static final String C_FIELD_PASSWORD = "PASSWORDFIELD";
    public static final String C_FIELD_DROPDOWNLIST = "DROPDOWNLIST";
    public static final String C_FIELD_CHECKBOX = "CHECKBOX";
    public static final String C_FIELD_RADIOGROUPFIELD = "RADIOGROUP";
    public static final String C_FIELD_MATRIXTITLE = "MATRIX-TITLE";
    public static final String C_FIELD_MATRIXDESCRIPTION = "MATRIX-DESCRIPTION";

    public static final String C_FIELD_MATRIXCELL = "MATRIX-CELL";
    public static final String C_FIELD_WEB = "WEB";
    public static String C_FIELD_DATE = "DATESELECTOR";
    public static String C_FIELD_TIME = "TIMESELECTOR";

    // field styles
    public static String C_FIELD_STYLE_MATRIXCOLUMN = "MATRIX-COLUMN";
    public static String C_FIELD_STYLE_UNDERLYINGSTOCKVALUE = "UNDERLYINGSTOCKVALUE";
    public static String C_FIELD_STYLE_DIFFABLE_MARKER = "DIFFABLE_MARKER";
    public static String C_FIELD_STYLE_DIFFABLE_PRIMARY = "DIFFABLE_PRIMARY";
    public static String C_FIELD_STYLE_DIFFABLE_SECONDARY = "DIFFABLE_SECONDARY";

    // Matrix row styles
    public static String C_STYLE_DOUBLE_LINED_MATRIX_ROW = "DOUBLE-LINED";
    public static String C_STYLE_SINGLE_LINED_MATRIX_ROW = "SINGLE-LINED";

    // Row styles
    public static String C_STYLE_WRAP_ROW = "WRAP";

    // field datatypes
    public static final String C_FIELD_DATATYPE_INT = "int";
    public static final String C_FIELD_DATATYPE_DOUBLE = "double";
    public static final String C_FIELD_DATATYPE_FLOAT = "float";

    // button styles
    public static final String C_FIELD_STYLE_NAVIGATION = "NAVIGATION";
    public static final String C_FIELD_STYLE_NETWORK = "NETWORK";
    public static final String C_FIELD_STYLE_POPUP = "POPUP";

    // Alignment
    public static final String C_ALIGNMENT_LEFT = "LEFT";
    public static final String C_ALIGNMENT_CENTER = "CENTER";
    public static final String C_ALIGNMENT_RIGHT = "RIGHT";
    public static final String C_ALIGNMENT_CENTER_VERTICAL = "CENTER_VERTICAL";

    // Locale related constants
    public static final String C_LOCALE_CODE_DUTCH = "nl_NL";
    public static final String C_LOCALE_CODE_ITALIAN = "it_IT";
    public static final String C_LANGUAGE_ENGLISH = "en";

    public static final String C_TRUE = "true";
    public static final String C_FALSE = "false";

    public static final String C_PAGE_CONTENT_VIEW = "PAGE-CONTENT-VIEW";
    public static final String C_PAGE_SOURCE_VIEW = "PAGE-SOURCE-VIEW";
    public static final String C_PAGE_ORIENTATION_PERMISSION_LANDSCAPE = "LANDSCAPE";
    public static final String C_PAGE_ORIENTATION_PERMISSION_PORTRAIT = "PORTRAIT";
    public static final String C_PAGE_ORIENTATION_PERMISSION_ANY = "ANY";

    public static final String C_BUTTON_REFRESH = "BUTTON-REFRESH";
    public static final String C_BUTTON_FULLSCREEN = "BUTTON-FULLSCREEN";
    public static final String C_ICON_TRANSPARENT = "ICON-transparent";

    // Properties
    public static final String C_PROPERTY_INDEVELOPMENT = "inDevelopment";
    public static final String C_PROPERTY_STRICTMODE = "enableStrictMode";
    public static final String C_PROPERTY_LOGLEVEL = "logLevel";
    public static final String C_PROPERTY_IMAGE_CACHE_MEMORY = "imageCacheMemSize";
    public static final String C_PROPERTY_IMAGE_CACHE_DISK = "imageCacheDiskSize";

    // Parcelable types
    public static final int C_PARCELABLE_TYPE_OUTCOME = 0;
    public static final int C_PARCELABLE_TYPE_ELEMENT_CONTAINER = 1;
    public static final int C_PARCELABLE_TYPE_ELEMENT = 2;
    public static final int C_PARCELABLE_TYPE_DEFINITION = 3;
    public static final int C_PARCELABLE_TYPE_ELEMENT_DEFINITION = 4;
    public static final int C_PARCELABLE_TYPE_ATTRIBUTE_DEFINITION = 5;
    public static final int C_PARCELABLE_TYPE_DOMAIN_DEFINITION = 6;
    public static final int C_PARCELABLE_TYPE_DOMAIN_VALIDATOR_DEFINITION = 7;
    public static final int C_PARCELABLE_TYPE_DOCUMENT = 8;
    public static final int C_PARCELABLE_TYPE_DOCUMENT_DEFINITION = 9;
    public static final int C_PARCELABLE_TYPE_ORIGIN = 10;

    // Intent related
    public static final String C_INTENT_POST_INITIALOUTCOMES_OUTCOMENAME = "POST_INITIALOUTCOMES_OUTCOMENAME";

    // Messages
    public static final int C_MESSAGE_INITIAL_OUTCOMES_FINISHED = 10;

    // Http request methods
    public static final String C_HTTP_REQUEST_METHOD_GET = "GET";
    public static final String C_HTTP_REQUEST_METHOD_POST = "POST";
    public static final String C_HTTP_REQUEST_METHOD_PUT = "PUT";
    public static final String C_HTTP_REQUEST_METHOD_DELETE = "DELETE";
    public static final String C_HTTP_REQUEST_METHOD_HEAD = "HEAD";
    public static final String C_HTTP_REQUEST_CONTENT_TYPE = "Content-Type";

    // Gravity
    public static String C_GRAVITY_LEFT = "LEFT";
    public static String C_GRAVITY_RIGHT = "RIGHT";
    public static String C_GRAVITY_TOP = "TOP";
    public static String C_GRAVITY_BOTTOM = "BOTTOM";
    public static String C_GRAVITY_CENTER = "CENTER";

    // Stated resources
    public static String C_STATED_RESOURCE_STATE_TYPE_IMAGE = "IMAGE";
    public static String C_STATED_RESOURCE_STATE_TYPE_COLOR = "COLOR";

    public static String C_STATED_RESOURCE_STATE_ENABLED = "enabled";
    public static String C_STATED_RESOURCE_STATE_SELECTED = "selected";
    public static String C_STATED_RESOURCE_STATE_PRESSED = "pressed";
    public static String C_STATED_RESOURCE_STATE_DISABLED = "disabled";
    public static String C_STATED_RESOURCE_STATE_CHECKED = "checked";
    public static String C_STATED_RESOURCE_STATE_UNCHECKED = "unchecked";

    // Display modes
    public static String C_DISPLAY_MODE_REPLACE = "REPLACE";
    public static String C_DISPLAY_MODE_BACKGROUND = "BACKGROUND";

    //Needs refactoring
    public static String C_DISPLAY_MODE_BACKGROUNDPIPELINEREPLACE = "BACKGROUND|REPLACE";

}