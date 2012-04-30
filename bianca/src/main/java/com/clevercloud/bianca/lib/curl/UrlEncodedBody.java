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
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Nam Nguyen
 */
package com.clevercloud.bianca.lib.curl;

import com.clevercloud.bianca.annotation.Optional;
import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.StringValue;
import com.clevercloud.bianca.env.Value;

import java.io.IOException;
import java.io.OutputStream;

public class UrlEncodedBody extends PostBody {

   private StringValue _body;
   private int _length;

   @Override
   protected boolean init(Env env, Value body) {
      _body = body.toStringValue(env);
      _length = _body.length();

      return true;
   }

   @Override
   public String getContentType(@Optional String contentType) {
      if (contentType != null) {
         return contentType;
      } else {
         return "application/x-www-form-urlencoded";
      }
   }

   @Override
   public long getContentLength() {
      return (long) _length;
   }

   @Override
   public void writeTo(Env env, OutputStream os)
      throws IOException {
      for (int i = 0; i < _length; i++) {
         os.write(_body.charAt(i));
      }
   }
}