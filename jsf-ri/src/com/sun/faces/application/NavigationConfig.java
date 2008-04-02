/*
 * $Id: NavigationConfig.java,v 1.3 2003/04/04 18:42:54 rkitain Exp $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.application;

import com.sun.faces.util.Util;

import org.apache.commons.digester.Digester;

import org.mozilla.util.Assert;
import org.mozilla.util.Debug;
import org.mozilla.util.Log;
import org.mozilla.util.ParameterCheck;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import javax.servlet.ServletContext;

/**
 *
 *
 */

public class NavigationConfig {

//
// Protected Constants
//

//
// Class Variables
//

//
// Instance Variables
//

//
// Ivars used during parsing
//

    private ArrayList navigationList = null;

    /**
    * Used during Digester parsing.  Accrues AttributeStruct instances
    * during createAndAccrueRenderer() calls.
    */

    private Digester parse_digester = null;

//
// Ivars used during actual client lifetime
//

// Relationship Instance Variables


//
// Constructors and Initializers    
//

    public NavigationConfig(ServletContext servletContext) {
        super();
        parse_digester = initConfig();
        navigationList = new ArrayList();
        loadProperties(servletContext);
    }

//
// Class methods
//

//
// General Methods
//

    private void loadProperties(ServletContext servletContext) {

        String fileName = "/WEB-INF/NavigationConfig.xml";
        InputStream input = null;
	try {
            input = servletContext.getResourceAsStream(fileName);
	} catch (Throwable t) {
	    throw new RuntimeException("Error Opening File:"+fileName);
	}
        try {
            parse_digester.push(this);
            parse_digester.parse(input);
        } catch (Throwable t) {
	    if (null != t) {
		t.printStackTrace();
	    }
            throw new IllegalStateException(
                "Unable to parse file:"+t.getMessage());
        }
    }

    private Digester initConfig() {
        Digester parser = new Digester();

        parser.setNamespaceAware(true);
        parser.setValidating(false);
        parser.addCallMethod("*/rule", "createAndAccrueNavigation", 4);
        parser.addCallParam("*/rule/page", 0);
        parser.addCallParam("*/rule/action", 1);
        parser.addCallParam("*/rule/outcome", 2);
        parser.addCallParam("*/rule/select", 3);

        return parser;
    }

    public void createAndAccrueNavigation(String page, 
        String action, String outcome, String select) {

        Navigation navigation = new Navigation();
        navigation.page= page;
        navigation.action = action;
        navigation.outcome = outcome;
        navigation.select = select;

        navigationList.add(navigation);
    }

    public ArrayList getNavigationList() {
       return navigationList;
    }

    public String getTreeIdByPageOutcome(String treeId, String outcome) {
        String returnTree = null;
        for (int i=0; i<navigationList.size(); i++) {
            Navigation navigation = (Navigation)navigationList.get(i);
            if (null != navigation.outcome && null != navigation.page &&
                null == navigation.action) {
                if (navigation.outcome.equals(outcome)) {
                    returnTree = navigation.select;
                    break;
                }
            }
        }
        return returnTree;
    }

    public String getTreeIdByPageActionOutcome(String treeId, String actionRef, String outcome) {
        String returnTree = null;
        for (int i=0; i<navigationList.size(); i++) {
            Navigation navigation = (Navigation)navigationList.get(i);
            if (null == navigation.page || null == navigation.action || 
                null == navigation.outcome) {
                continue;
            } else if (navigation.page.equals(treeId) && navigation.action.equals(actionRef) &&
                navigation.outcome.equals(outcome)) {
                returnTree = navigation.select;
                break;
            }
        }
        return returnTree;
    }

    class Navigation extends Object {
	public String page = null;
	public String action = null;
	public String outcome = null;
	public String select = null;
    }    
} 

