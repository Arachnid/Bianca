/*
 * Copyright (c) 1998-2010 Caucho Technology -- all rights reserved
 * Copyright (c) 2011-2012 Clever Cloud SAS -- all rights reserved
 *
 * This file is part of Bianca(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Bianca Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Bianca Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bianca Open Source; if not, write to the
 *   Free SoftwareFoundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Sam
 */
package com.clevercloud.bianca.lib.db;

import com.clevercloud.bianca.Location;
import com.clevercloud.bianca.env.BiancaLanguageException;
import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.StringValue;
import com.clevercloud.bianca.env.Value;

public class PDOException
   extends BiancaLanguageException {

   private final String _code;
   private final String _message;
   private Location _location;

   public PDOException(String code, String message) {
      super(StringValue.create("PDOException"));

      _code = code;
      _message = "SQLSTATE[" + code + "]: " + message;

      _location = Env.getInstance().getLocation();
   }

   public String getCode() {
      return _code;
   }

   @Override
   public Location getLocation(Env env) {
      return _location;
   }

   @Override
   public String getMessage() {
      return _message;
   }

   @Override
   public String getMessage(Env env) {
      return getMessage();
   }

   /**
    * Converts the exception to a Value.
    */
   @Override
   public Value toValue(Env env) {
      Value e = env.createException("PDOException", _message);

      return e;
   }
}
