package org.hupo.psi.mi.psicquic.server;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # PsicquicPortImpl: implementation of PSICQUIC 1.1 SOAP service 
 #
 #=========================================================================== */

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import java.io.InputStream;

import javax.jws.WebService;

//import javax.xml.ws.handler.MessageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hupo.psi.mi.psq.*;
import org.hupo.psi.mi.psicquic.*;

import org.hupo.psi.mi.psicquic.server.index.*;
import org.hupo.psi.mi.psicquic.server.store.*;

import org.hupo.psi.mi.psicquic.util.JsonContext;

import javax.annotation.*;

@WebService( name = "PsicquicService", 
             targetNamespace = "http://psi.hupo.org/mi/psicquic",
             serviceName = "PsicquicService",
             portName = "IndexBasedPsicquicServicePort",
             endpointInterface = "org.hupo.psi.mi.psq.PsqPort")
//             wsdlLocation = "/WEB-INF/wsdl/psicquic11.wsdl")

public class PsicquicPortImpl implements PsqPort {
    
    org.hupo.psi.mi.psq.ObjectFactory psqOF =
        new org.hupo.psi.mi.psq.ObjectFactory();
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    /*
    Index index = null;

    public void setIndex( Index index ){
        this.index = index;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    RecordDao  rdao = null;

    public void setRecordDao( RecordDao dao ){
        this.rdao= dao;
    }
    */

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    PsqContext psqContext;
    
    public void setPsqContext( PsqContext context ){
        psqContext = context;
    }
    

    //--------------------------------------------------------------------------

    private void initialize() {
        initialize( false );
    }
    
    //--------------------------------------------------------------------------
    
    private void initialize( boolean force) {

	Log log = LogFactory.getLog( this.getClass() );
	log.info( " psqContext=" + psqContext );
	
        /*
        if ( psqContext.getJsonConfig() == null || force ) {
            log.info( " initilizing psq context" );

            try {
                psqContext.readJsonConfigDef();
            } catch ( Exception e ){
                log.info( "JsonConfig reading error" );
            }            
        }
        */

    }

    //==========================================================================
    // WEB SERVICE OPERATIONS
    //=======================
    
    public QueryResponse getByInteractor( DbRef dbRef,
                                          RequestInfo infoRequest )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException {
        
        throw new NotSupportedMethodException( "", null );
    };
    
    //--------------------------------------------------------------------------

    public QueryResponse getByQuery( String query,
                                     RequestInfo infoRequest )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "PsqPortImpl: getByQuery: context =" + psqContext);
        log.info( "PsqPortImpl: getByQuery: q=" + query );


        /*        
        // MIQL Extension(s)
        //------------------

        // psq-facet-field:<field> - faceting field
        //-----------------------------------------

        String facetField = null;

        if( q != null && q.indexOf( " psq-facet-field:" ) > 0 ){

            int vtStart = q.indexOf( " psq-facet-field:" );
            int vtStop = q.indexOf( ' ', vtStart + 6 );

            if( vtStop > vtStart ){
                viewType = q.substring( vtStart+6, vtStop );
            } else {
                viewType = q.substring( vtStart + 6 );
            }

            String nq = q.substring( 0, vtStart + 1 );
            if( vtStop > vtStart ){
                nq = nq + q.substring( vtStop );
            }

            q = nq;
        }
        
        // psq-view:<view type> - return type
        //-----------------------------------

        String viewType = null;
        
        if( q != null && q.indexOf( " psq-view:" ) > 0 ){
        
            int vtStart = q.indexOf( " psq-view:" );
            int vtStop = q.indexOf( ' ', vtStart + 6 ); 
            
            if( vtStop > vtStart ){
                viewType = q.substring(vtStart+6, vtStop);
            } else {
                viewType = q.substring(vtStart+6);
            }

            String nq = q.substring(0, vtStart + 1);
            if( vtStop > vtStart ){
                nq = nq + q.substring( vtStop );
            }
            
            q = nq;
        }
        
        // psq-client:<ip>|'watchdog' - client info
        //------------------------------------------

        String client = null;
        
        if( q != null && q.indexOf( " psq-client:" ) > 0 ){
        
            int vtStart = q.indexOf( " psq-client:" );
            int vtStop = q.indexOf( ' ', vtStart + 6 ); 
            
            if( vtStop > vtStart ){
                viewType = q.substring( vtStart+6, vtStop );
            } else {
                viewType = q.substring( vtStart+6 );
            }

            String nq = q.substring( 0, vtStart + 1 );
            if( vtStop > vtStart ){
                nq = nq + q.substring( vtStop );
            }
            
            q = nq;
        }
        */
        
        if( infoRequest != null ){              
            log.info( "                         FR=" 
                      + infoRequest.getFirstResult() );
            log.info( "                         BS=" 
                      + infoRequest.getBlockSize() );
        }
        
        org.hupo.psi.mi.psicquic.server.index.ResultSet 
            rs = psqContext.getActiveIndex().query( query );
        
        QueryResponse qr = psqOF.createQueryResponse();
        qr.setResultSet( psqOF.createResultSet() );
        
        String mitab="";
	log.info( "getByQuery: rs="+ rs); 
        for( Iterator i = rs.getResultList().iterator(); i.hasNext(); ){
            Map in = (Map) i.next();
            log.info( "getByQuery: in="+ in);

            String recId = (String) in.get("recId");

            String drecord =  psqContext.getActiveStore()
                .getRecord( recId ,"psi-mi/tab25" );
        
            log.info( " SolrDoc: recId=" + recId + " :: "  + drecord );
            mitab += drecord + "\n";
        }
        qr.getResultSet().setMitab( mitab );     

        return qr;
    };
    
    //--------------------------------------------------------------------------

    public QueryResponse getByInteractorList( List<DbRef> dbRef,
                                              RequestInfo infoRequest,
                                              String operand )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException {
        
        throw new NotSupportedMethodException( "", null );
    };

    //--------------------------------------------------------------------------
    
    public QueryResponse getByInteraction( DbRef dbRef,
                                           RequestInfo infoRequest )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException {
        
        throw new NotSupportedMethodException( "", null );
    };

    //--------------------------------------------------------------------------
    
    public QueryResponse getByInteractionList( List<DbRef> dbRef,
                                               RequestInfo infoRequest )
        throws NotSupportedMethodException, 
               NotSupportedTypeException, 
               PsicquicServiceException{
        throw new NotSupportedMethodException( "", null );
    };


    //==========================================================================
    // META DATA
    //==========

    public List<String> getSupportedReturnTypes(){
        
        return (List<String>) ((Map) ((Map) psqContext.getJsonConfig()
                                      .get( "service" )).get( "soap" ))
            .get( "supported-return-type" );
    };
    
    //--------------------------------------------------------------------------
    
    public String getVersion(){
        
        return (String) ((Map) ((Map) psqContext.getJsonConfig()
                                .get( "service" )).get( "soap" ))
            .get( "version" );
    };

    //--------------------------------------------------------------------------
    
    public List<String> getSupportedDbAcs(){
        
        return (List<String>) ((Map) ((Map) psqContext.getJsonConfig()
                                      .get( "service" )).get( "soap" ))
            .get( "supported-db-ac" );
    };
    
    //--------------------------------------------------------------------------

    public String getProperty( String property ){     
        
        return (String) ((Map) ((Map) ((Map) psqContext.getJsonConfig()
                                       .get( "service" )) .get( "soap" )) 
                         .get( "properties" ))
            .get( property );
    };
    
    //--------------------------------------------------------------------------
    
    public List<Property> getProperties(){
        
        Map propmap = (Map) ((Map) ((Map) psqContext.getJsonConfig()
                                    .get( "service" )).get( "soap" ))
            .get( "properties" );
        
        List<Property> pl = new ArrayList<Property>();
        
        for( Iterator pi = propmap.entrySet().iterator(); pi.hasNext(); ){
            Map.Entry me = (Map.Entry) pi.next();
            
            Property p = psqOF.createProperty();
        
            p.setKey( (String) me.getKey() );
            p.setValue( (String) me.getValue() );
            pl.add( p );
        }
        return pl;
    };
}
