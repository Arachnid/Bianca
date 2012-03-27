/*
 * Copyright (c) 1998-2010 Caucho Technology -- all rights reserved
 * Copyright (c) 2011-2012 Clever Cloud SAS -- all rights reserved
 *
 * This file is part of Resin(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Resin Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Resin Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resin Open Source; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */
package com.clevercloud.bianca.lib;

import com.clevercloud.bianca.BiancaModuleException;
import com.clevercloud.bianca.annotation.Optional;
import com.clevercloud.bianca.env.ArrayValue;
import com.clevercloud.bianca.env.ArrayValueImpl;
import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.Value;
import com.clevercloud.bianca.module.AbstractBiancaModule;
import com.clevercloud.util.L10N;

import javax.servlet.http.*;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * PHP apache routines.
 */
public class ApacheModule extends AbstractBiancaModule {

   private static final L10N L = new L10N(ApacheModule.class);
   private static final Logger log =
           Logger.getLogger(ApacheModule.class.getName());

   /**
    * Stub for insisting the apache process should terminate.
    */
   public boolean apache_child_terminate() {
      return false;
   }

   // TODO: apache_get_modules
   // TODO: apache_get_version
   // TODO: apache_getenv
   // TODO: apache_lookup_uri
   /**
    * Gets and sets apache notes
    */
   public String apache_note(Env env,
           String name,
           @Optional Value value) {
      HttpServletRequest req = env.getRequest();

      Object oldValue = req.getAttribute(name);

      if (value.isset()) {
         req.setAttribute(name, value.toString());
      }

      if (oldValue != null) {
         return oldValue.toString();
      } else {
         return null;
      }
   }

   /**
    * Returns all the request headers
    */
   public Value apache_request_headers(Env env) {
      HttpServletRequest req = env.getRequest();

      ArrayValue result = new ArrayValueImpl();

      Enumeration e = req.getHeaderNames();

      while (e.hasMoreElements()) {
         String key = (String) e.nextElement();

         result.put(env.createString(key), env.createString(req.getHeader(key)));
      }

      return result;
   }

   // TODO: apache_response_headers
   /**
    * Stub for resetting the output timeout.
    */
   public boolean apache_reset_timeout() {
      return false;
   }

   // TODO: apache_setenv
   // TODO: ascii2ebcdic
   // TODO: ebcdic2ascii
   /**
    * Returns all the request headers
    */
   public Value getallheaders(Env env) {
      return apache_request_headers(env);
   }

   /**
    * Include request.
    */
   public boolean virtual(Env env, String url) {
      try {
         HttpServletRequest req = env.getRequest();
         HttpServletResponse res = env.getResponse();

         // TODO: need to put the output, so the included stream gets the
         // buffer, too
         env.getOut().flushBuffer();

         req.getRequestDispatcher(url).include(req, res);

         return true;
      } catch (RuntimeException e) {
         throw e;
      } catch (Exception e) {
         throw new BiancaModuleException(e);
      }
   }
}
