/**
 *
 */
package fr.paris.lutece.plugins.genericattributes.service.entrytype;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.EntryTypeHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.IMapProvider;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.MapProviderManager;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceList;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bass
 *
 */
public abstract class AbstractEntryTypeGeolocation extends EntryTypeService
{
    public static final String PARAMETER_ID_ENTRY = "id_entry";
    public static final String PARAMETER_ID_RESOURCE = "id_resource";

    /** The Constant PARAMETER_MAP_PROVIDER. */
    public static final String PARAMETER_MAP_PROVIDER = "map_provider";

    /** The Constant PARAMETER_EDIT_MODE. */
    public static final String PARAMETER_EDIT_MODE = "edit_mode";

    /** The Constant PARAMETER_VIEW_NUMBER. */
    public static final String PARAMETER_VIEW_NUMBER = "view_number";

    /** The Constant PARAMETER_SUFFIX_ID_ADDRESS. */
    public static final String PARAMETER_SUFFIX_ID_ADDRESS = "_idAddress";

    /** The Constant PARAMETER_SUFFIX_ADDRESS. */
    public static final String PARAMETER_SUFFIX_ADDRESS = "_address";

    /** The Constant PARAMETER_SUFFIX_ADDITIONAL_ADDRESS. */
    // public static final String PARAMETER_SUFFIX_ADDITIONAL_ADDRESS = "_additional_address";

    /** The Constant PARAMETER_SUFFIX_X_ADDRESS. */
    public static final String PARAMETER_SUFFIX_X = "_x";

    /** The Constant PARAMETER_SUFFIX_Y_ADDRESS. */
    public static final String PARAMETER_SUFFIX_Y = "_y";

    /** The Constant PARAMETER_SUFFIX_GEOMETRY. */
    public static final String PARAMETER_SUFFIX_GEOMETRY = "_geometry";

    /** The Constant CONSTANT_PROVIDER. */
    public static final String CONSTANT_PROVIDER = "provider";

    /** The Constant CONSTANT_PROVIDER. */
    public static final String CONSTANT_EDIT_MODE = "editMode";

    /** The Constant CONSTANT_VIEW_NUMBER. */
    public static final String CONSTANT_VIEW_NUMBER = "viewNumber";

    /** The Constant CONSTANT_ID_ADDRESS. */
    public static final String CONSTANT_ID_ADDRESS = "idAddress";

    /** The Constant CONSTANT_ADDRESS. */
    public static final String CONSTANT_ADDRESS = "address";

    /** The Constant CONSTANT_ADDRESS. */
    // public static final String CONSTANT_ADDITIONAL_ADDRESS = "additionalAddress";

    /** The Constant CONSTANT_X. */
    public static final String CONSTANT_X = "X";

    /** The Constant CONSTANT_Y. */
    public static final String CONSTANT_Y = "Y";

    /** The Constant CONSTANT_GEOMETRY. */
    public static final String CONSTANT_GEOMETRY = "geometry";

    // private static final String MESSAGE_SPECIFY_GEO = "genericattributes.message.specifyGeo";
    private static final String MESSAGE_SPECIFY_BOTH_X_AND_Y = "genericattributes.message.specifyBothXAndY";
    public static final String PARAMETER_EDIT_MODE_LIST = "gismap.edit.mode.list";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null ) ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim( ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strMapProvider = request.getParameter( PARAMETER_MAP_PROVIDER );
        String strEditMode = request.getParameter( PARAMETER_EDIT_MODE );
        String strViewNumber = request.getParameter( PARAMETER_VIEW_NUMBER );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );

        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = Integer.parseInt( strIdEntry );

        String strIdResource = request.getParameter( PARAMETER_ID_RESOURCE );
        int nIdResource = Integer.parseInt( strIdResource );

        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = FIELD_TITLE;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object [ ] tabRequiredFields = {
                I18nService.getLocalizedString( strFieldError, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        /**
         * we need 10 fields : 1 for map provider, 1 for id address, 1 for label address, 1 for x address, 1 for y address, 1 for id geographical object, 1 for
         * description geographical object, 1 for centroid geographical object, 1 for label geographical object and 1 for thematic geographical object
         **/
        if ( entry.getFields( ) == null )
        {
            List<Field> listFields = new ArrayList<Field>( );
            listFields.add( buildFieldMapProvider( entry, strMapProvider ) );
            listFields.add( buildField( entry, CONSTANT_EDIT_MODE, strEditMode ) );
            listFields.add( buildField( entry, CONSTANT_VIEW_NUMBER, strViewNumber ) );
            listFields.add( buildField( entry, CONSTANT_ID_ADDRESS, CONSTANT_ID_ADDRESS ) );
            listFields.add( buildField( entry, CONSTANT_ADDRESS, CONSTANT_ADDRESS ) );
            // listFields.add( buildField( entry, CONSTANT_ADDITIONAL_ADDRESS, CONSTANT_ADDITIONAL_ADDRESS ) );
            listFields.add( buildField( entry, CONSTANT_X, CONSTANT_X ) );
            listFields.add( buildField( entry, CONSTANT_Y, CONSTANT_Y ) );
            listFields.add( buildField( entry, CONSTANT_GEOMETRY, CONSTANT_GEOMETRY ) );

            entry.setFields( listFields );
        }
        else
        {
            entry.getFields( ).get( 0 ).setTitle( buildFieldMapProvider( entry, strMapProvider ).getTitle( ) );
            entry.getFields( ).get( 0 ).setValue( buildFieldMapProvider( entry, strMapProvider ).getValue( ) );

            entry.getFields( ).get( 1 ).setTitle( buildField( entry, CONSTANT_EDIT_MODE, strEditMode ).getTitle( ) );
            entry.getFields( ).get( 1 ).setValue( buildField( entry, CONSTANT_EDIT_MODE, strEditMode ).getValue( ) );

            entry.getFields( ).get( 2 ).setTitle( buildField( entry, CONSTANT_VIEW_NUMBER, strViewNumber ).getTitle( ) );
            entry.getFields( ).get( 2 ).setValue( buildField( entry, CONSTANT_VIEW_NUMBER, strViewNumber ).getValue( ) );

            entry.getFields( ).get( 3 ).setTitle( buildField( entry, CONSTANT_ID_ADDRESS, CONSTANT_ID_ADDRESS ).getTitle( ) );
            entry.getFields( ).get( 3 ).setValue( buildField( entry, CONSTANT_ID_ADDRESS, CONSTANT_ID_ADDRESS ).getValue( ) );

            entry.getFields( ).get( 4 ).setTitle( buildField( entry, CONSTANT_ADDRESS, CONSTANT_ADDRESS ).getTitle( ) );
            entry.getFields( ).get( 4 ).setValue( buildField( entry, CONSTANT_ADDRESS, CONSTANT_ADDRESS ).getValue( ) );

            // entry.getFields().get(5).setTitle(buildField( entry, CONSTANT_ADDITIONAL_ADDRESS, CONSTANT_ADDITIONAL_ADDRESS ).getTitle());
            // entry.getFields().get(5).setValue(buildField( entry, CONSTANT_ADDITIONAL_ADDRESS, CONSTANT_ADDITIONAL_ADDRESS).getValue());
            entry.getFields( ).get( 5 ).setTitle( buildField( entry, CONSTANT_X, CONSTANT_X ).getTitle( ) );
            entry.getFields( ).get( 5 ).setValue( buildField( entry, CONSTANT_X, CONSTANT_X ).getValue( ) );

            entry.getFields( ).get( 6 ).setTitle( buildField( entry, CONSTANT_Y, CONSTANT_Y ).getTitle( ) );
            entry.getFields( ).get( 6 ).setValue( buildField( entry, CONSTANT_Y, CONSTANT_Y ).getValue( ) );

            entry.getFields( ).get( 7 ).setTitle( buildField( entry, CONSTANT_GEOMETRY, CONSTANT_GEOMETRY ).getTitle( ) );
            entry.getFields( ).get( 7 ).setValue( buildField( entry, CONSTANT_GEOMETRY, CONSTANT_GEOMETRY ).getValue( ) );
        }

        entry.setTitle( strTitle );
        entry.setHelpMessage( strHelpMessage );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );
        entry.setMandatory( strMandatory != null );

        Entry entryAdditionalAddress = EntryHome.findByPrimaryKey( nIdEntry );
        boolean update = false;

        if ( entryAdditionalAddress != null )
        {
            update = true;
            getAdditionalAddressEntry( entryAdditionalAddress, locale, update );
        }
        else
        {
            entryAdditionalAddress = new Entry( );

            EntryType entryType = EntryTypeHome.findByPrimaryKey( 6 );
            entryAdditionalAddress.setEntryType( entryType );
            entryAdditionalAddress.setIdResource( nIdResource );
            entryAdditionalAddress.setResourceType( "FORM_FORM_TYPE" );

            getAdditionalAddressEntry( entryAdditionalAddress, locale, update );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        String strIdAddressValue = request.getParameter( entry.getIdEntry( ) + PARAMETER_SUFFIX_ID_ADDRESS );
        String strAddressValue = request.getParameter( entry.getIdEntry( ) + PARAMETER_SUFFIX_ADDRESS );

        // String strAdditionalAddressValue = request.getParameter( entry.getIdEntry( ) + PARAMETER_SUFFIX_ADDITIONAL_ADDRESS );
        String strXValue = request.getParameter( entry.getIdEntry( ) + PARAMETER_SUFFIX_X );
        String strYValue = request.getParameter( entry.getIdEntry( ) + PARAMETER_SUFFIX_Y );
        String strGeometryValue = request.getParameter( entry.getIdEntry( ) + PARAMETER_SUFFIX_GEOMETRY );

        Field fieldIdAddress = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_ID_ADDRESS, entry.getFields( ) );
        Field fieldAddress = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_ADDRESS, entry.getFields( ) );

        // Field fieldAdditionalAddress = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_ADDITIONAL_ADDRESS, entry.getFields( ) );
        Field fieldX = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_X, entry.getFields( ) );
        Field fieldY = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_Y, entry.getFields( ) );
        Field fieldGeometry = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_GEOMETRY, entry.getFields( ) );

        /**
         * Create the field "idAddress" in case the field does not exist in the database.
         */
        if ( fieldIdAddress == null )
        {
            fieldIdAddress = buildField( entry, CONSTANT_ID_ADDRESS, CONSTANT_ID_ADDRESS );
            FieldHome.create( fieldIdAddress );
        }

        // 1 : Response Id Address
        Response responseIdAddress = new Response( );
        responseIdAddress.setEntry( entry );
        responseIdAddress.setResponseValue( strIdAddressValue );
        responseIdAddress.setField( fieldIdAddress );
        responseIdAddress.setToStringValueResponse( strIdAddressValue );
        responseIdAddress.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( responseIdAddress );

        // 2 : Response Address
        Response responseAddress = new Response( );
        responseAddress.setEntry( entry );
        responseAddress.setResponseValue( strAddressValue );
        responseAddress.setField( fieldAddress );
        responseAddress.setToStringValueResponse( strAddressValue );
        responseIdAddress.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( responseAddress );

        // 2 : Response Additional Address
        /*
         * Response responseAdditionalAddress = new Response( ); responseAdditionalAddress.setEntry( entry ); responseAdditionalAddress.setResponseValue(
         * strAdditionalAddressValue ); responseAdditionalAddress.setField( fieldAdditionalAddress ); responseAdditionalAddress.setToStringValueResponse(
         * strAdditionalAddressValue ); listResponse.add( responseAdditionalAddress );
         */

        // 3 : Response X
        Response responseX = new Response( );
        responseX.setEntry( entry );
        responseX.setResponseValue( strXValue );
        responseX.setField( fieldX );
        responseX.setToStringValueResponse( strXValue );
        responseIdAddress.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( responseX );

        // 4: Response Y
        Response responseY = new Response( );
        responseY.setEntry( entry );
        responseY.setResponseValue( strYValue );
        responseY.setField( fieldY );
        responseY.setToStringValueResponse( strYValue );
        responseIdAddress.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( responseY );

        // 6 : Response Desc Geo
        Response responseGeomerty = new Response( );
        responseGeomerty.setEntry( entry );
        responseGeomerty.setResponseValue( strGeometryValue );
        responseGeomerty.setField( fieldGeometry );
        responseGeomerty.setToStringValueResponse( strGeometryValue );
        responseIdAddress.setIterationNumber( getResponseIterationValue( request ) );
        listResponse.add( responseGeomerty );

        if ( entry.isMandatory( ) )
        {
            if ( StringUtils.isBlank( strAddressValue ) )
            {
                return new MandatoryError( entry, locale );
            }
        }

        if ( ( StringUtils.isBlank( strXValue ) && StringUtils.isNotBlank( strYValue ) )
                || ( StringUtils.isNotBlank( strXValue ) && StringUtils.isBlank( strYValue ) ) )
        {
            if ( StringUtils.isBlank( strAddressValue ) )
            {
                GenericAttributeError error = new GenericAttributeError( );

                error.setMandatoryError( entry.isMandatory( ) );
                error.setTitleQuestion( entry.getTitle( ) );
                error.setErrorMessage( MESSAGE_SPECIFY_BOTH_X_AND_Y );

                return error;
            }
        }

        return super.getResponseData( entry, request, listResponse, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        if ( response.getField( ) != null )
        {
            String strTitle = response.getField( ).getTitle( );

            if ( CONSTANT_ADDRESS.equals( strTitle ) )
            {
                return response.getResponseValue( );
            }
        }

        return StringUtils.EMPTY;
    }

    /**
     * Returns the available map providers
     * 
     * @return all known map providers
     */
    public List<IMapProvider> getMaproviders( )
    {
        return MapProviderManager.getMapProvidersList( );
    }

    /**
     * Builds the {@link ReferenceList} of all available map providers
     * 
     * @return the {@link ReferenceList}
     */
    public ReferenceList getMapProvidersRefList( )
    {
        ReferenceList refList = new ReferenceList( );

        refList.addItem( StringUtils.EMPTY, StringUtils.EMPTY );

        for ( IMapProvider mapProvider : MapProviderManager.getMapProvidersList( ) )
        {
            refList.add( mapProvider.toRefItem( ) );
        }

        return refList;
    }

    /**
     * Builds the {@link ReferenceList} of all available edit mode
     * 
     * @return the {@link ReferenceList}
     */
    public ReferenceList getEditModeRefList( )
    {
        String strEditModeListProperty = AppPropertiesService.getProperty( PARAMETER_EDIT_MODE_LIST );
        ReferenceList refList = new ReferenceList( );
        refList.addItem( StringUtils.EMPTY, StringUtils.EMPTY );

        if ( strEditModeListProperty != null )
        {
            String [ ] strEditModeListPropertyArray = strEditModeListProperty.split( "," );

            for ( int i = 0; i < strEditModeListPropertyArray.length; i++ )
            {
                refList.addItem( strEditModeListPropertyArray [i], strEditModeListPropertyArray [i] );
            }
        }

        return refList;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        String fieldName = StringUtils.EMPTY;

        if ( response.getField( ) != null )
        {
            fieldName = ObjectUtils.toString( response.getField( ).getTitle( ) );
        }

        return fieldName + GenericAttributesUtils.CONSTANT_EQUAL + response.getResponseValue( );
    }

    // PRIVATE METHODS

    /**
     * Builds the field.
     * 
     * @param entry
     *            The entry
     * @param strFieldTitle
     *            the str field title
     * @return the field
     */
    private Field buildField( Entry entry, String strFieldTitle, String strFieldValue )
    {
        Field field = new Field( );
        field.setTitle( strFieldTitle );
        field.setValue( strFieldValue );
        field.setParentEntry( entry );

        return field;
    }

    /**
     * Builds the field map provider.
     * 
     * @param entry
     *            The entry
     * @param strMapProvider
     *            the map provider
     * @return the field
     */
    private Field buildFieldMapProvider( Entry entry, String strMapProvider )
    {
        Field fieldMapProvider = new Field( );
        fieldMapProvider.setTitle( CONSTANT_PROVIDER );

        if ( StringUtils.isNotBlank( strMapProvider ) )
        {
            String strTrimedMapProvider = strMapProvider.trim( );
            fieldMapProvider.setValue( strTrimedMapProvider );
            entry.setMapProvider( MapProviderManager.getMapProvider( strTrimedMapProvider ) );
        }
        else
        {
            fieldMapProvider.setValue( StringUtils.EMPTY );
        }

        fieldMapProvider.setParentEntry( entry );

        return fieldMapProvider;
    }

    public void getAdditionalAddressEntry( Entry entry, Locale locale, boolean update )
    {
        entry.setTitle( I18nService.getLocalizedString( "form.additional_address.title", locale ) );
        entry.setHelpMessage( "" );
        entry.setComment( "" );
        entry.setCSSClass( "" );
        entry.setErrorMessage( "" );

        if ( entry.getFields( ) == null )
        {
            ArrayList<Field> listFields = new ArrayList<Field>( );
            Field field = new Field( );
            field.setTitle( "additionalAddress" );
            field.setValue( "" );
            // FieldHome.create(field, pluginDirectory);
            listFields.add( field );
            entry.setFields( listFields );
        }

        entry.getFields( ).get( 0 ).setWidth( 0 );
        entry.getFields( ).get( 0 ).setMaxSizeEnter( 250 );

        if ( update )
        {
            EntryHome.update( entry );

            if ( entry.getFields( ) != null )
            {
                for ( Field field : entry.getFields( ) )
                {
                    field.setParentEntry( entry );
                    FieldHome.update( field );
                }
            }
        }
        else
        {
            EntryHome.create( entry );

            if ( entry.getFields( ) != null )
            {
                for ( Field field : entry.getFields( ) )
                {
                    field.setParentEntry( entry );
                    FieldHome.create( field );
                }
            }
        }
    }
}
