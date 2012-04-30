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
 * @author Marc-Antoine Perennou <Marc-Antoine@Perennou.com>
 */
package com.clevercloud.bianca.lib.i18n;

import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.StringValue;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CoderResult;

public class Big5Encoder
   extends GenericEncoder {

   public Big5Encoder(String charsetName) {
      super(charsetName);
   }

   @Override
   public boolean isEncodable(Env env, StringValue str) {
      int len = str.length();

      for (int i = 0; i < len; i++) {
         char ch = str.charAt(i);

         if (ch == '\u20AC') // euro
         {
            continue;
         } else if (!_encoder.canEncode(str.charAt(i))) {
            return false;
         }
      }

      return false;
   }

   @Override
   protected boolean fill(StringValue sb, CharBuffer in,
                          ByteBuffer out, CoderResult coder) {
      int len = out.position();

      if (len > 0) {
         int offset = out.arrayOffset();

         sb.append(new String(out.array()), offset, offset + len);
      }

      if (coder.isMalformed() || coder.isUnmappable()) {
         int errorIndex = in.position();

         in.position(errorIndex + 1);

         if (in.get(errorIndex) == '\u20AC') {
            // euro
            sb.append('\u00a3');
            sb.append('\u00e1');
         } else if (_isIgnore) {
         } else if (_replacement != null) {
            sb.append(_replacement);
         } else if (_isReplaceUnicode) {
            sb.append("U+" + Integer.toHexString(in.get(errorIndex)));
         } else {
            return false;
         }
      }

      return true;
   }
}